/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicgame;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author patricia
 */
public class Jogo extends Thread {

    private DatagramPacket sendPacket;
    private DatagramSocket sendSocket;
    private Desafio desafio;
    private LocalDateTime data;
    private BD bd;

    public Jogo(LocalDateTime data, Desafio b, BD bd) {
        this.desafio = b;
        this.data = data;
        this.bd = bd;

    }

    @SuppressWarnings("empty-statement")
    public void run() {
        LocalDateTime agora = LocalDateTime.now();
        PDU resposta;
        DatagramSocket sendSocket;
        byte[] dateSend;
        byte[] label;
        Campo c;
        int numQuestao = 1;

        try {

            while (this.data.isAfter(agora)) {
                agora = LocalDateTime.now();
            }

            for (Utilizador u : this.desafio.getUsers().values()) {
                label = this.desafio.getLabelByUser(u);
                int s = PDU.byteArrayToInt(label);
                resposta = new PDU(s, (byte) 0);
                c = new Campo(07, this.desafio.getNome().getBytes());
                resposta.addCampo(c);
                byte[] q = {(byte) numQuestao};

                c = new Campo(10, q);
                resposta.addCampo(c);
                c = new Campo(11, this.desafio.getPergunta(numQuestao - 1).getPergunta().getBytes());
                resposta.addCampo(c);
                int tam = this.desafio.getNumeroRespostas(numQuestao - 1);
                int i;
                for (i = 1; i <= 3; i++) {
                    q = new byte[]{(byte) i};
                    c = new Campo(12, q);
                    resposta.addCampo(c);
                    c = new Campo(13, this.desafio.getResposta(numQuestao - 1, i - 1).getBytes());
                    resposta.addCampo(c);
                }
                responde(resposta, u.getIp(), u.getPort());

                ////////////////// END REPLY //////////////////
                sendImage(desafio.getNome(), s, numQuestao, u.getIp(), u.getPort());

                sendMusic(desafio.getNome(), s, numQuestao, u.getIp(), u.getPort());

            }
        } catch (IOException ex) {
            Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void sendImage(String nome, int s, int numQuestao, InetAddress add, int port) throws IOException {
        Campo c;
        System.out.println("IMAGEM");
        int i;
        byte[] dateSend, m = this.desafio.getImagemQuestao(this.bd.getPathImage(), numQuestao - 1);

        int nPackets = m.length / 49152;
        int lastPackBytes = m.length % 49152;

        PDU image;

        byte[] p;
        for (i = 0; i < nPackets; i++) {

            image = new PDU(s, (byte) 0);
            c = new Campo(7, desafio.getNome().getBytes());
            image.addCampo(c);
            c = new Campo(10, PDU.intToByteArray(numQuestao));
            image.addCampo(c);
            c = new Campo(17, new byte[]{(byte) (i + 1)});
            image.addCampo(c);
            p = new byte[49152];
            for (int j = 0; j < 49152; j++) {
                p[j] = m[i * 49152 + j];
            }
            image.addCampo(new Campo(16, p));
            image.addCampo(new Campo(254, new byte[]{0}));
            responde(image, add, port);
            System.out.println(i);
        }
        p = new byte[lastPackBytes];
        for (int j = 0; j < lastPackBytes; j++) {
            p[j] = m[i * 49152 + j];
        }
        image = new PDU(s, (byte) 0);
        c = new Campo(7, desafio.getNome().getBytes());
        image.addCampo(c);
        c = new Campo(10, PDU.intToByteArray(numQuestao));
        image.addCampo(c);
        image.addCampo(new Campo(17, new byte[]{(byte) (i + 1)}));
        image.addCampo(new Campo(16, p));
        image.addCampo(new Campo(250, new byte[]{0}));  ////////////////////////////// last block
        responde(image, add, port);
    }

    public void sendMusic(String nome, int s, int numQuestao, InetAddress add, int port) throws IOException {
        Campo c;
        System.out.println("MUSICA");
        int i;
        byte[] m = this.desafio.getMusicaQuestao(this.bd.getPathMusic(), numQuestao - 1);

        int nPackets = m.length / 49152;
        int lastPackBytes = m.length % 49152;

        PDU music;

        byte[] p;
        for (i = 0; i < nPackets; i++) {

            music = new PDU(s, (byte) 0);
            c = new Campo(7, desafio.getNome().getBytes());
            music.addCampo(c);
            c = new Campo(10, PDU.intToByteArray(numQuestao));
            music.addCampo(c);
            c = new Campo(17, new byte[]{(byte) (i + 1)});
            music.addCampo(c);
            p = new byte[49152];
            for (int j = 0; j < 49152; j++) {
                p[j] = m[i * 49152 + j];
            }
            music.addCampo(new Campo(18, p));
            music.addCampo(new Campo(254, new byte[]{0}));
            responde(music, add, port);
            System.out.println(i);
        }
        p = new byte[lastPackBytes];
        for (int j = 0; j < lastPackBytes; j++) {
            p[j] = m[i * 49152 + j];
        }
        music = new PDU(s, (byte) 0);
        c = new Campo(7, desafio.getNome().getBytes());
        music.addCampo(c);
        c = new Campo(10, PDU.intToByteArray(numQuestao));
        music.addCampo(c);
        music.addCampo(new Campo(17, new byte[]{(byte) (i + 1)}));
        music.addCampo(new Campo(18, p));
        music.addCampo(new Campo(250, new byte[]{0}));  ////////////////////////////// last block
        responde(music, add, port);
    }

    public void responde(PDU pack, InetAddress add, int port) {
        try {
            sendPacket = new DatagramPacket(pack.getBytes(), pack.getBytes().length, add, port);
            sendSocket = new DatagramSocket();
            sendSocket.send(sendPacket);
        } catch (IOException ex) {
            System.out.println("Responde");
        }
    }
    //numQuestao++;
}

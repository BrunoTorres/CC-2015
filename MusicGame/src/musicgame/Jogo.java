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
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author patricia
 */
public class Jogo extends Thread {

    private static final int OK = 0;
    private static final int FIM = 250;
    private static final int ERRO = 255;
    private static final int CONTINUA = 254;
    private static final int NOME = 1;
    private static final int ALCUNHA = 2;
    private static final int PASSWORD = 3;
    private static final int DATA = 4;
    private static final int HORA = 5;
    private static final int ESCOLHA = 6;
    private static final int DESAFIO = 7;
    private static final int NQUESTAO = 10;
    private static final int QUESTAO = 11;
    private static final int NRESPOSTA = 12;
    private static final int RESPOSTA = 13;
    private static final int CERTA = 14;
    private static final int PONTOS = 15;
    private static final int IMAGEM = 16;
    private static final int BLOCO = 17;
    private static final int AUDIO = 18;
    private static final int SCORE = 20;
    private static final int TIME = 21;
    private static final int BLOCOS = 21;

    private DatagramPacket sendPacket;
    private DatagramSocket sendSocket;
    private Desafio desafio;
    private LocalDateTime data;
    private BD bd;
    private int numQuestao;
    private Utilizador u;
    private boolean init;

    public Jogo(Utilizador user, LocalDateTime data, Desafio b, BD bd, int nQ, boolean i) {
        this.desafio = b;
        this.data = data;
        this.bd = bd;
        this.numQuestao = nQ;
        this.u = user;
        this.init = i;

    }

    @SuppressWarnings("empty-statement")
    public void run() {
        LocalDateTime agora = LocalDateTime.now();
        PDU resposta;
        byte[] dateSend;
        byte[] label;
        Campo c;

        try {
            if (this.init) {
                while (this.data.isAfter(agora)) {
                    agora = LocalDateTime.now();

                    /// vai ter aqui cenas para recber pacote de delete//
                }
            }
            if (this.desafio.getStatus() == false) {
                this.desafio.setStatus(true);
                if (this.desafio.getUsers().size() < 2) {
                    Utilizador us = this.u;

                    PDU reply;
                    reply = new PDU(PDU.byteArrayToInt(this.desafio.getLabelByUser(us)), (byte) 0);
                    reply.addCampo(new Campo(7, this.desafio.getNome().getBytes()));
                    reply.addCampo(new Campo(255, "Número insuficiente de jogadores!".getBytes()));
                    responde(reply, us.getIp(), us.getPort());
                } else {
                    this.desafio = this.bd.getDesafio(this.desafio.getNome());

                    if (this.desafio.getUsers().containsKey(u.getAlcunha())) {
                        if (numQuestao == 1) {
                            u.initPontuacao();
                        }
                        label = this.desafio.getLabelByUser(u);
                        int s = PDU.byteArrayToInt(label);
                        resposta = new PDU(s, (byte) 0);
                        c = new Campo(DESAFIO, this.desafio.getNome().getBytes());
                        resposta.addCampo(c);
                        byte[] q = {(byte) numQuestao};

                        c = new Campo(NQUESTAO, q);
                        resposta.addCampo(c);
                        c = new Campo(QUESTAO, this.desafio.getPergunta(numQuestao - 1).getPergunta().getBytes());
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
                        //System.out.println("vai enviar imagem");
                        sendImage(desafio.getNome(), s, numQuestao, u.getIp(), u.getPort());

                        //System.out.println("vai enviar musica");
                        sendMusic(desafio.getNome(), s, numQuestao, u.getIp(), u.getPort());
                    }
                }
            } else {
                Utilizador us = this.u;
                PDU reply;
                reply = new PDU(PDU.byteArrayToInt(this.desafio.getLabelByUser(us)), (byte) 0);
                reply.addCampo(new Campo(7, this.desafio.getNome().getBytes()));
                reply.addCampo(new Campo(255, "Desafio eliminado!".getBytes()));
                responde(reply, us.getIp(), us.getPort());
            }
        } catch (IOException ex) {
            Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void sendImage(String nome, int s, int numQuestao, InetAddress add, int port) throws IOException {
        Campo c;
        int i;
        byte[] m = this.desafio.getImagemQuestao(this.bd.getPathImage(), numQuestao - 1);

        int nPackets = m.length / 49152;
        int lastPackBytes = m.length % 49152;
        PDU image;

        byte[] p;
        for (i = 0; i < nPackets; i++) {
            //System.out.println("Enviou pacote numero: " + i);

            image = new PDU(s, (byte) 0);
            c = new Campo(DESAFIO, desafio.getNome().getBytes());
            image.addCampo(c);
            c = new Campo(NQUESTAO, PDU.intToByteArray(numQuestao));
            image.addCampo(c);
            c = new Campo(BLOCO, new byte[]{(byte) (i + 1)});
            image.addCampo(c);
            p = new byte[49152];
            for (int j = 0; j < 49152; j++) {
                p[j] = m[i * 49152 + j];
            }
            image.addCampo(new Campo(IMAGEM, p));
            image.addCampo(new Campo(CONTINUA, new byte[]{0}));
            responde(image, add, port);
            //System.out.println(i + 1);
        }
        p = new byte[lastPackBytes];
        for (int j = 0; j < lastPackBytes; j++) {
            p[j] = m[i * 49152 + j];
        }
        image = new PDU(s, (byte) 0);
        c = new Campo(DESAFIO, desafio.getNome().getBytes());
        image.addCampo(c);
        c = new Campo(NQUESTAO, PDU.intToByteArray(numQuestao));
        image.addCampo(c);
        image.addCampo(new Campo(BLOCO, new byte[]{(byte) (i + 1)}));
        image.addCampo(new Campo(IMAGEM, p));
        image.addCampo(new Campo(FIM, new byte[]{0}));  ////////////////////////////// last block
        //System.out.println(i + 1);
        responde(image, add, port);
    }

    public void sendMusic(String nome, int s, int numQuestao, InetAddress add, int port) throws IOException {
        Campo c;
        int i;
        byte[] m = this.desafio.getMusicaQuestao(this.bd.getPathMusic(), numQuestao - 1);

        int nPackets = m.length / 49152;
        int lastPackBytes = m.length % 49152;

        PDU music;
        byte[] p;
        for (i = 0; i < nPackets; i++) {
            if (i != 1) {
                music = new PDU(s, (byte) 0);
                c = new Campo(DESAFIO, desafio.getNome().getBytes());
                music.addCampo(c);
                c = new Campo(NQUESTAO, PDU.intToByteArray(numQuestao));
                music.addCampo(c);
                c = new Campo(BLOCO, new byte[]{(byte) (i + 1)});
                music.addCampo(c);
                p = new byte[49152];
                for (int j = 0; j < 49152; j++) {
                    p[j] = m[i * 49152 + j];
                }
                //this.bd.partes.put(i+1, p);  /////////////////////////////////#####################
                music.addCampo(new Campo(AUDIO, p));
                music.addCampo(new Campo(CONTINUA, new byte[]{0}));

                responde(music, add, port);
                // System.out.println(i + 1);

            } else {
                p = new byte[49152];
                for (int j = 0; j < 49152; j++) {
                    p[j] = m[i * 49152 + j];
                }
                // this.bd.partes.put(i+1, p);

            }
        }
        p = new byte[lastPackBytes];
        for (int j = 0; j < lastPackBytes; j++) {
            p[j] = m[i * 49152 + j];
        }
        music = new PDU(s, (byte) 0);
        c = new Campo(DESAFIO, desafio.getNome().getBytes());
        music.addCampo(c);
        c = new Campo(NQUESTAO, PDU.intToByteArray(numQuestao));

        //this.bd.partes.put(i+1, p);
        music.addCampo(c);
        music.addCampo(new Campo(BLOCO, new byte[]{(byte) (i + 1)}));
        music.addCampo(new Campo(AUDIO, p));
        music.addCampo(new Campo(FIM, new byte[]{0}));  ////////////////////////////// last block
        //System.out.println(i + 1);
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

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicgame;

import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author patricia
 */
public class Jogo extends Thread {

    private DatagramPacket sendPacket;
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
        byte[] label;
        Campo c;
        int numQuestao = 1;
        
        
        try {
           
       /*
             while (this.data.isAfter(agora)){
                 agora=LocalDateTime.now();
                 System.out.println("passu");
         
        }
               */
            for (Utilizador u : this.desafio.getUsers().values()) {
                label = this.desafio.getLabelByUser(u);
                int s =  PDU.byteArrayToInt(label);
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
                    c = new Campo(13, this.desafio.getResposta(numQuestao - 1, i-1).getBytes());
                    resposta.addCampo(c);
                }

                c = new Campo(16, this.desafio.getImagemQuestao(this.bd.getPathImage(), numQuestao - 1));

                resposta.addCampo(c);
                c = new Campo(254, "0".getBytes());
                resposta.addCampo(c);
                
                byte[] m = this.desafio.getMusicaQuestao(this.bd.getPathMusic(), numQuestao - 1);
                int nPackets = m.length / 255;
                int lastPackBytes = m.length % 255;
                
                PDU music;
                byte[] p;
                for(i = 0; i < nPackets; i++){
                    music = new PDU(s, (byte) 0);
                    c = new Campo(17, new byte[] {(byte) (i+1)});
                    music.addCampo(c);
                    p = new byte[255];
                    for(int j = 0; j < 255; j++){
                        p[j] = m[i * 255 + j];
                    }
                    music.addCampo(new Campo(18, p));
                    music.addCampo(new Campo(254, new byte[] {0}));
                    //send bloco
                }
                p = new byte[lastPackBytes];
                for(int j = 0; j < lastPackBytes; j++){
                    p[j] = m[i * 255 + j];
                }
                music = new PDU(s, (byte) 0);
                music.addCampo(new Campo(17, new byte[] {(byte) (i+1)}));
                music.addCampo(new Campo(18, p));
                // send last bloco
                

            }
        } catch (IOException ex) {
            Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //numQuestao++;
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicgame;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;

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
        this.bd=bd;

    }
    private short byteArrayToInt(byte[] b) {
        ByteBuffer bb = ByteBuffer.wrap(b);
        return bb.getShort();
    }

    public void run() {
        LocalDateTime agora = LocalDateTime.now();
        PDU resposta;
        byte[] label;
        Campo c;
        int numQuestao=1;
        while (this.data.isBefore(agora));
        for(Utilizador u : this.desafio.getUsers().values()){       
                label=this.desafio.getLabelByUser(u);
                Short s = (short) byteArrayToInt(label);
                resposta = new PDU(s, (byte) 0);
                c = new Campo(07, this.desafio.getNome().getBytes());
                resposta.addCampo(c);
                byte[] q = {(byte)numQuestao};
                c = new Campo(10, q);
                resposta.addCampo(c);
                c = new Campo(11, this.desafio.getPergunta(numQuestao-1).getPergunta().getBytes());
                resposta.addCampo(c);
                int tam=this.desafio.getNumeroRespostas(numQuestao-1);
                int i;
                for(i=1; i<=tam;i++){
                    q = new byte[] {(byte) i};
                    c = new Campo(12, q);
                    resposta.addCampo(c);
                    c = new Campo(13, this.desafio.getResposta(numQuestao-1,i).getBytes());
                    resposta.addCampo(c);                    
                }
                c = new Campo(16, this.desafio.getImagemQuestao(numQuestao-1).getBytes());
                resposta.addCampo(c);
                c = new Campo(254, "0".getBytes());
                resposta.addCampo(c);
                
        }

    }
    
    
    
    //numQuestao++;
    
    
    
    
    
}

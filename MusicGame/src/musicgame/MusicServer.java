package musicgame;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

class MusicServer {

    private static BD bd;
    
    
    
    public static void main(String args[]) throws Exception {
        PDU p = new PDU(0, 0);
        //bd = new BD(p.getMusicPath(),p.getImagesPath()); 
        bd = new BD("C:\\Users\\John\\Documents\\Repos\\CC-2015\\MusicGame\\build\\classes\\musicgame\\musica\\", "C:\\Users\\John\\Documents\\Repos\\CC-2015\\MusicGame\\build\\classes\\musicgame\\imagens\\");
        String passe="123";
        Utilizador u= new Utilizador("patricia","tita", passe.getBytes(), null, -1);
        bd.addUser(u);
        //bd.carregaPerguntas(p.getDesafiosPath().concat("desafio.txt"));
        bd.carregaPerguntas("C:\\Users\\John\\Documents\\Repos\\CC-2015\\MusicGame\\build\\classes\\musicgame\\desafios\\desafio.txt");
        DatagramSocket serverSocket = new DatagramSocket(55555);
        byte[] receiveData = new byte[1024];
        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            Atendimento at = new Atendimento(receivePacket,bd);
            at.start();
        }
    }
}

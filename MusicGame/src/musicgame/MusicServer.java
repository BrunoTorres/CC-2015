package musicgame;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

class MusicServer {

    private static BD bd;
    
    public static void main(String args[]) throws Exception {
        bd = new BD(); 
        String passe="123";
        Utilizador u= new Utilizador("patricia","tita", passe.getBytes(), null, -1);
        bd.addUser(u);
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

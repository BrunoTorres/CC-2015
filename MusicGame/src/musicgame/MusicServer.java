package musicgame;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

class MusicServer {

    public static void main(String args[]) throws Exception {
        DatagramSocket serverSocket = new DatagramSocket(55555);
        byte[] receiveData = new byte[1024];
        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            Atendimento at = new Atendimento(receivePacket);
            at.start();
        }
    }
}

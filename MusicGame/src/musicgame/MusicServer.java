package musicgame;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class MusicServer {

    public static void main(String args[]) throws Exception {
        DatagramSocket serverSocket = new DatagramSocket(55555);
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];

        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            
            Atendimento at = new Atendimento(receivePacket);
            at.start();

              // DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
        }
    }
}

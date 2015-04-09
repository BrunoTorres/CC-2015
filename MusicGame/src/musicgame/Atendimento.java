package musicgame;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bruno Pereira
 */
public class Atendimento extends Thread {

    private DatagramPacket receivePacket;
    private DatagramSocket sendSocket;
    private DatagramPacket sendPacket;
    byte[] receiveData;
    byte[] sendData;

    public Atendimento(DatagramPacket so) {
        this.receivePacket = so;
        this.sendSocket = null;
        this.sendPacket = null;
        this.receiveData= new byte[1024];
        this.sendData= new byte[1024];

    }

    @Override
    public void run() {
        try {

            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            int tam = receivePacket.getLength();
            receivePacket.setLength(receivePacket.getLength());
            byte[] res = receivePacket.getData();
            byte[] data = new byte[tam];
            System.arraycopy(res, 0, data, 0, tam);
                
            System.out.println("RECEBIDOS: " + receivePacket.getLength());
            for(int i = 0; i < tam; i++){
                System.out.print(data[i] + "|");
            }
            System.out.println();
            PDU r = new PDU(data);
            sendPacket = new DatagramPacket(data, data.length, IPAddress, port);
            System.out.println(r.toString());
            sendSocket = new DatagramSocket();
            sendSocket.send(sendPacket);
            //String sentence = new String(
		//		    receivePacket.getData(), 0, receivePacket.getLength());
               //System.out.println("FROM Cliente:" + sentence);
            /*if (sentence.equals("01")) {
                String frase = "OK";
                //String capitalizedSentence = sentence.toUpperCase();
                //sendData = capitalizedSentence.getBytes();
                sendData = frase.getBytes();

                
                
                sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                sendSocket.send(sendPacket);

            }*/

        } catch (Exception e) {
            System.out.println(e.toString());

        }

    }
}

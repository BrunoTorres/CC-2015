package musicgame;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
    private BD bd;
    byte[] receiveData;
    byte[] sendData;

    public Atendimento(DatagramPacket so, BD b) {
        this.receivePacket = so;
        this.sendSocket = null;
        this.sendPacket = null;
        this.bd = b;
        this.receiveData = new byte[1024];
        this.sendData = new byte[1024];

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
            sendSocket = new DatagramSocket();
            /*for (byte b : data) {
             System.out.print(b + "|");
             }*/
            analisaPacote(data, IPAddress, port);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    private void analisaPacote(byte[] data, InetAddress add, int port) {
        PDU reply;
        switch (data[4]) {
            case 0:
                System.out.println("Reply");
                break;
            case 1:
                System.out.println("Hello");
                reply = new PDU((short) 12, (byte) 0);
                responde(reply, add, port);
                break;
            case 2:
                System.out.println("Register");
                reply = new PDU((short) 12, (byte) 0);
                responde(reply, add, port);
                break;
            case 3:
                System.out.println("Login");
                processaLogin(data);
                break;
            case 4:
                System.out.println("Logout");
                reply = new PDU((short) 12, (byte) 0);
                responde(reply, add, port);
                break;
            case 5:
                System.out.println("Quit");
                reply = new PDU((short) 12, (byte) 0);
                responde(reply, add, port);
                break;
            case 6:
                System.out.println("End");
                break;
            case 7:
                System.out.println("List Challenges");
                break;
            case 8:
                System.out.println("Make challenge");
                break;
            case 9:
                System.out.println("Accept challenge");
                reply = new PDU((short) 12, (byte) 0);
                responde(reply, add, port);
                break;
            case 10:
                System.out.println("Delete challenge");
                break;
            case 11:
                System.out.println("Answer");
                break;
            case 12:
                System.out.println("Retransmit");
                break;
            case 13:
                System.out.println("List ranking");
                break;
            default:
                break;
        }

    }

    private void responde(PDU pack, InetAddress add, int port) {
        try {
            byte[] data = pack.getBytes();
            sendPacket = new DatagramPacket(data, data.length, add, port);
            sendSocket.send(sendPacket);
        } catch (IOException ex) {
            Logger.getLogger(Atendimento.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void processaLogin(byte[] data) {
        PDU pacote = new PDU(data);
        String alc = pacote.getCampo(0).getValor();
        Utilizador u = new Utilizador();
        try {
            u = bd.getUser(alc);
            byte[] pass = pacote.getCampo(1).getValor().getBytes();
        } catch (UserInexistenteException ex) {
            //pacote de erro
        }

    }
}

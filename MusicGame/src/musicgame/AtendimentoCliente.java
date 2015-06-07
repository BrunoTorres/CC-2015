package musicgame;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class AtendimentoCliente extends Thread {
    
    private BD bd;
    private int portaUDP;
    
    /**
     *
     * @param bd
     * @param porta
     */
    public AtendimentoCliente(BD bd, int porta){        
        this.bd = bd;
        this.portaUDP = porta;
    }

    @Override
    public void run() {
        try {
            DatagramSocket serverSocket = new DatagramSocket(this.portaUDP);
            byte[] receiveData = new byte[1024];
            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                InteracaoCliente at = new InteracaoCliente(receivePacket, bd);
                at.start();
            }
        } catch (SocketException ex) {
            System.out.println("Falha: Socket Tcp");
        } catch (IOException ex) {
            System.out.println("Falha");
        } catch (UserInexistenteException ex) {
            System.out.println("Falha: User");
        }
    }

}

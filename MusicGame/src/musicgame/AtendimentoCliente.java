package musicgame;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AtendimentoCliente extends Thread {
    
    private BD bd;
    private int portaUDP;
    
    public AtendimentoCliente(BD bd, int porta){        
        System.out.println("Construtor atenCliente");
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
            Logger.getLogger(AtendimentoCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AtendimentoCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UserInexistenteException ex) {
            Logger.getLogger(AtendimentoCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

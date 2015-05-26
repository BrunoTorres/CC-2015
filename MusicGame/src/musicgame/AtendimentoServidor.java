package musicgame;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AtendimentoServidor extends Thread {

    public static final int INFO = 14;
    public static final int IP = 30;
    public static final int PORTA = 31;
    public static final int REGISTASV = 32;
    public static final int REGISTADESAFIO = 33;
    public static final int LISTADESAFIOS = 34;
    public static final int ACEITADESAFIO = 35;
    public static final int RESULTADOS = 36;
    public static final int RANKINGLOCAL = 37;
    public static final int REGISTASVSEMRESPOSTA = 38;
    public static final int LISTADESVS = 39;

    private BD bd;
    private int portaTCP; // EEUUUUUUU
    private String ipServer; // Servidor externo
    private int portaTCP2; // Servidor externo
    private Socket s;

    public AtendimentoServidor(BD bd, int porta) {
        this.bd = bd;
        this.portaTCP = porta;
        
        this.ipServer = null;
    }

    public AtendimentoServidor(BD bd, int porta, String sv, int porta2) throws IOException {
        this.bd = bd;
        this.portaTCP = porta;
        this.ipServer = sv;
        this.portaTCP2 = porta2;
        
        this.s = new Socket(InetAddress.getByName(sv), porta2);
    }

    @Override
    public void run() {
        try {
            if(ipServer != null)
                registaServidor();
            
            try {
                ServerSocket ss = new ServerSocket(this.portaTCP);
                while (true) {

                    this.s = ss.accept();
                    InteracaoServidor is = new InteracaoServidor(bd, this.s);
                    is.start();
                }
            } catch (IOException ex) {
                Logger.getLogger(AtendimentoServidor.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (UnknownHostException ex) {
            Logger.getLogger(AtendimentoServidor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AtendimentoServidor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void registaServidor() throws UnknownHostException, IOException {
        PDU p = new PDU(0, INFO);
        Campo c = new Campo(REGISTASV, new byte[] {0} );
        p.addCampo(c);
        c = new Campo(IP, InetAddress.getLocalHost().getAddress());
        p.addCampo(c);
        BigInteger bg = BigInteger.valueOf(portaTCP);
        c = new Campo(PORTA, bg.toByteArray());
        p.addCampo(c);
        
        this.bd.registaServidor(InetAddress.getByName(ipServer), portaTCP2);

        ObjectOutputStream o;
        o = new ObjectOutputStream(this.s.getOutputStream());
        o.writeObject(p);
        o.flush();
    }
}

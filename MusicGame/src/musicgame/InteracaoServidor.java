package musicgame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InteracaoServidor extends Thread {

    private BD bd;
    private Socket s;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    public InteracaoServidor(BD bd, Socket s) {
        this.bd = bd;
        this.s = s;
    }

    @Override
    public void run() {

        try {
            this.in = new ObjectInputStream(s.getInputStream());
            this.out = new ObjectOutputStream(s.getOutputStream());
            PDU input;

            while (true) {

                input = (PDU) in.readObject();
                int op = input.getCampo(0).getId();

                switch (op) {
                    case AtendimentoServidor.REGISTASV:
                        registaServidor(input);
                        break;

                    case AtendimentoServidor.REGISTASVSEMRESPOSTA:
                        adicionaSVLocal(input);
                        break;

                    case AtendimentoServidor.LISTADESVS:
                        adicionaSVLocal();
                        break;
                    default:
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(InteracaoServidor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(InteracaoServidor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void registaServidor(PDU p) throws IOException, ClassNotFoundException { // sv principal regista novo sv, devolve-lhe lista dos que conhece e envia aos que conhece
        // o novo sv
        String ip = new String(p.getCampo(1).getBytes());
        ObjectOutputStream o;
        BigInteger bg = new BigInteger(p.getCampo(2).getValor());
        int porta = bg.intValue();
        Socket serv = new Socket(InetAddress.getByName(ip), porta);

        Campo c;

        o = new ObjectOutputStream(serv.getOutputStream());
        o.writeObject(this.bd.getServidores());
        o.flush();

        this.bd.registaServidor(InetAddress.getByName(ip), porta);

        for (InetAddress i : this.bd.getServidores().keySet()) {
            int portaSV = this.bd.getServidores().get(i);
            Socket conhecidos = new Socket(i, portaSV);

            PDU res = new PDU(0, AtendimentoServidor.INFO);
            c = new Campo(AtendimentoServidor.REGISTASVSEMRESPOSTA, new byte[]{0});
            res.addCampo(c);
            c = new Campo(AtendimentoServidor.IP, ip.getBytes()); // SERÁ? ************************************************************
            res.addCampo(c);
            bg = BigInteger.valueOf(porta);
            c = new Campo(AtendimentoServidor.PORTA, bg.toByteArray());
            res.addCampo(c);
            o = new ObjectOutputStream(conhecidos.getOutputStream());
            o.writeObject(res);
            o.flush();
        }

///////////////////////////////////////TODOS
    }

    private void adicionaSVLocal() throws IOException, ClassNotFoundException { // novo servidor adiciona lista de svs que o principal conhece
        ServerSocket ss = new ServerSocket(this.s.getLocalPort());
        Socket s2 = ss.accept();
        ObjectInputStream in2 = new ObjectInputStream(s2.getInputStream());

        HashMap<InetAddress, Integer> svs = (HashMap<InetAddress, Integer>) in2.readObject();
        this.bd.registaServidores(svs);
    }

    private void adicionaSVLocal(PDU p) throws UnknownHostException {
        String ip = new String(p.getCampo(1).getBytes());
        BigInteger bg = new BigInteger(p.getCampo(2).getValor());
        int porta = bg.intValue();
        this.bd.getServidores().put(InetAddress.getByName(ip), porta);
        
        sendDesafios(ip, porta);
        sendRanking(ip, porta);
    }

    private void sendDesafios(String ip, int porta) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void sendRanking(String ip, int porta) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

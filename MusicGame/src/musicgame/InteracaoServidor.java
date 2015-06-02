package musicgame;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
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
                    case MusicClient.QUESTAO: // Recebe um PDU com o nome do desafio e o nº da pergunta e envia a imagem e o audio correspondente
                        String desafio = new String(input.getCampo(1).getValor());
                        BigInteger bg = new BigInteger(input.getCampo(0).getValor());
                        int pergunta = bg.intValue();
                        sendImage(desafio, pergunta);
                        sendAudio(desafio, pergunta);
                        break;
                        
                    case AtendimentoServidor.REQUESTDESAFIO:
                        desafio = new String(input.getCampo(0).getValor());
                        sendDesafio(desafio);
                        break;
                        
                    case AtendimentoServidor.DESAFIO:
                        desafio = new String(input.getCampo(0).getValor());
                        byte[] b = input.getCampo(1).getValor();
                        byte[] ano = {b[0], b[1], b[2]};
                        byte mes = b[3];
                        byte dia = b[4];
                        b = input.getCampo(2).getValor();
                        byte hora = b[0];
                        byte min = b[1];
                        byte seg = b[2];
                        int anos=new BigInteger(ano).intValue();
                       LocalDateTime data = LocalDateTime.of(anos, mes, dia, hora, min, seg);
                        this.bd.addDesafioGlobal(desafio,data);
                        break;

                   // case AtendimentoServidor.REGISTADESAFIO:  
                    //  registaDesafio();// RECEBE um DESAFIO e pede musica e imagem para cada pergunta do desafio
                    //     break;
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
        //ObjectOutputStream o;
        BigInteger bg = new BigInteger(p.getCampo(2).getValor());
        int porta = bg.intValue();
        //Socket serv = new Socket(InetAddress.getByName(ip), porta);

        Campo c;
          //                                                    falta info antes de enviar objecto?!
        // out = new ObjectOutputStream(serv.getOutputStream());
        out.writeObject(this.bd.getServidores());
        out.flush();

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
            out = new ObjectOutputStream(conhecidos.getOutputStream());
            out.writeObject(res);
            out.flush();
        }

///////////////////////////////////////TODOS
    }

// novo servidor adiciona lista de svs que o principal conhece
    private void adicionaSVLocal() throws IOException, ClassNotFoundException {
        ServerSocket ss = new ServerSocket(this.s.getLocalPort());
        Socket s2 = ss.accept();
        ObjectInputStream in2 = new ObjectInputStream(s2.getInputStream());

        HashMap<InetAddress, Integer> svs = (HashMap<InetAddress, Integer>) in2.readObject();
        this.bd.registaServidores(svs);

    }

    //RECEBE o lista de desafios pendentes GLOBAIS///////////////////////////////////////////////////////
    private void adicionaDesafios(PDU input) throws IOException, ClassNotFoundException {
        String ip = new String(input.getCampo(1).getBytes());
        BigInteger bg = new BigInteger(input.getCampo(2).getValor());
        int porta = bg.intValue();

        ServerSocket ss = new ServerSocket(this.s.getLocalPort());
        Socket s2 = ss.accept();
        ObjectInputStream in2 = new ObjectInputStream(s2.getInputStream());

        this.in = new ObjectInputStream(s2.getInputStream());

        HashMap<String, LocalDateTime> des = (HashMap<String, LocalDateTime>) in.readObject();

        this.bd.addDesafiosGlobais(des, InetAddress.getByName(ip), porta);

    }

    private void adicionaRanking() throws IOException, ClassNotFoundException {
        ServerSocket ss = new ServerSocket(this.s.getLocalPort());
        Socket s2 = ss.accept();
        this.in = new ObjectInputStream(s2.getInputStream());
        HashMap<String, Integer> rank = (HashMap<String, Integer>) in.readObject();

        this.bd.addRankingGlobal(rank);

    }

    private void adicionaSVLocal(PDU p) throws UnknownHostException, IOException {
        String ip = new String(p.getCampo(1).getBytes());
        BigInteger bg = new BigInteger(p.getCampo(2).getValor());
        int porta = bg.intValue();
        this.bd.getServidores().put(InetAddress.getByName(ip), porta);
        Socket server = new Socket(ip, porta);
        out = new ObjectOutputStream(server.getOutputStream());
        sendListDesafios(ip, bg);
        sendRanking();//
    }
// envia primeiro info a avisar que vai a seguir um MAP de desafios

    private void sendListDesafios(String ip, BigInteger bg) throws IOException {
        PDU res = new PDU(0, AtendimentoServidor.INFO);
        Campo c = new Campo(AtendimentoServidor.LISTADESAFIOS, new byte[]{0});
        res.addCampo(c);
        c = new Campo(AtendimentoServidor.IP, ip.getBytes());
        res.addCampo(c);
        c = new Campo(AtendimentoServidor.PORTA, bg.toByteArray());
        res.addCampo(c);

        out.writeObject(res);
        out.flush();
        out.reset();

        out.writeObject(bd.getDesafiosLocais());
        out.flush();
        out.reset();

    }
// envia primeiro info a avisar que vai a seguir um map com o ranking local

    private void sendRanking() throws IOException {
        PDU res = new PDU(0, AtendimentoServidor.INFO);
        Campo c = new Campo(AtendimentoServidor.RANKINGLOCAL, new byte[]{0});
        res.addCampo(c);
        out.writeObject(res);
        out.flush();
        out.reset();

        out.writeObject(bd.getRankingLocal());////////////////////////////////////////////
        out.flush();
        out.reset();
    }

    private void sendDesafio(String desafio) throws IOException {
        Desafio d = this.bd.getDesafio(desafio);
        out.writeObject(d);
        out.flush();
     
    }

    private void sendImage(String desafio, int pergunta) throws IOException {
        File f = new File(bd.getDesafio(desafio).getPergunta(pergunta).getImagem());
        out.writeObject(f);
        out.flush();
    }

    private void sendAudio(String desafio, int pergunta) throws IOException {
        File f = new File(bd.getDesafio(desafio).getPergunta(pergunta).getMusica());
        out.writeObject(f);
        out.flush();
    }

}

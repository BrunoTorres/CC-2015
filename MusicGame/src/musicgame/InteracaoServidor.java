package musicgame;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.TreeSet;
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

            input = (PDU) in.readObject();
            int op = input.getCampo(0).getIdTcp();

            switch (op) {
                case AtendimentoServidor.REGISTASV:
                    System.out.println("Regista");
                    registaServidor(input);
                    break;

                case AtendimentoServidor.REGISTASVSEMRESPOSTA:
                    System.out.println("registasemresposta");
                    adicionaSVLocal(input);
                    break;

                case AtendimentoServidor.LISTADESVS:
                    System.out.println("ListaDesafios");
                    adicionaSVLocal();
                    break;
                case MusicClient.QUESTAO: // Recebe um PDU com o nome do desafio e envia todas as musicas correspondentes
                    System.out.println("send ficheiros");
                    String desafio = input.getCampo(0).getValue();
                    sendAudio(desafio);
                    break;

                case AtendimentoServidor.REQUESTDESAFIO:
                    System.out.println("envia desafio");
                    desafio = input.getCampo(0).getValue();
                    String alcunha = input.getCampo(2).getValue();
                    System.out.println("DESAFIO PEDIDO = " + desafio);
                    sendDesafio(desafio, alcunha);
                    break;

                case AtendimentoServidor.DESAFIO:
                    System.out.println("desafiooo");
                    desafio = input.getCampo(0).getValue();
                    InetAddress ip = input.getCampo(1).getIP();
                    System.out.println("ip no recebe Desafio!!!= " + ip);
                    int porta = Integer.valueOf(input.getCampo(2).getValue());
                    Desafio des = (Desafio) in.readObject();
                    this.bd.addDesafioGlobal(desafio, des.getLocalDate());
                    this.bd.addDesafio(des);
                    System.out.println("$$$$$$$$$$$$ ip para guardar no desafio= " + ip);
                    this.bd.addDesafiosGlobais(des.getNome(), des.getLocalDate(), ip, porta);
                    break;
                case AtendimentoServidor.RANKINGLOCAL:
                    desafio=input.getCampo(1).getValue();
                    System.out.println("DESAFIO ONDE VAI ATUALIZAR O RANKING= "+ desafio);
                    adicionaRanking(desafio);
                    break;

                // case AtendimentoServidor.REGISTADESAFIO:  
                //  registaDesafio();// RECEBE um DESAFIO e pede musica e imagem para cada pergunta do desafio
                //     break;´
                }
            //this.s.close();

        } catch (IOException ex) {
            Logger.getLogger(InteracaoServidor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(InteracaoServidor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void registaServidor(PDU p) throws IOException, ClassNotFoundException { // sv principal regista novo sv, devolve-lhe lista dos que conhece e envia aos que conhece
        // o novo sv
        System.out.println("vou resgistar o servido que vem ");
        InetAddress ip = p.getCampo(1).getIP();
        //ObjectOutputStream o;
        System.out.println("ip= " + ip);
        //BigInteger bg = new BigInteger(p.getCampo(2).getValor());
        //int porta = bg.intValue();´
        int porta = Integer.valueOf(p.getCampo(2).getValue());
        //Socket serv = new Socket(InetAddress.getByName(ip), porta);

        Campo c;
        //                                                    falta info antes de enviar objecto?!
        // out = new ObjectOutputStream(serv.getOutputStream());
        //out.writeObject(this.bd.getServidores());
        //out.flush();
        System.out.println("vou enviar cenas");
        if (this.bd.getDesafios().size() > 0) {
            sendListDesafios(ip, porta);
        }
        if (this.bd.getRankingLocal().size() > 0) {
            sendRanking();
        }

        for (InetAddress i : this.bd.getServidores().keySet()) {
            int portaSV = this.bd.getServidores().get(i);
            if (i != ip) {
                try (Socket conhecidos = new Socket(i, portaSV)) {
                    PDU res = new PDU(0, AtendimentoServidor.INFO);
                    c = new Campo(AtendimentoServidor.REGISTASVSEMRESPOSTA, "");
                    res.addCampoTcp(c);
                    c = new Campo(AtendimentoServidor.IP, ip);
                    res.addCampoTcp(c);
                    c = new Campo(AtendimentoServidor.PORTA, String.valueOf(porta));
                    res.addCampoTcp(c);
                    out = new ObjectOutputStream(conhecidos.getOutputStream());
                    out.writeObject(res);
                    out.flush();
                    conhecidos.close();
                }

            }

        }
        this.bd.registaServidor(ip, porta);

///////////////////////////////////////TODOS
    }
/*
    //RECEBE o lista de desafios pendentes GLOBAIS///////////////////////////////////////////////////////
    private void adicionaDesafios(PDU input) throws IOException, ClassNotFoundException {
        InetAddress ip = input.getCampo(1).getIP();
        //BigInteger bg = new BigInteger(input.getCampo(2).getValor());
        //int porta = bg.intValue();
        int porta = Integer.valueOf(input.getCampo(2).getValue());

        ServerSocket ss = new ServerSocket(this.s.getLocalPort());
        Socket s2 = ss.accept();
        ObjectInputStream in2 = new ObjectInputStream(s2.getInputStream());

        this.in = new ObjectInputStream(s2.getInputStream());

        HashMap<String, LocalDateTime> des = (HashMap<String, LocalDateTime>) in.readObject();
        
        
        //this.s.close();

        // this.bd.addDesafiosGlobais(des, ip, porta);
    }
    */

    private void adicionaRanking(String desafio) throws IOException, ClassNotFoundException {
        
//        ServerSocket ss = new ServerSocket(this.s.getLocalPort());
       // Socket s2 = ss.accept();
        //this.in = new ObjectInputStream(s2.getInputStream());
        this.bd.getDesafio(desafio).incNumPlayersDone();
        System.out.println("CHEGOU UTILIZADOR");
        Utilizador  rank = (Utilizador)in.readObject();
        System.out.println("JA TEM O UTILIZADOR");

        this.bd.addRankingGlobal(desafio,rank);
        
       
        System.out.println("ACABOU");
        

        //this.s.close();

    }
// novo servidor adiciona lista de svs que o principal conhece

    private void adicionaSVLocal() throws IOException, ClassNotFoundException {
        ServerSocket ss = new ServerSocket(this.s.getLocalPort());
        Socket s2 = ss.accept();
        ObjectInputStream in2 = new ObjectInputStream(s2.getInputStream());

        HashMap<InetAddress, Integer> svs = (HashMap<InetAddress, Integer>) in2.readObject();

        this.bd.registaServidores(svs);

    }

    private void adicionaSVLocal(PDU p) throws UnknownHostException, IOException {
        System.out.println("vou receber as cenas");
        InetAddress ip = p.getCampo(1).getIP();
        //BigInteger bg = new BigInteger(p.getCampo(2).getValor());
        int porta = Integer.valueOf(p.getCampo(2).getValue());

        System.out.println("adiciona sv local ip " + ip + " Porta= " + porta);
        if (!this.bd.getServidores().containsKey(ip)) {
            this.bd.getServidores().put(ip, porta);
        }
        try (Socket server = new Socket(ip, porta)) {
            out = new ObjectOutputStream(server.getOutputStream());

            /**
             * *******************************************************************
             */
            System.out.println("Vou enviar as cenas");
            if (this.bd.getDesafios().size() > 0) {
                sendListDesafios(ip, porta);
            }
            if (this.bd.getRankingLocal().size() > 0) {
                sendRanking();
            }
            sendListDesafios(ip, porta);
            sendRanking();//
        }
    }
// envia primeiro info a avisar que vai a seguir um MAP de desafios

    private void sendListDesafios(InetAddress ip, int bg) throws IOException {
        PDU res = new PDU(0, AtendimentoServidor.INFO);
        Campo c = new Campo(AtendimentoServidor.LISTADESAFIOS, "");
        res.addCampoTcp(c);
        c = new Campo(AtendimentoServidor.IP, ip);
        res.addCampoTcp(c);
        c = new Campo(AtendimentoServidor.PORTA, String.valueOf(bg));
        res.addCampoTcp(c);

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
        Campo c = new Campo(AtendimentoServidor.RANKINGLOCAL, "");
        res.addCampoTcp(c);
        out.writeObject(res);
        out.flush();
        out.reset();

        out.writeObject(bd.getRankingLocal());////////////////////////////////////////////
        out.flush();
        out.reset();
    }

    private void sendDesafio(String desafio, String alcunha) throws IOException {
        Desafio d = this.bd.getDesafio(desafio);
        System.out.println("DESAFIO A SER ENVIAD como o NOME = " + d.getNome());
        out.writeObject(d);
        out.flush();
        File f;
        HashMap<String, byte[]> imagens = new HashMap<String, byte[]>();

        for (int i = 0; i < d.getQuestoes().size(); i++) {
            f = new File("C:\\Users\\patricia\\Desktop\\CC-2015\\Kit TP2-LEI-CC\\imagens\\" + d.getQuestoes().get(i).getImagem());
            byte[] r = Files.readAllBytes(f.toPath());
            imagens.put(d.getQuestoes().get(i).getImagem(), r);

        }
        out.writeObject(imagens);
        out.flush();
        out.reset();

        Utilizador u = new Utilizador(alcunha, 0);
        d.addUser(u, new byte[]{0});

    }

    private void sendImage(String desafio, int pergunta) throws IOException {
        File f = new File(bd.getDesafio(desafio).getPergunta(pergunta).getImagem());
        out.writeObject(f);
        out.flush();
    }

    private void sendAudio(String desafio) throws IOException {
        Desafio d = bd.getDesafio(desafio);
        HashMap<String, byte[]> musicas = new HashMap<String, byte[]>();

        for (int i = 0; i < d.getQuestoes().size(); i++) {
            File f = new File("C:\\Users\\patricia\\Desktop\\CC-2015\\Kit TP2-LEI-CC\\musica\\" + d.getQuestoes().get(i).getMusica());
            byte[] r = Files.readAllBytes(f.toPath());
            musicas.put(d.getQuestoes().get(i).getMusica(), r);

        }
        out.writeObject(musicas);
        out.flush();
        out.reset();

        /*
         } else if (pacote == 2) {
         musicas = new HashMap<String, byte[]>();
         for (int i = 5; i < d.getQuestoes().size(); i++) {
         File f = new File("C:\\Users\\patricia\\Desktop\\CC-2015\\Kit TP2-LEI-CC\\musica\\" + d.getQuestoes().get(i).getMusica());
         byte[] r = Files.readAllBytes(f.toPath());
         musicas.put(d.getQuestoes().get(i).getMusica(), r);
         }
         out.writeObject(musicas);
         out.flush();
         out.reset();
         }
         */
    }

}

package musicgame;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *
 */
public class InteracaoCliente extends Thread {

    private static final int OK = 0;
    private static final int FIM = 250;
    private static final int ERRO = 255;
    private static final int CONTINUA = 254;
    private static final int NOME = 1;
    private static final int ALCUNHA = 2;
    private static final int PASSWORD = 3;
    private static final int DATA = 4;
    private static final int HORA = 5;
    private static final int ESCOLHA = 6;
    private static final int DESAFIO = 7;
    private static final int NQUESTAO = 10;
    private static final int QUESTAO = 11;
    private static final int NRESPOSTA = 12;
    private static final int RESPOSTA = 13;
    private static final int CERTA = 14;
    private static final int PONTOS = 15;
    private static final int IMAGEM = 16;
    private static final int BLOCO = 17;
    private static final int AUDIO = 18;
    private static final int SCORE = 20;
    private static final int TIME = 21;

    private final DatagramPacket receivePacket;
    private DatagramSocket sendSocket;
    private DatagramPacket sendPacket;
    private BD bd;
    byte[] receiveData;
    byte[] sendData;

    public InteracaoCliente(DatagramPacket so, BD b) throws UserInexistenteException {
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
        } catch (IOException | UserInexistenteException e) {
            System.out.println(e.toString());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(InteracaoCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void analisaPacote(byte[] data, InetAddress add, int port) throws IOException, UserInexistenteException, ClassNotFoundException {
        PDU reply;
        int s;
        Campo c;
        byte[] tl = {data[2], data[3]};
        System.out.println("Opcao:" + data[4]);
        switch (data[4]) {
            case 1:
                s = PDU.byteArrayToInt(tl);
                reply = new PDU(s, (byte) 0);
                c = new Campo(OK, "OK".getBytes());
                reply.addCampo(c);
                responde(reply, add, port);
                break;
            case 2:
                processaRegisto(data, add, port);
                break;
            case 3:
                processaLogin(data, add, port);
                break;
            case 4:
                s = PDU.byteArrayToInt(tl);
                reply = new PDU(s, (byte) 0);
                c = new Campo(OK, "OK".getBytes());
                reply.addCampo(c);
                responde(reply, add, port);
                break;
            case 5:
                this.bd.updateUser(bd.getUserByIP(add).getAlcunha(), add, port);
                quitDesafio(data, add, port);
                break;
            case 6:
                System.out.println("End");
                this.bd.updateUser(this.bd.getUserByIP(add).getAlcunha(), add, port);
                fimDesafio(data, add, port);
                break;
            case 7:
                System.out.println("Vai listar os desafios");
                this.bd.updateUser(bd.getUserByIP(add).getAlcunha(), add, port);
                listaDesafios(data, add, port);
                break;
            case 8:
                System.out.println("Make challenge");
                 {
                    try {
                        this.bd.updateUser(bd.getUserByIP(add).getAlcunha(), add, port);
                        criaDesafio(data, add, port);
                    } catch (UserInexistenteException ex) {
                        Logger.getLogger(InteracaoCliente.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;
            case 9:
                System.out.println("Accept challenge");
                s = PDU.byteArrayToInt(tl);
                reply = new PDU(s, (byte) 0);
                c = new Campo(OK, PDU.intToByteArray(0));
                reply.addCampo(c);
                PDU p = new PDU(data);
                //Desafio d = bd.getDesafio(new String(p.getCampo(0).getValor()));
                String desafio = new String(p.getCampo(0).getValor());
                if (this.bd.getDesafiosGlobais().containsKey(desafio)) {

                    try {
                        System.out.println("vou fazer fazer request desafio");
                        requestDesafio(desafio, bd.getUserByIP(add));
                    } catch (UserInexistenteException | IOException | ClassNotFoundException e) {
                        System.out.println("erro");
                    }

                }

                Desafio d = bd.getDesafio(desafio);
                this.bd.updateUser(this.bd.getUserByIP(add).getAlcunha(), add, port);
                d.addUser(this.bd.getUserByIP(add), tl);
                System.out.println("add user =" + bd.getUserByIP(add));
                responde(reply, add, port);
                Jogo j;
                j = new Jogo(bd.getUserByIP(add), d.getLocalDate(), d, this.bd, 1, true);
                j.start();
                break;
            case 10:
                System.out.println("Delete challenge");
                this.bd.updateUser(bd.getUserByIP(add).getAlcunha(), add, port);
                deleteChallenge(data, add, port);
                break;
            case 11:
                this.bd.updateUser(bd.getUserByIP(add).getAlcunha(), add, port);
                respostas(data, add, port);

                break;
            case 12:
                System.out.println("Retransmit");
                this.bd.updateUser(bd.getUserByIP(add).getAlcunha(), add, port);
                retransmit(data, add, port);
                break;
            case 13:
                System.out.println("List ranking");
                this.bd.updateUser(bd.getUserByIP(add).getAlcunha(), add, port);
                listaRanking(data, add, port);
                break;
            case 14:
                System.out.println("Proxima pergunta");
                p = new PDU(data);
                d = bd.getDesafio(new String(p.getCampo(0).getValor()));
                int nQuestao = p.getCampo(1).getValor()[0];
                this.bd.updateUser(this.bd.getUserByIP(add).getAlcunha(), add, port);
                j = new Jogo(bd.getUserByIP(add), d.getLocalDate(), d, this.bd, nQuestao, false);
                j.start();

            default:
                break;
        }

    }

    public void responde(PDU pack, InetAddress add, int port) {
        try {
            byte[] data = pack.getBytes();
            sendPacket = new DatagramPacket(data, data.length, add, port);
            sendSocket.send(sendPacket);
        } catch (IOException ex) {
            System.out.println("Responde");
        }
    }

    public void quitDesafio(byte[] data, InetAddress add, int port) throws UserInexistenteException {
        PDU pacote = new PDU(data);
        byte[] tl = {data[2], data[3]};
        int s = PDU.byteArrayToInt(tl);
        PDU reply;
        Campo c;
        //System.out.println("coisasaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        Utilizador user = bd.getUserByIP(add);
        Desafio d = bd.getDesafio(new String(pacote.getCampo(0).getValor()));
        d.remUtilizadoresEnd(user.getAlcunha());
        d.remUtilizadores(user.getAlcunha());
        reply = new PDU(s, (byte) 0);
        c = new Campo(OK, "OK".getBytes());
        reply.addCampo(c);
        responde(reply, add, port);

    }

    @SuppressWarnings("empty-statement")
    public synchronized void fimDesafio(byte[] data, InetAddress add, int port) throws UserInexistenteException, IOException {
        PDU pacote = new PDU(data);
        byte[] tl = {data[2], data[3]};
        int s = PDU.byteArrayToInt(tl);
        Campo c;
        Utilizador user = bd.getUserByIP(add);
        Desafio d = bd.getDesafio(new String(pacote.getCampo(0).getValor()));

        d.addUserEnd(user);
        System.out.println("USERRRRR " + user.getAlcunha());

        sendRankinLocal(user, d.getNome());
        System.out.println("ACABOUUUU RANKING");
        d = bd.getDesafio(new String(pacote.getCampo(0).getValor()));
        System.out.println("DESAFIO update!!!!!");
        boolean cenas=false;
        Campo des = new Campo(DESAFIO, pacote.getCampo(0).getValor());
        
        while(cenas==false){
        if (d.getNumPlayersDone() < d.getTamanhoUsers()) {
            System.out.println("MENOR NAO PODE ACABAR= "+ d.getNumPlayersDone());
            d.setNumPlayersDone(d.getNumPlayersDone() + 1);
            System.out.println("Incrementou");
            cenas=false;
        } else {

            d.setStatus(true);
            TreeSet<Utilizador> utili = new TreeSet<>(new CompareUsersByPoints());
            for (Utilizador u : d.getUserEnd().values()) {
                utili.add(u);
            }

            utili.first().addPontuacao(3);
            for (Utilizador uaux : utili) {
                PDU resposta = new PDU(s, (byte) 0);
                resposta.addCampo(des);
                this.bd.actRanking(uaux);

                //******************** SEND INF DE ACTUALIZACAO DE RANKING*****************//
                //*************************************************************************//
                for (Utilizador u : utili) {
                    c = new Campo(ALCUNHA, u.getAlcunha().getBytes());
                    resposta.addCampo(c);
                    c = new Campo(PONTOS, PDU.intToByteArray(u.getPontuacao()));
                    resposta.addCampo(c);
                }

                responde(resposta, this.bd.getUser(uaux.getAlcunha()).getIp(), this.bd.getUser(uaux.getAlcunha()).getPort());
                 cenas=true;
            }

            // sendRankinLocal();
        }
    }
    }
    
    

    private synchronized void processaLogin(byte[] data, InetAddress add, int port) {

        PDU pacote = new PDU(data);

        String alc = new String(pacote.getCampo(0).getValor());

        PDU resposta;
        Campo c;
        byte[] tl = {data[2], data[3]};
        int s = PDU.byteArrayToInt(tl);
        try {
            Utilizador u;
            u = this.bd.getUser(alc);
            System.out.println("ALCUNNHA=" + alc);
            byte[] passATestar = pacote.getCampo(1).getValor();
            byte[] pass = u.getPass();
            if (Arrays.equals(passATestar, pass)) { //Se a passe for correta
                resposta = new PDU(s, (byte) 0);
                c = new Campo(NOME, u.getUserName().getBytes());
                resposta.addCampo(c);
                c = new Campo(SCORE, PDU.intToByteArray(bd.getRanking(u.getAlcunha())));
                resposta.addCampo(c);
                this.bd.updateUser(u.getAlcunha(), add, port);

                responde(resposta, this.bd.getUser(alc).getIp(), this.bd.getUser(alc).getPort());
            } else { //Pacote de erro passe incorreta
                resposta = new PDU(s, (byte) 0);
                c = new Campo(ERRO, "Password".getBytes());
                resposta.addCampo(c);
                responde(resposta, add, port);
            }
        } catch (UserInexistenteException ex) {
            //pacote de erro            
            resposta = new PDU(s, (byte) 0);
            c = new Campo(ERRO, "Utilizador".getBytes());
            resposta.addCampo(c);
            responde(resposta, add, port);
        }

    }

    private void processaRegisto(byte[] data, InetAddress add, int port) {
        PDU pacote = new PDU(data);
        byte[] tl = {data[2], data[3]};
        int s = PDU.byteArrayToInt(tl);
        PDU reply;
        Campo c;
        String nome = new String(pacote.getCampo(0).getValor());
        String alc = new String(pacote.getCampo(1).getValor());
        byte pass[] = pacote.getCampo(2).getValor();
        boolean e = this.bd.existeUser(alc);
        if (e) {
            reply = new PDU(s, (byte) 0);
            c = new Campo(ERRO, "Utilizador existente!".getBytes());
            reply.addCampo(c);
            responde(reply, add, port);
        } else {
            Utilizador novo = new Utilizador(nome, alc, pass, add, port);
            this.bd.addUser(novo);

            reply = new PDU(s, (byte) 0);
            c = new Campo(OK, "OK".getBytes());
            reply.addCampo(c);
            responde(reply, add, port);
        }

    }

    private void listaDesafios(byte[] data, InetAddress add, int port) throws IOException {
        ArrayList<Desafio> desafios = bd.getDesafios();
        ArrayList<Desafio> desafiosAenviar = new ArrayList<>();
        HashMap<String, LocalDateTime> dGlobais = (HashMap<String, LocalDateTime>) bd.getDesafiosGlobais();

        // System.out.println("##############################"+bd.getDesafiosLocais().get("desafio1"));
        byte[] tl = {data[2], data[3]};
        int s = PDU.byteArrayToInt(tl);
        int tamGlobais = dGlobais.size();

        System.out.println("DESAFIO TAMANHO " + tamGlobais);
        PDU reply;
        Campo c, da, h, f;
        int tam = desafios.size();
        int t = 0;
        if (tam > 0) {
            for (Desafio d : desafios) {
                if (d.getStatus() == false && d.getComecou() == false) {
                    desafiosAenviar.add(d);
                }
            }
            tam = desafiosAenviar.size();

            if (tam > 0) {
                for (Desafio d : desafiosAenviar) {
                    t++;
                    reply = new PDU(s, (byte) 0);
                    c = new Campo(DESAFIO, d.getNome().getBytes());
                    reply.addCampo(c);
                    da = new Campo(DATA, d.getData());
                    reply.addCampo(da);
                    h = new Campo(HORA, d.getTempo());
                    reply.addCampo(h);

                    if (t < tam) {
                        f = new Campo(CONTINUA, "0".getBytes());
                        reply.addCampo(f);
                    }
                    responde(reply, add, port);

                }

            } else {
                reply = new PDU(s, (byte) 0);
                c = new Campo(ERRO, "Zero desafios".getBytes());
                reply.addCampo(c);
                responde(reply, add, port);
            }
        } else {
            reply = new PDU(s, (byte) 0);
            c = new Campo(ERRO, "Zero desafios".getBytes());
            reply.addCampo(c);
            responde(reply, add, port);
        }

    }

    private void listaRanking(byte[] data, InetAddress add, int port) throws IOException, UserInexistenteException {
        byte[] tl = {data[2], data[3]};
        int s = PDU.byteArrayToInt(tl);
        TreeSet<Utilizador> utili = new TreeSet<>(new CompareUsersByPoints());
        PDU reply;
        Campo c;
        int tam = this.bd.getRankingLocal().size();
        int t = 0;

        for (String a : this.bd.getRankingLocal().keySet()) {
            Utilizador u = this.bd.getUser(a).clone();
            u.initPontuacao();
            u.addPontuacao(this.bd.getRanking(a));
            utili.add(u);
        }

        t = 0;
        for (Utilizador u : utili) {
            t++;

            reply = new PDU(s, (byte) 0);
            c = new Campo(ALCUNHA, u.getAlcunha().getBytes());
            reply.addCampo(c);
            c = new Campo(PONTOS, PDU.intToByteArray(u.getPontuacao()));
            reply.addCampo(c);
            if (t < tam) {
                c = new Campo(CONTINUA, "0".getBytes());
                reply.addCampo(c);
            }
            responde(reply, add, port);
        }
    }

    private void criaDesafio(byte[] data, InetAddress add, int port) throws UserInexistenteException, SocketException {
        PDU pacote = new PDU(data);
        byte[] tl = {data[2], data[3]};
        int s = PDU.byteArrayToInt(tl);
        PDU reply;
        Campo c, dat, hor;
        String nome = new String(pacote.getCampo(0).getValor());
        String user = new String(pacote.getCampo(1).getValor());
        boolean e = bd.existeDesafio(nome);

        if (e) {
            reply = new PDU(s, (byte) 0);
            c = new Campo(ERRO, "Desafio existente!".getBytes());
            reply.addCampo(c);
            responde(reply, add, port);
        } else {
            //LocalDateTime tempo = LocalDateTime.now().plusMinutes(5);
            LocalDateTime tempo = LocalDateTime.now().plusSeconds(20);
            int aux = tempo.getYear() % 100;
            int pri = aux / 10;
            int sec = aux % 10;
            BigInteger anoAux = BigInteger.valueOf(tempo.getYear());
            byte[] anoBytes = anoAux.toByteArray();
            byte[] anoF;
            if (anoBytes.length < 3) {
                anoF = new byte[]{0x00, anoBytes[0], anoBytes[1]};
            } else {
                anoF = anoBytes;
            }
            byte mes = (byte) tempo.getMonthValue();
            byte dia = (byte) tempo.getDayOfMonth();
            byte hora = (byte) tempo.getHour();
            byte minuto = (byte) tempo.getMinute();
            byte segundo = (byte) tempo.getSecond();
            Desafio d = new Desafio(nome, user, anoF, mes, dia, hora, minuto, segundo);
            // d.setDataProperty();
            //d.setHoraProperty();
            criaPerguntas(d);
            Utilizador u = bd.getUserByIP(add);
            d.addUser(u, tl);
            this.bd.addDesafio(d);
            reply = new PDU(s, (byte) 0);
            c = new Campo(DESAFIO, d.getNome().getBytes());
            reply.addCampo(c);
            dat = new Campo(DATA, d.getData());
            reply.addCampo(dat);
            hor = new Campo(HORA, d.getTempo());
            reply.addCampo(hor);
            responde(reply, add, port);
            this.bd.updateUser(u.getAlcunha(), add, port);

            try {
                if (!bd.getServidores().isEmpty()) {
                    System.out.println("##################### vou enviar info desafio " + d.getNome());
                    sendInfoDesafio(d);
                }
            } catch (IOException ex) {
                Logger.getLogger(InteracaoCliente.class.getName()).log(Level.SEVERE, null, ex);
            }

            Jogo j;
            System.out.println("vou entrar no jogo");
            j = new Jogo(this.bd.getUserByIP(add), tempo, d, this.bd, 1, true);
            j.start();
        }
    }

    private void respostas(byte[] data, InetAddress add, int port) throws SocketException, UserInexistenteException {
        PDU pacote = new PDU(data);
        byte[] tl = {data[2], data[3]};
        int s = PDU.byteArrayToInt(tl);
        PDU reply;
        Campo c, dat;
        int id = pacote.getCampo(0).getId();
        int escolha = PDU.byteArrayToInt(pacote.getCampo(0).getValor());
        String nomeDesafio = new String(pacote.getCampo(1).getValor());
        int nQuestao = PDU.byteArrayToInt(pacote.getCampo(2).getValor());
        int tempoResposta = PDU.byteArrayToInt(pacote.getCampo(3).getValor());
        int pontuacao, certa;
        Utilizador user = bd.getUserByIP(add);
        Desafio d = bd.getDesafio(nomeDesafio);
        if (d.getUsers().containsKey(user.getAlcunha())) {

            System.out.println("Numero de questao na validação da resposta: " + (nQuestao - 2));
            int respostaCerta = d.getPergunta(nQuestao - 2).getRespostaCerta();
            System.out.println("A resposta que ele escolheu é a: " + escolha);
            System.out.println("resposta certa é a numero: " + respostaCerta + " À pergunta: " + d.getPergunta(nQuestao - 2).getPergunta());
            if (respostaCerta == escolha) {
                user.addTempoResposta(tempoResposta);
                user.addPontuacao(2);
                pontuacao = 2;
                certa = 1;

            } else {
                if (escolha == 0) {
                    pontuacao = 0;
                    certa = 0;
                } else {
                    user.subPontuacao(1);
                    pontuacao = -1;
                    certa = 0;
                }
            }
            reply = new PDU(s, (byte) 0);
            c = new Campo(DESAFIO, d.getNome().getBytes());
            reply.addCampo(c);
            c = new Campo(NQUESTAO, PDU.intToByteArray(nQuestao));
            reply.addCampo(c);
            c = new Campo(CERTA, new byte[]{(byte) certa});
            reply.addCampo(c);
            c = new Campo(PONTOS, new byte[]{(byte) pontuacao});
            reply.addCampo(c);

            if (nQuestao != d.getQuestoes().size()) {
                c = new Campo(CONTINUA, new byte[]{(byte) 0});
            } else {
                c = new Campo(FIM, new byte[]{(byte) 0});
            }
            reply.addCampo(c);

            responde(reply, add, port);
        }

    }

    private void criaPerguntas(Desafio d) {
        for (int i = 0; i < 10; i++) {

            Pergunta p = this.bd.getPergunta();
            boolean b = d.addPergunta(p);
            while (!b) {
                p = this.bd.getPergunta();
                b = d.addPergunta(p);

            }
        }

    }

    private Map<Integer, byte[]> getBlocos(String path) throws IOException {
        TreeMap<Integer, byte[]> blocos = new TreeMap<>();
        File f = new File(path);
        byte[] m = Files.readAllBytes(f.toPath());
        int nPackets = m.length / 49152;
        int lastPackBytes = m.length % 49152;
        int i;
        PDU image;

        byte[] p;
        for (i = 0; i < nPackets; i++) {
            p = new byte[49152];
            for (int j = 0; j < 49152; j++) {
                p[j] = m[i * 49152 + j];
            }

            blocos.put(i + 1, p);

        }
        p = new byte[lastPackBytes];
        for (int j = 0; j < lastPackBytes; j++) {
            p[j] = m[i * 49152 + j];
        }
        blocos.put(i + 1, p);

        return blocos;
    }

    private void retransmit(byte[] data, InetAddress add, int port) {
        try {
            PDU pacote = new PDU(data);
            byte[] tl = {data[2], data[3]};
            int s = PDU.byteArrayToInt(tl);
            Campo c;
            String nome = new String(pacote.getCampo(0).getValor()); //nome
            Desafio d = this.bd.getDesafio(nome);
            int nQ = pacote.getCampo(1).getValor()[1] - 1;               //n questao
            Pergunta p = d.getPergunta(nQ);
            int tipo = pacote.getCampo(2).getValor()[0];             //imagem ou audio
            String ca;
            if (tipo == IMAGEM) {
                ca = this.bd.getPathImage().concat(p.getImagem());
            } else {
                ca = this.bd.getPathMusic().concat(p.getMusica());
            }
            int bloco = PDU.byteArrayToInt(pacote.getCampo(3).getValor());      //n bloco

            TreeMap<Integer, byte[]> blocos = (TreeMap) this.getBlocos(ca);
            byte b[] = blocos.get(bloco);

            //byte b[] = this.bd.partes.get(bloco);
            PDU music = new PDU(s, (byte) 0);
            c = new Campo(DESAFIO, nome.getBytes());
            music.addCampo(c);                                      //nome
            c = new Campo(NQUESTAO, new byte[]{(byte) nQ});
            music.addCampo(c);                                      //nQuestao
            c = new Campo(BLOCO, new byte[]{(byte) bloco});
            music.addCampo(c);                                          //nBloco
            c = new Campo(tipo, b);
            music.addCampo(c);                                      //bloco
            if (bloco == blocos.lastKey()) {
                music.addCampo(new Campo(FIM, new byte[]{0}));
            } else {
                music.addCampo(new Campo(CONTINUA, new byte[]{0}));
            }
            responde(music, add, port);

        } catch (IOException ex) {
            Logger.getLogger(InteracaoCliente.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void deleteChallenge(byte[] data, InetAddress add, int port) {
        PDU pacote = new PDU(data);
        String des = new String(pacote.getCampo(0).getValor());
        Desafio d = this.bd.getDesafio(des);
        d.setStatus(true);

    }

    // Faz um pedido para ser enviado um desafio nao existente na base de dados local e recebe as imagens e musicas correspondentes.
    private void requestDesafio(String desafio, Utilizador u) throws IOException, ClassNotFoundException {
        PDU res = new PDU(0, AtendimentoServidor.INFO);
        Campo c = new Campo(AtendimentoServidor.REQUESTDESAFIO, desafio);
        res.addCampoTcp(c);
        c = new Campo(MusicClient.DESAFIO, desafio);
        res.addCampoTcp(c);
        c = new Campo(MusicClient.ALCUNHA, u.getAlcunha());
        res.addCampoTcp(c);

        HashMap<InetAddress, Integer> servidor = this.bd.getDesafioByIp(desafio);

        InetAddress j = InetAddress.getLocalHost();
        int porta = 0;
        for (InetAddress i : servidor.keySet()) {
            System.out.println("ip = " + i);
            j = i;
            porta = servidor.get(i);

            break;
        }
        Socket serv = new Socket(j, porta);

        //ServerSocket ss = new ServerSocket(this.bd.getPorta());
        //serv = ss.accept();
        ObjectOutputStream out = new ObjectOutputStream(serv.getOutputStream());
        out.writeObject(res);
        out.flush();
        out.reset();
        //serv.close();
        //Desafio d = (Desafio) in.readObject();

        ObjectInputStream inFromServer = new ObjectInputStream(serv.getInputStream());
        Desafio d = (Desafio) inFromServer.readObject();
        HashMap<String, byte[]> imagens = (HashMap<String, byte[]>) inFromServer.readObject();
        //inFromServer.reset();

        for (String s : imagens.keySet()) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            os.write(imagens.get(s));
            File f = new File("/Users/brunopereira/Documents/SourceTree/CC/MusicGame/build/classes/musicgame/imagens/" + s);
            // System.out.println("CAminho da imagem = " + f.getPath());
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(os.toByteArray());
        }
        serv.close();
///////////////////////////////////MUSICA///////////////////////////////////////////////////////////
        serv = new Socket(j, porta);
        out = new ObjectOutputStream(serv.getOutputStream());

        res = new PDU(0, AtendimentoServidor.INFO);
        c = new Campo(MusicClient.QUESTAO, d.getNome());
        res.addCampoTcp(c);
        c = new Campo(MusicClient.QUESTAO, "");
        res.addCampoTcp(c);

        out.writeObject(res);
        out.flush();
        out.reset();
        serv.shutdownOutput();

        inFromServer = new ObjectInputStream(serv.getInputStream());

        HashMap<String, byte[]> musicas = (HashMap<String, byte[]>) inFromServer.readObject();
        //inFromServer.reset();

        for (String s : musicas.keySet()) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            os.write(musicas.get(s));
            File f = new File("/Users/brunopereira/Documents/SourceTree/CC/MusicGame/build/classes/musicgame/musica/" + s);
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(os.toByteArray());
        }

        serv.shutdownInput();

        serv.close();

    }

    //Envia a nome e data correspondente ao desafio novo que acabou de ser criado 
    private void sendInfoDesafio(Desafio d) throws IOException {

        for (InetAddress i : this.bd.getServidores().keySet()) {
            int portaSV = this.bd.getServidores().get(i);
            try (Socket conhecidos = new Socket(i, portaSV)) {

                PDU res = new PDU(0, AtendimentoServidor.INFO);
                Campo c = new Campo(AtendimentoServidor.DESAFIO, d.getNome());
                res.addCampoTcp(c);
                c = new Campo(AtendimentoServidor.IP, bd.getIp());

                res.addCampoTcp(c);
                c = new Campo(AtendimentoServidor.PORTA, String.valueOf(bd.getPorta()));
                res.addCampoTcp(c);

                ObjectOutputStream out = new ObjectOutputStream(conhecidos.getOutputStream());

                out.writeObject(res);
                out.flush();

                out.writeObject(d);
                out.flush();

                conhecidos.close();
            }

        }

    }

    private void sendRankinLocal(Utilizador utili, String desafio) throws IOException {
        System.out.println("Envia ranking a todos");
        for (InetAddress i : this.bd.getServidores().keySet()) {
            System.out.println(" enviou x vezez");
            int portaSV = this.bd.getServidores().get(i);
            Socket conhecidos = new Socket();
            conhecidos.connect(new InetSocketAddress(i, portaSV), 600000);
            System.out.println("Abriu socket de ranking");

            PDU res = new PDU(0, AtendimentoServidor.INFO);
            Campo c = new Campo(AtendimentoServidor.RANKINGLOCAL, new byte[]{0});
            res.addCampoTcp(c);
            c = new Campo(MusicClient.DESAFIO, desafio);
            res.addCampoTcp(c);

            ObjectOutputStream out = new ObjectOutputStream(conhecidos.getOutputStream());
            out.writeObject(res);
            System.out.println("enviou pdu");
            out.flush();
            out.writeObject(utili);
            System.out.println("enviou utilizadores");
            out.flush();
            //conhecidos.close();
            System.out.println("Fexou");

        }
    }
}

/*   
 private void registaDesafio() throws IOException, ClassNotFoundException{ //////////////
 ServerSocket ss = new ServerSocket(this.bd.getPorta());
 Socket s2 = ss.accept();
 ObjectInputStream in2 = new ObjectInputStream(s2.getInputStream());
 PDU res;
 Campo c;
 Desafio d =(Desafio) in2.readObject();
        
 for(int i=0;i<d.getQuestoes().size();i++){
 res = new PDU(0, AtendimentoServidor.INFO);
 c = new Campo(MusicClient.DESAFIO,d.getNome().getBytes());
 res.addCampo(c);
 c = new Campo(MusicClient.QUESTAO,PDU.intToByteArray(i));
 res.addCampo(c);
 out.writeObject(res);
 out.flush(); 
            
 File imagem =(File)in.readObject();
 File audio = (File)in.readObject();
        
 d.getQuestoes().get(i).setImagem(imagem.getPath());
 d.getQuestoes().get(i).setMusica(audio.getPath());
 }
 }
 */
////////LIST DESAFIOS

/*
 /****************************************************************** TESTAR AGORA COM DESAFIOS GLOBAIS ******/////////////
/*
 for (String desafio : dGlobais.keySet()) {
 t++;
 reply = new PDU(s, (byte) 0);
 c = new Campo(DESAFIO, desafio.getBytes());
 reply.addCampo(c);
 int aux = dGlobais.get(desafio).getYear() % 100;
 int pri = aux / 10;
 int sec = aux % 10;
 BigInteger anoAux = BigInteger.valueOf(dGlobais.get(desafio).getYear());
 byte[] anoBytes = anoAux.toByteArray();
 byte[] anoF;
 if (anoBytes.length < 3) {
 anoF = new byte[]{0x00, anoBytes[0], anoBytes[1]};
 } else {
 anoF = anoBytes;
 }
 byte mes = (byte) dGlobais.get(desafio).getMonthValue();
 byte dia = (byte) dGlobais.get(desafio).getDayOfMonth();
 byte hora = (byte) dGlobais.get(desafio).getHour();
 byte minuto = (byte) dGlobais.get(desafio).getMinute();
 byte segundo = (byte) dGlobais.get(desafio).getSecond();
 Desafio desafioGlobal = new Desafio(desafio, null, anoF, mes, dia, hora, minuto, segundo);

 c = new Campo(DESAFIO, desafioGlobal.getNome().getBytes());
 reply.addCampo(c);
 da = new Campo(DATA, desafioGlobal.getData());
 reply.addCampo(da);
 h = new Campo(HORA, desafioGlobal.getTempo());
 reply.addCampo(h);
 if (t < tam) {
 f = new Campo(CONTINUA, "0".getBytes());
 reply.addCampo(f);
 }
 responde(reply, add, port);

 }
 */
/*


 ////////////////// 2º pedido/////////////////////////////////////
 serv = new Socket(j, porta);
 System.out.println("Musicas 2 para IP PARA SER PEDIDO = " + j);
 out = new ObjectOutputStream(serv.getOutputStream());

 res = new PDU(0, AtendimentoServidor.INFO);
 c = new Campo(MusicClient.QUESTAO, d.getNome());
 res.addCampoTcp(c);
 c = new Campo(MusicClient.QUESTAO, "2");
 res.addCampoTcp(c);

 out.writeObject(res);
 out.flush();
 out.reset();
 serv.shutdownOutput();
 System.out.println("pedido enviado");
 inFromServer = new ObjectInputStream(serv.getInputStream());
 System.out.println("Abriu o input para receber o musicas!");

 musicas = (HashMap<String, byte[]>) inFromServer.readObject();
 serv.shutdownInput();
 serv.close();
        
 System.out.println("recebeu musicas");

 for (String s : musicas.keySet()) {
 ByteArrayOutputStream os = new ByteArrayOutputStream();
 os.write(musicas.get(s));
 File f = new File("/Users/brunopereira/Documents/SourceTree/CC/MusicGame/build/classes/musicgame/musica/" + s);
 FileOutputStream fos = new FileOutputStream(f);
 fos.write(os.toByteArray());
 }
 */

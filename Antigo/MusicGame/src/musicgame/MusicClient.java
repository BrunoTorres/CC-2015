package musicgame;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class MusicClient {

    public static final int HELLO = 1;
    public static final int REGISTER = 2;
    public static final int LOGIN = 3;
    public static final int LOGOUT = 4;
    public static final int QUIT = 5;
    public static final int END = 6;
    public static final int LIST_CHALLENGES = 7;
    public static final int MAKE_CHALLENGE = 8;
    public static final int ACCEPT_CHALLENGE = 9;
    public static final int DELETE_CHALLENGE = 10;
    public static final int ANSWER = 11;
    public static final int RETRANSMIT = 12;
    public static final int LIST_RANKING = 13;
    public static final int NEXT_QUESTION = 14;

    public static final int OK = 0;
    public static final int FIM = 250;
    public static final int ERRO = 255;
    public static final int CONTINUA = 254;
    public static final int NOME = 1;
    public static final int ALCUNHA = 2;
    public static final int PASSWORD = 3;
    public static final int DATA = 4;
    public static final int HORA = 5;
    public static final int ESCOLHA = 6;
    public static final int DESAFIO = 7;
    public static final int NQUESTAO = 10;
    public static final int QUESTAO = 11;
    public static final int NRESPOSTA = 12;
    public static final int RESPOSTA = 13;
    public static final int CERTA = 14;
    public static final int PONTOS = 15;
    public static final int IMAGEM = 16;
    public static final int BLOCO = 17;
    public static final int AUDIO = 18;
    public static final int SCORE = 20;
    public static final int TIME = 21;

    private static final Scanner in = new Scanner(System.in);
    private static int label = 1;
    private static DatagramPacket sendPacket;
    private static BufferedReader inFromUser;
    private static byte[] sendData;
    private static byte[] receiveData;
    private static InetAddress IPAddress;
    private static DatagramSocket clientSocket;
    private static DatagramPacket receivePacket;
    private static ArrayList<Campo> lastCamposSent;
    private static int lastIdSent;
    private static int tentativa;

    public static void sendPDU(int id, ArrayList<Campo> campos) throws IOException {
        if (id != lastIdSent) {
            tentativa = 0;
        }
        lastCamposSent = campos;
        lastIdSent = id;
        System.out.println("Atualizou para a opcao: " + id);
        clientSocket = new DatagramSocket();
        //IPAddress = InetAddress.getByName("192.168.1.79");
        IPAddress = InetAddress.getByName("192.168.173.1");
        //System.out.println(IPAddress);
        receiveData = new byte[50000];
        byte[] data;
        label++;
        PDU packet = new PDU(label, (byte) id);
        if (campos != null) {
            for (Campo c : campos) {
                packet.addCampo(c);
            }
        }
        data = packet.getBytes();
        sendPacket = new DatagramPacket(data, data.length, IPAddress, 55000);
        clientSocket.send(sendPacket);
        label++;

    }

    public static PDU receivePDUNoExeception() throws IOException, SocketTimeoutException {
        int tam;
        byte[] data, res;
        clientSocket.setSoTimeout(600000);
        receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        tam = receivePacket.getLength();
        receivePacket.setLength(receivePacket.getLength());
        res = receivePacket.getData();
        data = new byte[tam];
        System.arraycopy(res, 0, data, 0, tam);
        PDU pacote = new PDU(data);
        return pacote;
    }

    public static PDU receivePDU() throws IOException, SocketTimeoutException, ServerUnreachableException {
        int tam;
        byte[] data, res;
        clientSocket.setSoTimeout(5000);
        try {
            receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            tam = receivePacket.getLength();
            receivePacket.setLength(receivePacket.getLength());
            res = receivePacket.getData();
            data = new byte[tam];
            System.arraycopy(res, 0, data, 0, tam);
            PDU pacote = new PDU(data);
            return pacote;
        } catch (SocketTimeoutException ste) {
            System.out.println("Tentativa numero: " + tentativa);
            tentativa++;
            if (tentativa <= 5) {
                sendPDU(lastIdSent, lastCamposSent);
                return receivePDU();
            } else {
                throw new ServerUnreachableException();
            }

        }
    }

    public static void menuInit() throws IOException, ServerUnreachableException {
        sendPDU(HELLO, null);
        try {
            PDU pacote = receivePDU();
        } catch (SocketTimeoutException ste) {

        }
    }

    public static boolean menuRegista(String nome, String al, String pass) throws IOException, SocketTimeoutException, ServerUnreachableException {
        ArrayList<Campo> campos = new ArrayList<>();
        Campo nomeCampo = new Campo(NOME, nome.getBytes());
        campos.add(nomeCampo);
        Campo alcunha = new Campo(ALCUNHA, al.getBytes());
        campos.add(alcunha);
        Campo password = new Campo(PASSWORD, pass.getBytes());
        campos.add(password);
        sendPDU(REGISTER, campos);
        PDU pacote = receivePDU();
        return pacote.getCampo(0).getId() != 255;
    }

    public static Utilizador menuLogin(Utilizador u) throws IOException, SocketTimeoutException, UserInexistenteException, ServerUnreachableException {
        ArrayList<Campo> campos = new ArrayList<>();
        int score;
        Campo m = new Campo(ALCUNHA, u.getAlcunha().getBytes());
        Campo p = new Campo(PASSWORD, u.getPass());
        campos.add(m);
        campos.add(p);
        sendPDU(LOGIN, campos);

        PDU pacote = receivePDU();
        Utilizador utilizador = new Utilizador();

        if (pacote.getCampo(0).getId() == 255) {
            throw new UserInexistenteException(new String(pacote.getCampo(0).getValor()));// Utilizador e Password apanhar
        } else {
            String nome = new String(pacote.getCampo(0).getValor());
            score = PDU.byteArrayToInt(pacote.getCampo(1).getValor());
            utilizador = new Utilizador(nome, score);

        }
        return utilizador;

    }

    public static void menuLogout() throws SocketTimeoutException, IOException, ServerUnreachableException {
        sendPDU(LOGOUT, null);
        PDU p = receivePDU();
    }

    public static void menuQuit(String nomeDesafio) throws IOException, SocketTimeoutException, ServerUnreachableException {
        ArrayList<Campo> campos = new ArrayList<>();
        Campo c = new Campo(DESAFIO, nomeDesafio.getBytes());
        campos.add(c);
        sendPDU(QUIT, campos);
        PDU p = receivePDU();

    }

    public static Set<Utilizador> menuEnd(String nomeDesafio) throws IOException, SocketTimeoutException, ServerUnreachableException {
        //TreeMap<String, Integer> lista = new TreeMap<>();
        TreeSet<Utilizador> users = new TreeSet<>(new CompareUsersByPoints());
        ArrayList<Campo> campos = new ArrayList<>();
        Campo c = new Campo(DESAFIO, nomeDesafio.getBytes());
        campos.add(c);
        sendPDU(END, campos);

        PDU p = receivePDUNoExeception();
        for (int i = 1; i < p.getNumCampos(); i += 2) {
            users.add(new Utilizador(new String(p.getCampo(i).getValor()), PDU.byteArrayToInt(p.getCampo(i + 1).getValor())));
            //lista.put(new String(p.getCampo(i).getValor()), PDU.byteArrayToInt(p.getCampo(i + 1).getValor()));
        }

        return users;

    }

    /////SERVIDOR EM FALTA
    public static void menuDelete(String desafio) throws IOException, ChallengeException, SocketTimeoutException, ServerUnreachableException {
        ArrayList<Campo> campos = new ArrayList<>();
        campos.add(new Campo(DESAFIO, desafio.getBytes()));
        sendPDU(DELETE_CHALLENGE, campos);
        //boolean flag;
        /*PDU pacote = receivePDU();
        Desafio d = null;
        if (pacote.getCampo(0).getId() == 255) {
            throw new ChallengeException("Data");
        } else {
            String nome = new String(pacote.getCampo(0).getValor());
            byte[] b = pacote.getCampo(1).getValor();
            byte[] ano = new byte[]{b[0], b[1]};
            byte[] mes = new byte[]{b[2], b[3]};
            byte[] dia = new byte[]{b[4], b[5]};
            b = pacote.getCampo(2).getValor();
            byte[] hora = new byte[]{b[0], b[1]};
            byte[] min = new byte[]{b[2], b[3]};
            byte[] seg = new byte[]{b[4], b[5]};
            // d = new Desafio(nome, ano, mes, dia, hora, min, seg);

        }
        return d;*/
    }

    public static Resposta answer(String nDesafio, int escolha, int nQuestao, int tempo) throws IOException, SocketTimeoutException, ServerUnreachableException {
        ArrayList<Campo> campos = new ArrayList<>();
        Campo c = new Campo(ESCOLHA, new byte[]{(byte) escolha});
        campos.add(c);
        c = new Campo(DESAFIO, nDesafio.getBytes());
        campos.add(c);
        c = new Campo(NQUESTAO, new byte[]{(byte) nQuestao});
        campos.add(c);

        c = new Campo(TIME, new byte[]{(byte) tempo});
        campos.add(c);

        sendPDU(ANSWER, campos);

        PDU pacote = receivePDU();

        int resposta = pacote.getCampo(2).getValor()[0];
        int pontos = pacote.getCampo(3).getValor()[0];

        boolean flag = pacote.getCampo(4).getId() != 250;

        Resposta r = new Resposta(nQuestao, resposta, pontos, flag);

        return r;
    }

    public static Pergunta proximaPergunta(String nomeDesafio, int nQuestao) throws IOException, SocketException, SocketTimeoutException, UnsupportedAudioFileException, LineUnavailableException, InsuficientPlayersException, ServerUnreachableException {
        ArrayList<Campo> campos = new ArrayList<>();
        Campo c = new Campo(NOME, nomeDesafio.getBytes());
        campos.add(c);
        c = new Campo(NQUESTAO, new byte[]{(byte) nQuestao});
        campos.add(c);
        sendPDU(NEXT_QUESTION, campos);

        Pergunta p = null;
        p = jogar(false);
        return p;

    }

    private static Utilizador getNextUserRanking(PDU pacote) {
        Utilizador u = null;
        String nome = new String(pacote.getCampo(0).getValor());
        int score = PDU.byteArrayToInt(pacote.getCampo(1).getValor());
        return new Utilizador(nome, score);
    }

    //SERVIDOR EM FALTA
    public static List<Utilizador> menuListRankings() throws IOException, SocketTimeoutException, ServerUnreachableException {
        ArrayList<Utilizador> lista = new ArrayList<>();
        sendPDU(LIST_RANKING, null);
        PDU pacote = receivePDU();
        Utilizador u;
        u = getNextUserRanking(pacote);
        if (u != null) {
            lista.add(u);
        }

        while (pacote.getNumCampos() == 3) {
            pacote = receivePDU();
            u = getNextUserRanking(pacote);
            if (u != null) {
                lista.add(u);
            }
        }

        return lista;

    }

    private static Desafio getNextDesafio(PDU pacote) {
        //int numCampos = pacote.getNumCampos();
        Desafio d = null;
        String nome;

        if (pacote.getCampo(0).getId() != MusicClient.ERRO) {

            nome = new String(pacote.getCampo(0).getValor());

            byte[] b = pacote.getCampo(1).getValor();

            byte[] ano = {b[0], b[1], b[2]};
            byte mes = b[3];
            byte dia = b[4];
            /*byte[] mes = new byte[]{b[2], b[3]};
             byte[] dia = new byte[]{b[4], b[5]};

             int anoAux = 2000 + Integer.parseInt(new String(ano));
             int aux = anoAux % 100;
             int pri = aux / 10;
             int sec = aux % 10;
             ano = new byte[]{(byte) pri, (byte) sec};
             int mes2 = Integer.parseInt(new String(mes));
             int dia2 = Integer.parseInt(new String(dia));*/

            b = pacote.getCampo(2).getValor();
            byte hora = b[0];
            byte min = b[1];
            byte seg = b[2];
            /*byte[] hora = new byte[]{b[0], b[1]};
             byte[] min = new byte[]{b[2], b[3]};
             byte[] seg = new byte[]{b[4], b[5]};

             int hora2 = Integer.parseInt(new String(hora));
             int min2 = Integer.parseInt(new String(min));
             int seg2 = Integer.parseInt(new String(seg));*/

            d = new Desafio(nome, null, ano, mes, dia, hora, min, seg);
            //d = new Desafio(name, ano, PDU.intToByteArray(mes2), PDU.intToByteArray(dia2), PDU.intToByteArray(hora2), PDU.intToByteArray(min2), PDU.intToByteArray(seg2));  */
            d.setDataProperty();
            d.setHoraProperty();
        }
        return d;
    }

    public static ArrayList<Desafio> menuListChallenge() throws IOException, SocketTimeoutException, ServerUnreachableException {

        sendPDU(LIST_CHALLENGES, null);
        PDU pacote = receivePDU();
        Desafio d;
        ArrayList<Desafio> desafios = new ArrayList<>();

        d = getNextDesafio(pacote);
        if (d != null) {
            desafios.add(d);
        }
        while (pacote.getNumCampos() == 4) {
            pacote = receivePDU();
            d = getNextDesafio(pacote);
            if (d != null) {
                desafios.add(d);
            }
        }

        return desafios;
    }

    public static boolean acceptChallenge(String nome) throws IOException, SocketTimeoutException, ServerUnreachableException {
        ArrayList<Campo> campos = new ArrayList<>();
        Campo m = new Campo(DESAFIO, nome.getBytes());
        campos.add(m);
        sendPDU(ACCEPT_CHALLENGE, campos);
        PDU pacote = receivePDU();

        return pacote.getCampo(0).getValor()[0] == 0; /*try {
         System.out.println("vamos jogar");
         jogar();
         } catch (SocketException | UnsupportedAudioFileException | LineUnavailableException | InsuficientPlayersException ex) {
         Logger.getLogger(MusicClient.class
         .getName()).log(Level.SEVERE, null, ex);
         }*/

    }

    public static Desafio menuMakeChallenge(String nome, String alc) throws IOException, SocketTimeoutException, ServerUnreachableException {
        ArrayList<Campo> campos = new ArrayList<>();
        Campo m = new Campo(DESAFIO, nome.getBytes());
        campos.add(m);
        m = new Campo(ALCUNHA, alc.getBytes());
        campos.add(m);
        sendPDU(MAKE_CHALLENGE, campos);
        PDU pacote = receivePDU();

        Desafio d = null;

        if (pacote.getCampo(0).getId() != ERRO) {
            String name = new String(pacote.getCampo(0).getValor());

            byte[] b = pacote.getCampo(1).getValor();

            byte[] ano = {b[0], b[1], b[2]};
            byte mes = b[3];
            byte dia = b[4];
            /*byte[] mes = new byte[]{b[2], b[3]};
             byte[] dia = new byte[]{b[4], b[5]};

             int anoAux = 2000 + Integer.parseInt(new String(ano));
             int aux = anoAux % 100;
             int pri = aux / 10;
             int sec = aux % 10;
             ano = new byte[]{(byte) pri, (byte) sec};
             int mes2 = Integer.parseInt(new String(mes));
             int dia2 = Integer.parseInt(new String(dia));*/
            System.err.println("DataC: " + new String(b));

            b = pacote.getCampo(2).getValor();
            byte hora = b[0];
            byte min = b[1];
            byte seg = b[2];
            /*byte[] hora = new byte[]{b[0], b[1]};
             byte[] min = new byte[]{b[2], b[3]};
             byte[] seg = new byte[]{b[4], b[5]};

             int hora2 = Integer.parseInt(new String(hora));
             int min2 = Integer.parseInt(new String(min));
             int seg2 = Integer.parseInt(new String(seg));*/

            d = new Desafio(name, alc, ano, mes, dia, hora, min, seg);
            //d = new Desafio(name, ano, PDU.intToByteArray(mes2), PDU.intToByteArray(dia2), PDU.intToByteArray(hora2), PDU.intToByteArray(min2), PDU.intToByteArray(seg2));  */
            d.setDataProperty();
            d.setHoraProperty();
        }

        return d;
    }

    public static Pergunta jogar(boolean quit) throws SocketException, SocketTimeoutException, IOException, UnsupportedAudioFileException, LineUnavailableException, InsuficientPlayersException, ServerUnreachableException {
        if (!quit) {
            byte[] b, res, data;
            PDU pacote;
            int num = 0;
            int nQuestao;
            int tam;
            String nome, pergunta;
            ArrayList<String> respostas = new ArrayList<>();
            TreeMap<Integer, PDU> blocosImagem = new TreeMap<>();
            TreeMap<Integer, PDU> blocosMusica = new TreeMap<>();
            Pergunta p = null;
            try {
                // 1ª pacote -> estrutura da pergunta
                pacote = receivePDU();

                System.err.println("Recebi pergunta");
                nome = new String(pacote.getCampo(0).getValor());
                int id = pacote.getCampo(1).getId();
                // se == 255 -> Não existem jogadores suficientes -> Exception
                // se não -> jogar

                if (id != 255) {
                    nQuestao = pacote.getCampo(1).getValor()[0];
                    pergunta = new String(pacote.getCampo(2).getValor());
                    respostas.add(new String(pacote.getCampo(4).getValor()));
                    respostas.add(new String(pacote.getCampo(6).getValor()));
                    respostas.add(new String(pacote.getCampo(8).getValor()));

                    // 2ª parte -> receber pacotes de uma imagem
                    blocosImagem = (TreeMap) recebeBlocos(IMAGEM);
                    System.err.println("Recebi imagem");
                    checkBlocos(blocosImagem, nome, nQuestao, 16);
                    String fImage = constroiFicheiroImagem(blocosImagem);

                    // 3º parte -> receber pacotes de uma musica
                    blocosMusica = (TreeMap) recebeBlocos(AUDIO);
                    System.err.println("Recebi musica");
                    checkBlocos(blocosMusica, nome, nQuestao, 18);
                    String fMusic = constroiFicheiroAudio(blocosMusica);

                    p = new Pergunta(fMusic, fImage, pergunta, respostas, -1);
                } else {
                    throw new InsuficientPlayersException(new String(pacote.getCampo(1).getValor()));
                }
            } catch (SocketTimeoutException e) {
                sendPDU(QUIT, null);
            }

            return p;
        } else {
            return null;
        }
    }

    private static Map<Integer, PDU> recebeBlocos(int tipo) throws IOException, SocketTimeoutException, ServerUnreachableException {
        byte[] b;
        PDU pacote;
        int num;
        TreeMap<Integer, PDU> blocos = new TreeMap<>();
        // 2º pacote -> primeiro pacote de com uma imagem

        pacote = receivePDUNoExeception();
        int numero = pacote.getCampo(4).getId();

        while (numero == 254 && pacote.getCampo(3).getId() == tipo) {
            num = (byte) pacote.getCampo(2).getValor()[0];
            blocos.put(num, pacote);
            pacote = receivePDUNoExeception();
            numero = pacote.getCampo(4).getId();
        }
        if (numero == 250) {
            b = pacote.getCampo(2).getValor();
            num = (byte) b[0];
            blocos.put(num, pacote);
        }
        /*
         for (Integer c : blocos.keySet()) {
         System.out.println(c);
         }
         */
        return blocos;
    }

    private static void askBlockRetransmit(TreeMap<Integer, PDU> blocos, int n, String nome, int nQuestao, int tipo) throws SocketTimeoutException, IOException, ServerUnreachableException {
        ArrayList<Campo> campos = new ArrayList<>();
        int num;
        Campo c = new Campo(DESAFIO, nome.getBytes());
        campos.add(c);
        c = new Campo(NQUESTAO, PDU.intToByteArray(nQuestao));
        campos.add(c);
        c = new Campo(tipo, new byte[]{(byte) tipo});
        campos.add(c);
        c = new Campo(BLOCO, new byte[]{(byte) n});
        campos.add(c);
        sendPDU(RETRANSMIT, campos);
        PDU pacote = receivePDU();
        byte[] b = pacote.getCampo(2).getValor();
        num = PDU.byteArrayToInt(b);
        blocos.put(num, pacote);
    }

    private static void checkBlocos(TreeMap<Integer, PDU> blocos, String nome, int nQuestao, int tipo) throws ServerUnreachableException {
        int i;
        try {
            for (i = 1; i < blocos.lastKey(); i++) {
                if (!blocos.containsKey(i)) {
                    askBlockRetransmit(blocos, i, nome, nQuestao, tipo);
                }
            }
            while (blocos.get(blocos.lastKey()).getCampo(4).getId() == 254) {
                askBlockRetransmit(blocos, blocos.lastKey() + 1, nome, nQuestao, tipo);
            }
        } catch (IOException ex) {
            System.out.println("excecao!!!");
        }

    }

    private static String constroiFicheiroAudio(Map<Integer, PDU> blocos) throws UnsupportedAudioFileException, IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            for (PDU a : blocos.values()) {
                os.write(a.getCampo(3).getValor());

            }
        } catch (IOException ex) {
            Logger.getLogger(MusicClient.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        File f = new File("m.mp3");
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(os.toByteArray());

        return f.getAbsolutePath();
    }

    private static String constroiFicheiroImagem(TreeMap<Integer, PDU> blocosImagem) throws FileNotFoundException, IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            for (PDU a : blocosImagem.values()) {
                os.write(a.getCampo(3).getValor());

            }
        } catch (IOException ex) {
            Logger.getLogger(MusicClient.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        File f = new File("i.jpg");
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(os.toByteArray());
        return f.getAbsolutePath();
    }

}

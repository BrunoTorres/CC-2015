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
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

class MusicClient {

    private static final int HELLO               = 1;
    private static final int REGISTER            = 2;
    private static final int LOGIN               = 3;
    private static final int LOGOUT              = 4;
    private static final int QUIT                = 5;
    private static final int END                 = 6;
    private static final int LIST_CHALLENGES     = 7;
    private static final int MAKE_CHALLENGE      = 8;
    private static final int ACCEPT_CHALLENGE    = 9;
    private static final int DELETE_CHALLENGE    = 10;
    private static final int ANSWER              = 11;
    private static final int RETRANSMIT          = 12;
    private static final int LIST_RANKING        = 13;
    
    
    private static final int OK                  = 0;
    private static final int ERRO                = 255;
    private static final int CONTINUA            = 254;
    private static final int NOME                = 1;
    private static final int ALCUNHA             = 2;
    private static final int PASSWORD            = 3;
    private static final int DATA                = 4;
    private static final int HORA                = 5;
    private static final int ESCOLHA             = 6;
    private static final int DESAFIO             = 7;
    private static final int NQUESTAO            = 10;
    private static final int QUESTAO             = 11;
    private static final int NRESPOSTA           = 12;
    private static final int RESPOSTA            = 13;
    private static final int CERTA               = 14;
    private static final int PONTOS              = 15;
    private static final int IMAGEM              = 16;
    private static final int BLOCO               = 17;
    private static final int AUDIO               = 18;
    private static final int SCORE               = 20;

    private static final Scanner in = new Scanner(System.in);
    private static int label = 1;
    private static DatagramPacket sendPacket;
    private static BufferedReader inFromUser;
    private static byte[] sendData;
    private static byte[] receiveData;
    private static InetAddress IPAddress;
    private static DatagramSocket clientSocket;
    private static DatagramPacket receivePacket;

    //construtor com timeout
    public static void main(String args[]) throws Exception {

        try {
            clientSocket = new DatagramSocket();
            IPAddress = InetAddress.getByName("localhost");
            receiveData = new byte[50000];
            menuInit();
            //menuRegista("patricia", "tita", "123");                                           funciona
            Utilizador u = new Utilizador("patricia", "tita", "123".getBytes(), null, 0);
            menuLogin(u);                                                                     //funciona
            menuMakeChallenge("desafio1");
            
            /*
            
            
            PDU hello = new PDU(label, (byte) 01);
            byte[] data;
            

            PDU login = new PDU(12, (byte) 3);
            Campo m1 = new Campo(2, "tita".getBytes());
            Campo p = new Campo(3, "123".getBytes());

            login.addCampo(m1);
            login.addCampo(p);

            receivePacket = new DatagramPacket(receiveData, receiveData.length);

            clientSocket.receive(receivePacket);
            int tam = receivePacket.getLength();
            receivePacket.setLength(receivePacket.getLength());
            byte[] res = receivePacket.getData();
            data = new byte[tam];
            System.arraycopy(res, 0, data, 0, tam);
            PDU pacote = new PDU(data);
            String nome = new String(pacote.getCampo(0).getValor());
            System.out.println("FROM SERVER:");
            //partir desafio mostra   
            //            
            System.out.println("Mensagem: " + nome);
            for (byte b : data) {
                System.out.print(b + "|");
            }
            System.out.println("");

            label++;

            menuMakeChallenge("desafio1");
            menuListChallenge();
            acceptChallenge("desafio1");
*/
        } catch (IOException | UserInexistenteException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void sendPDU(int id, ArrayList<Campo> campos) throws IOException {
        byte[] data;
        label++;
        PDU packet = new PDU(label, (byte) id);
        if(campos!=null)
            for(Campo c:campos){
                packet.addCampo(c);
            }
        data = packet.getBytes();
        sendPacket = new DatagramPacket(data, data.length, IPAddress, 55555);
        clientSocket.send(sendPacket);
        label++;
        
    }

    private static PDU receivePDU() throws IOException, SocketTimeoutException {
        int tam;
        byte[] data, res;
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

    public static void menuInit() throws IOException {
        System.out.println("hello");
        sendPDU(HELLO,null);
        PDU pacote = receivePDU();
        System.out.println("cenas "+ pacote.getCampo(0).getId());
    }

    public static boolean menuRegista(String nome, String al, String pass) throws IOException, SocketTimeoutException {
        ArrayList<Campo> campos=new ArrayList<>();
        Campo nomeCampo=new Campo(NOME,nome.getBytes());
        campos.add(nomeCampo);
        Campo alcunha= new Campo(ALCUNHA,al.getBytes());
        campos.add(alcunha);
        Campo password= new Campo(PASSWORD,pass.getBytes());
        campos.add(alcunha);
        sendPDU(REGISTER,campos);
        PDU pacote = receivePDU();
        System.out.println(pacote.getCampo(0).getId() != 255);
        return pacote.getCampo(0).getId() != 255;

    }

    public static Utilizador menuLogin(Utilizador u) throws IOException, SocketTimeoutException, UserInexistenteException {
        ArrayList<Campo> campos=new ArrayList<>();
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

    public static void menuLogout() throws SocketTimeoutException, IOException {
       sendPDU(LOGOUT, null);
       PDU p = receivePDU();
    }

    public static void menuQuit() throws IOException {
        sendPDU(QUIT, null);
        PDU p = receivePDU();

    }
    
    ///SERVIDOR EM FALTA
    public static Map<String,Integer> menuEnd() throws IOException {   
        TreeMap<String,Integer> lista= new TreeMap<>();
        sendPDU(END, null);
        PDU p = receivePDU();
        for(int i=1;i<p.getNumCampos();i+=2){
            lista.put(new String(p.getCampo(i).getValor()), PDU.byteArrayToInt(p.getCampo(i+1).getValor()));
        }
        
        return lista;
       

    }
    /////SERVIDOR EM FALTA
    public static Desafio menuDelete() throws IOException, ChallengeException{   
        sendPDU(DELETE_CHALLENGE, null);
        boolean flag;
        PDU pacote = receivePDU();
        Desafio d;
        if(pacote.getCampo(0).getId()==255)
            throw new ChallengeException("Data");
        else{
            String nome = new String(pacote.getCampo(0).getValor());
            byte[] b = pacote.getCampo(1).getValor();
            byte[] ano = new byte[]{b[0], b[1]};
            byte[] mes = new byte[]{b[2], b[3]};
            byte[] dia = new byte[]{b[4], b[5]};
            b = pacote.getCampo(2).getValor();
            System.out.println("c  " + ano[0]);
            byte[] hora = new byte[]{b[0], b[1]};
            byte[] min = new byte[]{b[2], b[3]};
            byte[] seg = new byte[]{b[4], b[5]};
            d = new Desafio(nome, ano, dia, mes, hora, min, seg);
        
        }
        return d;
    }
    //SERVIDOR EM FALTA
    public static int answer(String nDesafio,int escolha,int nQuestao) throws IOException{
        ArrayList<Campo>campos = new ArrayList<>();
        Campo c = new Campo(ESCOLHA,new byte[]{(byte)escolha});
        campos.add(c);
        c= new Campo(DESAFIO,nDesafio.getBytes());
        campos.add(c);
        c= new Campo(NQUESTAO,new byte[]{(byte)nQuestao});
        campos.add(c);
        
        sendPDU(ANSWER, campos);
        
        PDU pacote = receivePDU();
        
        int resposta = pacote.getCampo(0).getValor()[0];
        int pontos = pacote.getCampo(1).getValor()[0];
        
        
        return pontos;    
        
    } 
    
 
    //SERVIDOR EM FALTA
    public static Map<String,Integer> menuListRankings() throws IOException {   
        TreeMap<String,Integer> lista= new TreeMap<>();
        sendPDU(LIST_RANKING, null);
        PDU p = receivePDU();
        for(int i=1;i<p.getNumCampos();i+=2){
            lista.put(new String(p.getCampo(i).getValor()), PDU.byteArrayToInt(p.getCampo(i+1).getValor()));
        }
        
        return lista;
       

    }
    public static ArrayList<Desafio> menuListChallenge() throws IOException, SocketTimeoutException {
       
        sendPDU(LIST_CHALLENGES, null);
        PDU pacote = receivePDU();

        int numCampos = pacote.getNumCampos();
        System.out.println("nu campos " + numCampos);
        Desafio d;
        String nome;
        ArrayList<Desafio> desafios = new ArrayList<>();
        for (int i = 0; i < numCampos; i += 3) {
            nome = new String(pacote.getCampo(i).getValor());
            System.out.println("nome = " + nome);
            byte[] b = pacote.getCampo(i + 1).getValor();
            byte[] ano = new byte[]{b[0], b[1]};
            byte[] mes = new byte[]{b[2], b[3]};
            byte[] dia = new byte[]{b[4], b[5]};
            b = pacote.getCampo(i + 2).getValor();
            System.out.println("c  " + ano[0]);
            byte[] hora = new byte[]{b[0], b[1]};
            byte[] min = new byte[]{b[2], b[3]};
            byte[] seg = new byte[]{b[4], b[5]};
            d = new Desafio(nome, ano, dia, mes, hora, min, seg);
            desafios.add(d);
        }
        return desafios;

    }

    public static void acceptChallenge(String nome) throws IOException, SocketTimeoutException {
        ArrayList<Campo>campos = new ArrayList<>();
        Campo m = new Campo(DESAFIO, nome.getBytes());
        campos.add(m);
        sendPDU(ACCEPT_CHALLENGE, campos);
        PDU pacote = receivePDU();
        System.out.println(" idiiii " + pacote.getCampo(0).getValor()[0]);
        if (pacote.getCampo(0).getValor()[0] == 0) {
            try {
                System.out.println("vamos jogar");
                jogar();
            } catch (SocketException | UnsupportedAudioFileException | LineUnavailableException | InsuficientPlayersException ex) {
                Logger.getLogger(MusicClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public static void menuMakeChallenge(String nome) throws IOException, SocketTimeoutException {
        ArrayList<Campo>campos = new ArrayList<>();
        Campo m = new Campo(7, nome.getBytes());
        campos.add(m);
        sendPDU(MAKE_CHALLENGE, campos);
        System.out.println("chegouuu");
        PDU pacote = receivePDU();

        nome = new String(pacote.getCampo(0).getValor());
        System.out.println("Desafio: " + nome);
        System.out.println("Data: " + new String(pacote.getCampo(1).getValor()));

        try {
            //// IFACE CHAMA O JOGAR
            jogar();
        } catch (SocketException | UnsupportedAudioFileException | LineUnavailableException | InsuficientPlayersException ex) {
            Logger.getLogger(MusicClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void jogar() throws SocketException, SocketTimeoutException, IOException, UnsupportedAudioFileException, LineUnavailableException, InsuficientPlayersException {
        byte[] b, res, data;
        PDU pacote;
        int num = 0;
        int nQuestao;
        int tam;
        String nome, pergunta;
        ArrayList<String> respostas = new ArrayList<>();
        TreeMap<Integer, byte[]> blocosImagem;
        TreeMap<Integer, byte[]> blocosMusica;
        try {
            // 1ª pacote -> estrutura da pergunta
            pacote =receivePDU();
            nome = new String(pacote.getCampo(0).getValor());
            System.out.println("Desafio: " + nome);
            int id = pacote.getCampo(1).getId();
            // se == 255 -> Não existem jogadores suficientes -> Exception
            // se não -> jogar

            //if (id != 255) {
            nQuestao = pacote.getCampo(1).getValor()[0];
            pergunta = new String(pacote.getCampo(2).getValor());
            respostas.add(new String(pacote.getCampo(4).getBytes()));
            respostas.add(new String(pacote.getCampo(6).getBytes()));
            respostas.add(new String(pacote.getCampo(8).getBytes()));
            System.out.println("Nome: " + nome);
            System.out.println(pergunta);

            for (String s : respostas) {
                System.out.println(s);
            }

            // 2ª parte -> receber pacotes de uma imagem
            blocosImagem = (TreeMap) recebeBlocos();
            checkBlocos(blocosImagem, nome, nQuestao, 16);
            String fImage = constroiFicheiroImagem(blocosImagem);

            // 3º parte -> receber pacotes de uma musica
            blocosMusica = (TreeMap) recebeBlocos();
            checkBlocos(blocosMusica, nome, nQuestao, 18);
            String fMusic = constroiFicheiroAudio(blocosMusica);
            //  }
            //  else{
            //      throw new InsuficientPlayersException(new String(pacote.getCampo(2).getValor()));
            // }
        } catch (SocketTimeoutException e) {
            sendPDU(QUIT, null);
        }
    }

    private static Map<Integer, byte[]> recebeBlocos() throws IOException, SocketTimeoutException {
        byte[] b ;
        PDU pacote;
        int num;
        TreeMap<Integer, byte[]> blocos = new TreeMap<>();
        // 2º pacote -> primeiro pacote de com uma imagem
        
        pacote = receivePDU();
        int i = 1;
        int numero = pacote.getCampo(4).getId();
        
        
        while (numero == 254) {
            b = pacote.getCampo(2).getValor();
         
            num = PDU.byteArrayToInt(b);
            System.out.println("##################### "+ num);
            blocos.put(num, pacote.getCampo(3).getValor());
            pacote = receivePDU();
            numero = pacote.getCampo(4).getId();
        }
        b = pacote.getCampo(2).getValor();
        num = PDU.byteArrayToInt(b);

        blocos.put(num, pacote.getCampo(3).getValor());
/*
        for (Integer c : blocos.keySet()) {
            System.out.println(c);
        }
*/
        return blocos;
    }

    private static void askBlockRetransmit(TreeMap<Integer, byte[]> blocos, int n, String nome, int nQuestao, int tipo) throws SocketTimeoutException, IOException {
        ArrayList<Campo>campos= new ArrayList<>();
        int num;
        Campo c = new Campo(DESAFIO, nome.getBytes());
        campos.add(c);
        c = new Campo(NQUESTAO, PDU.intToByteArray(nQuestao));
        campos.add(c);
        c = new Campo(tipo, new byte[]{(byte) tipo});
        campos.add(c);
        c = new Campo(BLOCO, PDU.intToByteArray(n));
        campos.add(c);
        sendPDU(RETRANSMIT, campos);



        PDU pacote = receivePDU();
        byte[] b = pacote.getCampo(2).getValor();
        PDU.printBytes(b);
        num = PDU.byteArrayToInt(b);
        blocos.put(num, pacote.getCampo(3).getValor());
       
        System.out.println("b.size "+ pacote.getCampo(3).getValor().length);
    }

    private static void checkBlocos(TreeMap<Integer, byte[]> blocos, String nome, int nQuestao, int tipo) {
        int i;
        try {
            for (i = 1; i < blocos.lastKey(); i++) {
                if (!blocos.containsKey(i)) {
                  //  System.out.println("NO BLOCK: " + i);
                    askBlockRetransmit(blocos, i, nome, nQuestao, tipo);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(MusicClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static String constroiFicheiroAudio(Map<Integer, byte[]> blocos) throws UnsupportedAudioFileException, IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            for (byte[] a : blocos.values()) {
                os.write(a);
            }
        } catch (IOException ex) {
            Logger.getLogger(MusicClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        File f = new File("m.mp3");
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(os.toByteArray());

        return f.getAbsolutePath();
    }

    private static String constroiFicheiroImagem(TreeMap<Integer, byte[]> blocosImagem) throws FileNotFoundException, IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            for (byte[] a : blocosImagem.values()) {
                os.write(a);
            }
        } catch (IOException ex) {
            Logger.getLogger(MusicClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        File f = new File("i.jpg");
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(os.toByteArray());
        return f.getAbsolutePath();
    }
}

package musicgame;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.converter.LocalDateTimeStringConverter;

class MusicClient {

    private static final Scanner in = new Scanner(System.in);
    private static int label = 1;
    private static DatagramPacket sendPacket;
    private static BufferedReader inFromUser;
    private static byte[] sendData;
    private static byte[] receiveData;
    private static InetAddress IPAddress;
    private static DatagramSocket clientSocket;
    private static DatagramPacket receivePacket;

    public static void main(String args[]) throws Exception {

        try {
            clientSocket = new DatagramSocket();
            IPAddress = InetAddress.getByName("localhost");
            PDU hello = new PDU(label, (byte) 01);

            receiveData = new byte[50000];

            PDU login = new PDU(12, (byte) 3);
            Campo m1 = new Campo(2, "tita".getBytes());
            Campo p = new Campo(3, "123".getBytes());

            login.addCampo(m1);
            login.addCampo(p);
            byte[] data1 = login.getBytes();
            for (byte b : data1) {
                System.out.print(b + "|");
            }

            byte[] data;

            sendPacket = new DatagramPacket(data1, data1.length, IPAddress, 55555);
            clientSocket.send(sendPacket);

            label++;
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
            //RECEBER cenas
            /*int i = 0;
             while (true) {
             receivePacket = new DatagramPacket(receiveData, receiveData.length);
             clientSocket.receive(receivePacket);
             tam = receivePacket.getLength();
             receivePacket.setLength(receivePacket.getLength());
             res = receivePacket.getData();
             data = new byte[tam];
             System.arraycopy(res, 0, data, 0, tam);
             pacote = new PDU(data);
             //nome = new String(pacote.getCampo(0).getValor());
             int n = (int) pacote.getNumCampos();
             byte[] b = pacote.getCampo(0).getValor();
             for (byte bb : b) {
             System.out.print(((int) bb) + "|");
             }
             System.out.println("FROM SERVER:");
             //partir desafio mostra   
             //            
             System.out.println("Mensagem: " + i + " Nome= " + n);
             i++;
             // for (byte b : data) {
             //     System.out.print(b + "|");
             // }
             }
             */
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // Função que imprime o menu principal da aplicação e dependendo da escolha segue para o caminho correto
    public static void menuPrincipal() throws IOException, ClassNotFoundException {
        System.out.println("################# Menu de Principal ######################");
        System.out.println("#                                                        #");
        System.out.println("#   Bem Vindo                                            #");
        System.out.println("#                                                        #");
        System.out.println("#   1 - Registar                                         #");
        System.out.println("#   2 - Login                                            #");
        System.out.println("#   0 - Sair                                             #");
        System.out.println("#   Escolha uma opção                                    #");
        System.out.println("##########################################################");
        String opt = in.next();
        while (true) {
            switch (opt) {
                case "1":
                    menuRegista();
                    break;
                case "2":
                    menuLogin();
                    break;
                case "0":
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opcão inválida!");
                    menuPrincipal();
                    break;
            }
        }
    }

    private static void menuLogin() {

        PDU login = new PDU(12, (byte) 3);
        Campo m = new Campo(2, "Manuel".getBytes());
        Campo p = new Campo(3, "123".getBytes());

        login.addCampo(m);
        login.addCampo(p);

    }

    private static void menuMakeChallenge(String nome) throws IOException {

        PDU fazDesafio = new PDU(label, (byte) 8);
        Campo m = new Campo(7, nome.getBytes());
        byte[] b;
        int num = 0;
        int nQuestao;

        fazDesafio.addCampo(m);
        byte[] data = fazDesafio.getBytes();
        sendPacket = new DatagramPacket(data, data.length, IPAddress, 55555);
        clientSocket.send(sendPacket);
        label++;

        receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        int tam = receivePacket.getLength();
        receivePacket.setLength(receivePacket.getLength());
        byte[] res = receivePacket.getData();
        data = new byte[tam];
        System.arraycopy(res, 0, data, 0, tam);
        PDU pacote = new PDU(data);

        nome = new String(pacote.getCampo(0).getValor());
        System.out.println("Desafio: " + nome);
        System.out.println("Data: " + new String(pacote.getCampo(1).getValor()));

        //// IFACE CHAMA O JOGAR
        jogar();

    }

    private static void jogar() throws SocketException, IOException {
        byte[] b, res, data;
        PDU pacote;
        int num = 0;
        int nQuestao;
        int tam;
        String nome, pergunta;
        ArrayList<String> respostas = new ArrayList<>();
        TreeMap<Integer, byte[]> blocosImagem = new TreeMap<>();
        TreeMap<Integer, byte[]> blocosMusica = new TreeMap<>();
        clientSocket.setSoTimeout(10000);
        try {
            // 1ª pacote -> estrutura da pergunta
            receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            tam = receivePacket.getLength();
            receivePacket.setLength(receivePacket.getLength());
            res = receivePacket.getData();
            data = new byte[tam];
            System.arraycopy(res, 0, data, 0, tam);
            pacote = new PDU(data);
            nome = new String(pacote.getCampo(0).getValor());
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
            
            blocosImagem = (TreeMap) recebeBlocos();
            
            blocosMusica = (TreeMap) recebeBlocos();

            // 2º pacote -> primeiro pacote de com uma imagem
            checkBlocos(blocosMusica, nome, nQuestao, 18);
            
            
            
            ////   ACABAR RETRANSMIT ///////////////
            
            
            
            
        } catch (SocketTimeoutException e) {
            PDU tout = new PDU(label, 0);
            tout.addCampo(new Campo(255, new byte[]{0}));
            sendPacket = new DatagramPacket(tout.getBytes(), tout.getBytes().length, IPAddress, 55555);
            clientSocket.send(sendPacket);
        }
    }

    private static Map<Integer, byte[]> recebeBlocos() throws IOException, SocketTimeoutException {
        byte[] b, res, data;
        PDU pacote;
        int num;
        int tam;
        TreeMap<Integer, byte[]> blocos = new TreeMap<>();
        clientSocket.setSoTimeout(10000);
        // 2º pacote -> primeiro pacote de com uma imagem
        receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        tam = receivePacket.getLength();
        receivePacket.setLength(receivePacket.getLength());
        res = receivePacket.getData();
        data = new byte[tam];
        System.arraycopy(res, 0, data, 0, tam);
        pacote = new PDU(data);
        int i = 1;

        int numero = pacote.getCampo(4).getId() + 128;
        while (numero == 254) {
            b = pacote.getCampo(2).getValor();
            if (b.length > 1) {
                num = PDU.byteArrayToInt(b);
            } else {
                num = (int) b[0];
            }
            blocos.put(num, pacote.getCampo(3).getValor());
            receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            tam = receivePacket.getLength();
            receivePacket.setLength(receivePacket.getLength());
            res = receivePacket.getData();
            data = new byte[tam];
            System.arraycopy(res, 0, data, 0, tam);
            pacote = new PDU(data);
            numero = pacote.getCampo(4).getId() + 128;
        }
        b = pacote.getCampo(2).getValor();
        if (b.length > 1) {
            num = PDU.byteArrayToInt(b);
        } else {
            num = (int) b[0];
        }
        blocos.put(num, pacote.getCampo(4).getValor());

        for (Integer c : blocos.keySet()) {
            System.out.println(c);
        }

        return blocos;
    }

    private static void menuRegista() {
        System.out.println("#################### Registar Utilizador #####################");
        System.out.println("                                                              ");
        in.nextLine();
        System.out.println("   Defina um Nome                                             ");
        String nome = in.nextLine();
        System.out.println("  Defina uma Alcunha                                          ");
        String al = in.nextLine();
        System.out.println("  Defina uma password                                         ");
        String pass = in.nextLine();

        Campo name = new Campo(1, nome.getBytes());
        Campo alcunha = new Campo(2, al.getBytes());
        Campo password = new Campo(3, pass.getBytes());

        label++;
        PDU packet = new PDU(label, (byte) 01);
        packet.addCampo(name);
        packet.addCampo(alcunha);
        packet.addCampo(password);

    }

    private static void askBlockRetransmit(TreeMap<Integer, byte[]> blocos, int n, String nome, int nQuestao, int tipo) throws IOException {
        PDU ret = new PDU(label, (byte) 12);
        Campo c = new Campo(7, nome.getBytes());
        ret.addCampo(c);
        c = new Campo(10, PDU.intToByteArray(nQuestao));
        ret.addCampo(c);
        c = new Campo(tipo, new byte[]{(byte) tipo});
        ret.addCampo(c);
        c = new Campo(16, PDU.intToByteArray(n));
        ret.addCampo(c);

        sendPacket = new DatagramPacket(ret.getBytes(), ret.getBytes().length, IPAddress, 55555);
        clientSocket.send(sendPacket);

        receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        int num;
        int tam = receivePacket.getLength();
        receivePacket.setLength(receivePacket.getLength());
        byte[] res = receivePacket.getData();
        byte[] data = new byte[tam];
        System.arraycopy(res, 0, data, 0, tam);
        PDU pacote = new PDU(data);
        byte[] b = pacote.getCampo(1).getValor();
        num = PDU.byteArrayToInt(b);
        blocos.put(num, pacote.getCampo(1).getValor());

    }

    private static void checkBlocos(TreeMap<Integer, byte[]> blocos, String nome, int nQuestao, int tipo) {
        int i;
        try {
            for (i = 1; i < blocos.lastKey(); i++) {
                if (!blocos.containsKey(i)) {
                    System.out.println("NO BLOCK: " + i);
                    askBlockRetransmit(blocos, i, nome, nQuestao, tipo);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(MusicClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}

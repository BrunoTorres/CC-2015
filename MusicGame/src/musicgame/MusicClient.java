package musicgame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

class MusicClient {

    private static Scanner in = new Scanner(System.in);
    private static short label = 0;
    private static DatagramPacket sendPacket;
    private static BufferedReader inFromUser;
    private static byte[] sendData;// = new byte[1024];
    private static byte[] receiveData;

    public static void main(String args[]) throws Exception {
        try ( //inFromUser = new BufferedReader(new InputStreamReader(System.in));
                DatagramSocket clientSocket = new DatagramSocket()) {
            InetAddress IPAddress = InetAddress.getByName("localhost");
                 //receiveData = new byte[1024];

            //String sentence = inFromUser.readLine();
            // sendData = sentence.getBytes();
            //PDU hello = new PDU(label, (byte) 01);
            PDU login = new PDU((short) 12, (byte) 29);
            //byte[] data = hello.getBytes();
            Campo m = new Campo(2, "Manuel");
            Campo p = new Campo(3, "123");
            
            login.addCampo(m);
            login.addCampo(p);
            byte[] data = login.getBytes();
            System.out.println(login.toString());
            sendPacket = new DatagramPacket(data, data.length, IPAddress, 55555);
            
            for (byte b : data) {
                System.out.print(b + "|");
            }
            System.out.println();
            
            clientSocket.send(sendPacket);

            receiveData = new byte[1024];
            //RECEBER cenas
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            String modifiedSentence = new String(receivePacket.getData());
            System.out.println("FROM SERVER:");
            for (byte b : data) {
                System.out.print(b);
            }
            System.out.println();
        }
    }

    public static void menuPrincipal() throws IOException, ClassNotFoundException {

        System.out.println("#################### Menu de Principal ######################");
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

        Campo name = new Campo(1, nome);
        Campo alcunha = new Campo(2, al);
        Campo password = new Campo(3, pass);

        label++;
        PDU packet = new PDU(label, (byte) 01);
        packet.addCampo(name);
        packet.addCampo(alcunha);
        packet.addCampo(password);

    }

}

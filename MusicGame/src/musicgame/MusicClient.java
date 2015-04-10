package musicgame;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

class MusicClient {

    private static final Scanner in = new Scanner(System.in);
    private static short label = 0;
    private static DatagramPacket sendPacket;
    private static BufferedReader inFromUser;
    private static byte[] sendData;
    private static byte[] receiveData;

    public static void main(String args[]) throws Exception {
        try ( 
            DatagramSocket clientSocket = new DatagramSocket()) {
            InetAddress IPAddress = InetAddress.getByName("localhost");
            PDU hello = new PDU(label, (byte) 01);
            PDU login = new PDU((short) 12, (byte) 03);
        Campo m = new Campo(2, "Antonio");
        Campo p = new Campo(3, "patricia");
            
        login.addCampo(m);
        login.addCampo(p);
            byte[] data = login.getBytes();
           
            System.out.println();
            sendPacket = new DatagramPacket(data, data.length, IPAddress, 55555);
            clientSocket.send(sendPacket);
            
            receiveData = new byte[1024];
            
            //RECEBER cenas
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            String modifiedSentence = new String(receivePacket.getData());
            int tam = receivePacket.getLength();
            receivePacket.setLength(receivePacket.getLength());
            byte[] res = receivePacket.getData();
            data = new byte[tam];
            System.arraycopy(res, 0, data, 0, tam);  
            System.out.println("FROM SERVER:");
            /*for (byte b : data) {
                System.out.print(b + "|");
            }
            System.out.println();*/
        }
    }
    // Função que imprime o menu principal da aplicação e dependendo da escolha segue para o caminho correto
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
        
        PDU login = new PDU((short) 12, (byte) 29);
        Campo m = new Campo(2, "Manuel");
        Campo p = new Campo(3, "123");
            
        login.addCampo(m);
        login.addCampo(p);

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

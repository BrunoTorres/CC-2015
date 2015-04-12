package musicgame;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Scanner;

class MusicClient {

    private static final Scanner in = new Scanner(System.in);
    private static short label = 1;
    private static DatagramPacket sendPacket;
    private static BufferedReader inFromUser;
    private static byte[] sendData;
    private static byte[] receiveData;
    
    private static byte[] intToByteArray(short n) {
        byte[] res = new byte[2];

        int sec=n%10;
        int pri =n/10;

        res[0] = (byte)pri;
        res[1] = (byte)sec;

        /*for(byte b : res){
         System.out.print(b + "|");
         }
         System.out.println();*/
        return res;
    }

    public static void main(String args[]) throws Exception {
        
        try ( 
             
            DatagramSocket clientSocket = new DatagramSocket()) {
            InetAddress IPAddress = InetAddress.getByName("localhost");
            PDU hello = new PDU(label, (byte) 01);
            PDU fazDesafio = new PDU(label, (byte) 8);
            Campo m = new Campo(7, "desafio1".getBytes());
            
            fazDesafio.addCampo(m);
            byte[] data = fazDesafio.getBytes();
            for (byte b : data) {
                System.out.print(b + "|");
            }
            label++;
            
            sendPacket = new DatagramPacket(data, data.length, IPAddress, 55555);
            clientSocket.send(sendPacket);
            
            receiveData = new byte[1024];
            
            //RECEBER cenas
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
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
            System.out.println("Mensagem: "+nome);
            for (byte b : data) {
                System.out.print(b + "|");
            }
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
        
        PDU login = new PDU((short) 12, (byte) 3);
        Campo m = new Campo(2, "Manuel".getBytes());
        Campo p = new Campo(3, "123".getBytes());
            
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

        Campo name = new Campo(1, nome.getBytes());
        Campo alcunha = new Campo(2, al.getBytes());
        Campo password = new Campo(3, pass.getBytes());

        label++;
        PDU packet = new PDU(label, (byte) 01);
        packet.addCampo(name);
        packet.addCampo(alcunha);
        packet.addCampo(password);

    }

}

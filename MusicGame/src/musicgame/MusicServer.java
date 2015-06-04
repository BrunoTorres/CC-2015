package musicgame;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Scanner;

class MusicServer {

    private static BD bd;

    public static void main(String args[]) throws Exception {

        Scanner ler = new Scanner(System.in);
        int portaUDP, portaTCP, portaTCP2;
        String ipServer;
        //bd=new BD();
        //bd = new BD(p.getMusicPath(),p.getImagesPath()); 
        bd = new BD("C:\\Users\\patricia\\Desktop\\CC-2015\\Kit TP2-LEI-CC\\musica\\", "C:\\Users\\patricia\\Desktop\\CC-2015\\Kit TP2-LEI-CC\\imagens\\");
        //bd = new BD("C:\\Users\\John\\Documents\\Repos\\CC-2015\\MusicGame\\build\\classes\\musicgame\\musica\\", "C:\\Users\\John\\Documents\\Repos\\CC-2015\\MusicGame\\build\\classes\\musicgame\\imagens\\");
        //bd = new BD("/Users/brunopereira/Documents/SourceTree/CC/MusicGame/build/classes/musicgame/musica/", "/Users/brunopereira/Documents/SourceTree/CC/MusicGame/build/classes/musicgame/imagens/");

        //String passe = "123";
        //Utilizador u = new Utilizador("patricia", "tita", passe.getBytes(), null, -1);
        //bd.addUser(u);
        //u = new Utilizador("joao", "jmano", passe.getBytes(), null, -1);
        //bd.addUser(u);
        //bd.carregaPerguntas(p.getDesafiosPath().concat("desafio.txt"));
        bd.carregaPerguntas("C:\\Users\\patricia\\Desktop\\CC-2015\\desafio-000001.txt");
        //bd.carregaPerguntas("C:\\Users\\John\\Documents\\Repos\\CC-2015\\MusicGame\\build\\classes\\musicgame\\desafios\\desafio.txt");
        //bd.carregaPerguntas("/Users/brunopereira/Documents/SourceTree/CC/MusicGame/build/classes/musicgame/desafios/desafio.txt");


        System.out.println("Porta UDP para escuta:");
        portaUDP = ler.nextInt();
        System.out.println("Porta TCP para escuta:");
        portaTCP = ler.nextInt();
        bd.setPorta(portaTCP);

        String sv;
        System.out.println("Servidor secundário? Se sim introduza o IP, caso contrário insira 0");
        ler.nextLine();
        ipServer = ler.nextLine();
        System.out.println("");
        if (!ipServer.equals("0")) {
            System.out.println("Introduza a porta TCP");
            portaTCP2 = ler.nextInt();
            AtendimentoServidor as = new AtendimentoServidor(bd, portaTCP, ipServer, portaTCP2);
            as.start();
        } else {
            System.out.println("Principal");
            AtendimentoServidor as = new AtendimentoServidor(bd, portaTCP);
            as.start();
        }
        System.out.println("Vou abrir a thread cliente");
        AtendimentoCliente ac = new AtendimentoCliente(bd, portaUDP);
        ac.start();

    }
}

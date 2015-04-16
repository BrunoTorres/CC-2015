package musicgame;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bruno Pereira
 */
public class Atendimento extends Thread {

    private DatagramPacket receivePacket;
    private DatagramSocket sendSocket;
    private DatagramPacket sendPacket;
    private BD bd;
    byte[] receiveData;
    byte[] sendData;

    public Atendimento(DatagramPacket so, BD b) {
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
            //imgTeste();
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
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    /*private void imgTeste() throws IOException{
     File f = new File("C:\\Users\\John\\Pictures\\2.jpg");
     byte[] img = Files.readAllBytes(f.toPath());
     FileOutputStream fos = new FileOutputStream("C:\\Users\\John\\Pictures\\testeeee.jpg");
     fos.write(img);
     fos.close();
     }*/
    private void analisaPacote(byte[] data, InetAddress add, int port) {
        PDU reply;
        int s;
        Campo c;
        byte[] tl = {data[2], data[3]};
        System.out.println("Opcao:" + data[4]);
        switch (data[4]) {
            case 0:
                System.out.println("Reply");
                break;
            case 1:
                s = PDU.byteArrayToInt(tl);
                reply = new PDU(s, (byte) 0);
                c = new Campo(0, "OK".getBytes());
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
                c = new Campo(0, "OK".getBytes());
                reply.addCampo(c);
                responde(reply, add, port);
                break;
            case 5:
                s = PDU.byteArrayToInt(tl);
                reply = new PDU(s, (byte) 0);
                c = new Campo(0, "OK".getBytes());
                reply.addCampo(c);
                responde(reply, add, port);
                break;
            case 6:
                System.out.println("End");
                break;
            case 7:
                listaDesafios(data, add, port);
                break;
            case 8:
                System.out.println("Make challenge");
                 {
                    try {
                        criaDesafio(data, add, port);
                    } catch (UserInexistenteException ex) {
                        Logger.getLogger(Atendimento.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;
            case 9:
                System.out.println("Accept challenge");
                s = PDU.byteArrayToInt(tl);
                reply = new PDU(s, (byte) 0);
                c = new Campo(0, "OK".getBytes());
                reply.addCampo(c);
                responde(reply, add, port);
                break;
            case 10:
                System.out.println("Delete challenge");
                break;
            case 11:
                System.out.println("Answer");
                break;
            case 12:
                System.out.println("Retransmit");
                retransmit(data, add, port);
                break;
            case 13:
                System.out.println("List ranking");
                break;
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

    private void processaLogin(byte[] data, InetAddress add, int port) {

        PDU pacote = new PDU(data);

        String alc = new String(pacote.getCampo(0).getValor());

        PDU resposta;
        Campo c;
        byte[] tl = {data[2], data[3]};
        int s = PDU.byteArrayToInt(tl);
        try {
            Utilizador u;
            u = this.bd.getUser(alc);
            byte[] passATestar = pacote.getCampo(1).getValor();
            byte[] pass = u.getPass();
            if (Arrays.equals(passATestar, pass)) { //Se a passe for correta
                resposta = new PDU(s, (byte) 0);
                c = new Campo(1, u.getUserName().getBytes());
                resposta.addCampo(c);
                this.bd.updateUser(u.getAlcunha(), add, port);
                responde(resposta, this.bd.getUser(alc).getIp(), this.bd.getUser(alc).getPort());
            } else { //Pacote de erro passe incorreta
                resposta = new PDU(s, (byte) 0);
                c = new Campo(255, "Password incorreta!".getBytes());
                resposta.addCampo(c);
                responde(resposta, add, port);
            }
        } catch (UserInexistenteException ex) {
            //pacote de erro            
            resposta = new PDU(s, (byte) 0);
            c = new Campo(255, "Utilizador inexistente!".getBytes());
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
            c = new Campo(255, "Utilizador existente!".getBytes());
            reply.addCampo(c);
            responde(reply, add, port);
        } else {
            Utilizador novo = new Utilizador(nome, alc, pass, add, port);
            this.bd.addUser(novo);
            reply = new PDU(s, (byte) 0);
            c = new Campo(0, "OK".getBytes());
            reply.addCampo(c);
            responde(reply, add, port);
        }

    }

    private void listaDesafios(byte[] data, InetAddress add, int port) {
        ArrayList<Desafio> desafios = bd.getDesafios();
        byte[] tl = {data[2], data[3]};
        int s = PDU.byteArrayToInt(tl);
        PDU reply;
        Campo c, da, h, f;
        int tam = desafios.size();
        int t = 0;
        for (Desafio d : desafios) {
            t++;
            reply = new PDU(s, (byte) 0);
            c = new Campo(7, d.getNome().getBytes());
            reply.addCampo(c);
            da = new Campo(4, d.getData().getBytes());
            reply.addCampo(da);
            h = new Campo(5, d.getTempo().getBytes());
            reply.addCampo(h);
            if (t < tam) {
                f = new Campo(254, "0".getBytes());
                reply.addCampo(f);
            }
            responde(reply, add, port);
        }
    }

    private void criaDesafio(byte[] data, InetAddress add, int port) throws UserInexistenteException {
        PDU pacote = new PDU(data);
        byte[] tl = {data[2], data[3]};
        int s = PDU.byteArrayToInt(tl);
        PDU reply;
        Campo c, dat;
        String nome = new String(pacote.getCampo(0).getValor());

        boolean e = bd.existeDesafio(nome);

        if (e) {
            reply = new PDU(s, (byte) 0);
            c = new Campo(255, "Desafio existente!".getBytes());
            reply.addCampo(c);
            responde(reply, add, port);
        } else {
            //LocalDateTime tempo = LocalDateTime.now().plusMinutes(5);
            LocalDateTime tempo = LocalDateTime.now().plusSeconds(1);
            int aux = tempo.getYear() % 100;
            int pri = aux / 10;
            int sec = aux % 10;
            byte[] ano = {(byte) pri, (byte) sec};
            byte[] mes = PDU.intToByteArray(tempo.getMonthValue());
            byte[] dia = PDU.intToByteArray(tempo.getDayOfMonth());
            byte[] hora = PDU.intToByteArray(tempo.getHour());
            byte[] minuto = PDU.intToByteArray(tempo.getMinute());
            byte[] segundo = PDU.intToByteArray(tempo.getSecond());
            Desafio d = new Desafio(nome, ano, dia, mes, hora, minuto, segundo);
            criaPerguntas(d);
            Utilizador u = bd.getUserByIP(add);
            d.addUser(u, tl);
            this.bd.addDesafio(d);
            reply = new PDU(s, (byte) 0);
            c = new Campo(07, d.getNome().getBytes());
            reply.addCampo(c);
            dat = new Campo(04, d.getDataByte().getBytes());
            reply.addCampo(dat);
            responde(reply, add, port);

            //System.out.println("cecec"+tempo.toString());
            Jogo j = new Jogo(tempo, d, this.bd);
            j.start();

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
        for (i = 1; i < nPackets; i++) {
            p = new byte[49152];
            for (int j = 0; j < 49152; j++) {
                p[j] = m[i * 49152 + j];
            }
            blocos.put(i, p);
        }
        p = new byte[lastPackBytes];
        for (int j = 0; j < lastPackBytes; j++) {
            p[j] = m[i * 49152 + j];
        }
        blocos.put(i, p);

        return blocos;
    }

    private void retransmit(byte[] data, InetAddress add, int port){
        try {
            PDU pacote = new PDU(data);
            byte[] tl = {data[2], data[3]};
            int s = PDU.byteArrayToInt(tl);
            PDU reply;
            Campo c;
            String nome = new String(pacote.getCampo(0).getValor());
            Desafio d = this.bd.getDesafio(nome);
            int nQ = pacote.getCampo(1).getValor()[0];
            Pergunta p = d.getPergunta(nQ);
            int tipo = pacote.getCampo(2).getValor()[0];
            String ca;
            if (tipo == 16) {
                ca = this.bd.getPathImage().concat(p.getImagem());
            } else {
                ca = this.bd.getPathMusic().concat(p.getMusica());
            }
            int bloco = PDU.byteArrayToInt(pacote.getCampo(3).getValor());
            TreeMap<Integer, byte[]> blocos = (TreeMap) this.getBlocos(ca);
            byte b[] = blocos.get(bloco);
            
            PDU music = new PDU(s, (byte) 0);
            c = new Campo(7, nome.getBytes());
            music.addCampo(c);
            c = new Campo(10, PDU.intToByteArray(nQ));
            music.addCampo(c);
            music.addCampo(new Campo(17, new byte[]{(byte) (bloco)}));
            music.addCampo(new Campo(tipo, b));
            responde(music, add, port);
        } catch (IOException ex) {
            Logger.getLogger(Atendimento.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

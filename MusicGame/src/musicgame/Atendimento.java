package musicgame;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bruno Pereira
 */
public class Atendimento extends Thread {
   
    private static final int OK                  = 0;
    private static final int FIM                 = 250;
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
    private static final int TIME                = 21;

    private final DatagramPacket receivePacket;
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
        } catch (IOException | UserInexistenteException e) {
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
    private void analisaPacote(byte[] data, InetAddress add, int port) throws IOException, UserInexistenteException {
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
                s = PDU.byteArrayToInt(tl);
                reply = new PDU(s, (byte) 0);
                c = new Campo(OK, "OK".getBytes());
                reply.addCampo(c);
                responde(reply, add, port);
                break;
            case 6:
                System.out.println("End");
                fimDesafio(data, add, port);
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
                c = new Campo(OK, PDU.intToByteArray(0));
                reply.addCampo(c);
                PDU p = new PDU(data);
                Desafio d = bd.getDesafio(new String(p.getCampo(0).getValor()));
                d.addUser(bd.getUserByIP(add), tl);
                responde(reply, add, port);
                Jogo j;
                boolean f = true;
                // for (int i = 1; i <= 10 && f; i++) {
                j = new Jogo(bd.getUserByIP(add), d.getLocalDate(), d, this.bd, 1, true);
                j.start();

                //}
                /*
                if (!f) {
                    d.removeUtilizador(bd.getUserByIP(add).getAlcunha());
                    reply = new PDU(s, (byte) 0);
                    c = new Campo(01, "Ok".getBytes());
                    reply.addCampo(c);
                    responde(reply, add, port);

                }
*/
                break;
            case 10:
                System.out.println("Delete challenge");
                break;
            case 11:
                respostas(data, add, port);

                break;
            case 12:
                System.out.println("Retransmit");
                retransmit(data, add, port);
                break;
            case 13:
                System.out.println("List ranking");
                break;
            case 14:
                p = new PDU(data);
                d = bd.getDesafio(new String(p.getCampo(0).getValor()));
                int nQuestao = p.getCampo(1).getValor()[0];
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

    @SuppressWarnings("empty-statement")
    public void fimDesafio(byte[] data, InetAddress add, int port) throws UserInexistenteException {
        PDU pacote = new PDU(data);
        byte[] tl = {data[2], data[3]};
        int s = PDU.byteArrayToInt(tl);
        PDU reply;
        Campo c, dat;

        Utilizador user = bd.getUserByIP(add);

        Desafio d = bd.getDesafio(new String(pacote.getCampo(0).getValor()));
        d.addUserEnd(user);

        while (d.getTamanhoUtilizadoresEnd() < d.getTamanhoUsers());
        int maior = 0;
        Utilizador us = new Utilizador();

        PDU resposta = new PDU(s, (byte) 0);

        for (Utilizador u : d.getUserEnd().values()) {
            if (u.getPontuacao() > maior) {
                maior = u.getPontuacao();
                us = u;
            } else if (u.getPontuacao() == maior) {
                if (u.getTempoResposta() < us.getTempoResposta()) {
                    us = u;
                    maior = u.getPontuacao();
                }
            }
        }

        us.addPontuacao(3);
       

        TreeSet<Utilizador> utili = new TreeSet<>(new CompareUsersByPoints());

        for (Utilizador u : d.getUserEnd().values()) {
            utili.add(u);
            bd.actRanking(u);
        }

        for (Utilizador u : utili) {
            c = new Campo(ALCUNHA, u.getAlcunha().getBytes());
            resposta.addCampo(c);
            c = new Campo(PONTOS, PDU.intToByteArray(u.getPontuacao()));
            resposta.addCampo(c);
        }

        responde(resposta, add, port);

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
        byte[] tl = {data[2], data[3]};
        int s = PDU.byteArrayToInt(tl);
        PDU reply;
        Campo c, da, h, f;
        int tam = desafios.size();
        int t = 0;
        for (Desafio d : desafios) {
            t++;
            reply = new PDU(s, (byte) 0);
            c = new Campo(DESAFIO, d.getNome().getBytes());
            reply.addCampo(c);
            da = new Campo(DATA, d.getDataByte().getBytes());
            System.out.println("dataaaa " + new String(da.getValor()));
            reply.addCampo(da);
            h = new Campo(HORA, d.getTempo().getBytes());
            reply.addCampo(h);
            if (t < tam) {
                f = new Campo(CONTINUA, "0".getBytes());
                reply.addCampo(f);
            }
            responde(reply, add, port);
        }
    }

    private void criaDesafio(byte[] data, InetAddress add, int port) throws UserInexistenteException, SocketException {
        PDU pacote = new PDU(data);
        byte[] tl = {data[2], data[3]};
        int s = PDU.byteArrayToInt(tl);
        PDU reply;
        Campo c, dat;
        String nome = new String(pacote.getCampo(0).getValor());

        boolean e = bd.existeDesafio(nome);

        System.out.println("cria desafio " + e);

        if (e) {
            reply = new PDU(s, (byte) 0);
            c = new Campo(ERRO, "Desafio existente!".getBytes());
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
            c = new Campo(DESAFIO, d.getNome().getBytes());
            reply.addCampo(c);
            dat = new Campo(DATA, d.getDataByte().getBytes());
            reply.addCampo(dat);
            responde(reply, add, port);
            boolean f = true;
            //System.out.println("cecec"+tempo.toString());
            Jogo j;
            //for (int i = 1; i <= 10 && f; i++) {
            j = new Jogo(u, tempo, d, this.bd, 1, true);
            j.start();
                //f = respostas();

            // }
        }
    }

    private void respostas(byte[] data, InetAddress add, int port) throws SocketException, UserInexistenteException {
        PDU pacote = new PDU(data);
        byte[] tl = {data[2], data[3]};
        int s = PDU.byteArrayToInt(tl);
        PDU reply;
        Campo c, dat;

        int id = pacote.getCampo(0).getId();
        System.out.println("ID = " + id);

        int escolha = PDU.byteArrayToInt(pacote.getCampo(0).getValor());
        String nomeDesafio = new String(pacote.getCampo(1).getValor());
        int nQuestao = PDU.byteArrayToInt(pacote.getCampo(2).getValor());
        int tempoResposta = PDU.byteArrayToInt(pacote.getCampo(3).getValor());
        int pontuacao, certa;
        Utilizador user = bd.getUserByIP(add);

        Desafio d = bd.getDesafio(nomeDesafio);

        int respostaCerta = d.getPergunta(nQuestao - 1).getRespostaCerta();
        if (respostaCerta == escolha) {
            user.addTempoResposta(tempoResposta);
            user.addPontuacao(2);
            pontuacao = 2;
            certa = 1;

        } else {
            user.subPontuacao(1);
            pontuacao = -1;
            certa = 0;
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
            PDU reply;
            Campo c;
            String nome = new String(pacote.getCampo(0).getValor()); //nome
            Desafio d = this.bd.getDesafio(nome);
            int nQ = pacote.getCampo(1).getValor()[0];               //n questao
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
            System.out.println("b.size  " + b.length);
            System.out.println("numeroooo bloocooooo " + bloco);

            PDU music = new PDU(s, (byte) 0);
            c = new Campo(DESAFIO, nome.getBytes());
            System.out.println("nome do desafio " + nome);
            music.addCampo(c);                                      //nome
            c = new Campo(NQUESTAO, PDU.intToByteArray(nQ));
            System.out.println("numero da questao " + nQ);
            music.addCampo(c);                                      //nQuestao
            c = new Campo(BLOCO, PDU.intToByteArray(bloco));
            music.addCampo(c);                                          //nBloco
            System.out.println("blocooo " + bloco);
            c = new Campo(tipo, b);
            music.addCampo(c);                                      //bloco
            responde(music, add, port);
        } catch (IOException ex) {
            Logger.getLogger(Atendimento.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

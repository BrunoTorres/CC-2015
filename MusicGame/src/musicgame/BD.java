/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicgame;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 *
 * @author patricia
 */
class BD implements Serializable {

    private HashMap<String, Utilizador> users;
    private HashMap<String, Desafio> desafios;
    private ArrayList<Pergunta> perguntas;
    private String pastaMusica;
    private String pastaImagem;

    public BD(String pMusica, String pImagem) {
        this.pastaImagem = pImagem;
        this.perguntas = new ArrayList<>();
        this.pastaMusica = pMusica;
        this.users = new HashMap<>();
        this.desafios = new HashMap<>();
    }

    public void addUser(Utilizador u) {
        users.put(u.getAlcunha(), u);
    }

    public void addDesafio(Desafio d) {
        desafios.put(d.getNome(), d);
    }

    public void addPergunta(Pergunta p) {
        perguntas.add(p);
    }

    public void removeUser(String alcunha) {
        users.remove(alcunha);
    }

    public void removeDesafio(String nd) {
        desafios.remove(nd);
    }

    public void removePergunta(Pergunta p) {
        perguntas.remove(p);
    }

    public boolean existeUser(String alc) {
        return users.containsKey(alc);
    }

    public void carregaPerguntas(String path) throws IOException {
        File f = new File(path);
        //Pattern init = Pattern.compile("\\w+=\\w+");
        //Pattern rest = Pattern.compile("\\w+=\\w+");

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String l = br.readLine();
            String music = null;
            String image = null;
            String quest = null;
            Pergunta p;
            while (l != null) {
                /*
                 if (l.matches("music_DIR=\\w+")) {
                 music = l.split("=")[1];
                 this.pastaMusica=(music);
                 }
                 if (l.matches("images_DIR=\\w+")) {
                 image = l.split("=")[1];
                 this.pastaImagem=image;
                 }

                 if (l.matches("questions_#=\\w+")) {
                 quest = l.split("=")[1];
                 }
                 */
                p = new Pergunta();
                splitLinha(l, p, ";");
                this.addPergunta(p);

                l = br.readLine();

            }
          

        }
    }

    private String trimAspas(String s) {
        return s.substring(1, s.length() - 1);
    }

    private void splitLinha(String l, Pergunta p, String delim) {
        String[] s = l.split(delim);
        p.setMusica(s[0]);
        p.setImagem(s[1]);

        p.setPergunta(trimAspas(s[2]));
        p.setRespostas(trimAspas(s[3]));
        p.setRespostas(trimAspas(s[4]));
        p.setRespostas(trimAspas(s[5]));

        p.setRespostaCerta(Integer.valueOf(s[s.length - 1]));

    }

    public Utilizador getUser(String alc) throws UserInexistenteException {
        if (existeUser(alc)) {
            return users.get(alc);
        } else {
            throw new UserInexistenteException("Utilizador inexistente!");
        }
    }

    public Utilizador getUserByIP(InetAddress ip) throws UserInexistenteException {
        boolean enc = false;
        Utilizador us = new Utilizador();
        for (Utilizador u : users.values()) {
            if (u.getIp().equals(ip)) {
                us = u;
                enc = true;
            }
        }
        if (!enc) {
            throw new UserInexistenteException("Utilizador inexistente!");
        } else {
            return us;
        }
    }

    void updateUser(String alcunha, InetAddress add, int port) {
        this.users.get(alcunha).setIp(add);
        this.users.get(alcunha).setPort(port);
    }

    ArrayList<Desafio> getDesafios() {
        ArrayList<Desafio> des = new ArrayList<>();
        for (Desafio d : desafios.values()) {
            des.add(d);
        }
        return des;
    }

    public boolean existeDesafio(String nome) {
        return desafios.containsKey(nome);
    }

    public Pergunta getPergunta() {
        Random ran = new Random();

        int index = ran.nextInt(this.perguntas.size());
        return this.perguntas.get(index);
    }

    public String getPathImage() {
        return this.pastaImagem;
    }
    
    public String getPathMusic() {
        return this.pastaMusica;
    }

}

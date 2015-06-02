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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 */
class BD implements Serializable {
    private int port;
    private HashMap<String, Utilizador> users; /// syc
    private HashMap<String, Integer> rankingLocal;     /// syc
    private HashMap<String, Integer> rankingGlobal;     /// syc
    private HashMap<String, Desafio> desafiosLocais; // syc
    private HashMap<String, LocalDateTime> desafiosGlobais; // syc
    
    private HashMap<String, HashMap<InetAddress,Integer>> listaDesafiosServidores;
    
    private ArrayList<Pergunta> perguntas;
    private HashMap<InetAddress, Integer> servidores;
    private String pastaMusica;
    private String pastaImagem;
    private Lock l = new ReentrantLock();

    public BD(String pMusica, String pImagem) {
        this.pastaImagem = pImagem;
        this.perguntas = new ArrayList<>();
        this.pastaMusica = pMusica;
        this.users = new HashMap<>();
        this.desafiosLocais = new HashMap<>();
        this.rankingLocal = new HashMap<>();
        this.servidores = new HashMap<>();
        this.desafiosGlobais = new HashMap<>();
        this.rankingGlobal = new HashMap<>();
        this.listaDesafiosServidores= new HashMap<String, HashMap<InetAddress,Integer>>();
        
    }
    
    public Map<String, LocalDateTime> getDesafiosGlobais(){
        LocalDateTime agora = LocalDateTime.now();
        HashMap<String, LocalDateTime> aux= new HashMap<>();
        for(String s : this.desafiosGlobais.keySet()){
            if(this.desafiosGlobais.get(s).isAfter(agora))
                aux.put(s,this.desafiosGlobais.get(s));
        }
        return aux;
    }
    
    public Map<String, Integer> getRankingGlobal(){
        return this.rankingGlobal;
    }

    public int getRanking(String nome) {
        return this.rankingLocal.get(nome);
    }

    public Map<String, Integer> getRankingLocal() {
        return this.rankingLocal;

    }

    public void actRanking(Utilizador u) {
        l.lock();
        try {
            if (this.rankingLocal.containsKey(u.getAlcunha())) {
                this.rankingLocal.put(u.getAlcunha(), this.rankingLocal.get(u.getAlcunha()) + u.getPontuacao());
            } else {
                this.rankingLocal.put(u.getAlcunha(), u.getPontuacao());
            }
            System.out.println("PONTUACAO FOI ATUALIZADA: " + this.rankingLocal.get(u.getAlcunha()));
        } finally {
            l.unlock();
        }

    }

    public void addUser(Utilizador u) {
        l.lock();
        try {
            users.put(u.getAlcunha(), u);
            rankingLocal.put(u.getAlcunha(), 0);
        } finally {
            l.unlock();
        }
    }

    public int addPontuacao(String alcunha, int p) {
        l.lock();
        int r;
        try {
            r = p + rankingLocal.get(alcunha);
            rankingLocal.put(alcunha, r);
        } finally {
            l.unlock();
        }
        return r;
    }
    
    
    public Map<String,LocalDateTime> getDesafiosLocais(){
        HashMap<String, LocalDateTime> aux = new HashMap<>();
        for(String s: this.desafiosLocais.keySet()){
            Desafio d=this.desafiosLocais.get(s);
            if(!d.getStatus())
                aux.put(s,d.getLocalDate());  
        }
        return aux;
    }
    
    
    public synchronized void addDesafio(Desafio d) {
        desafiosLocais.put(d.getNome(), d);
    }

    public synchronized void addPergunta(Pergunta p) {
        perguntas.add(p);
    }

    public synchronized void removeUser(String alcunha) {
        users.remove(alcunha);
    }

    public synchronized void removeDesafio(String nd) {
        desafiosLocais.remove(nd);
    }

    public synchronized void removePergunta(Pergunta p) {
        perguntas.remove(p);
    }

    public boolean existeUser(String alc) {
        return users.containsKey(alc);
    }

    public synchronized void carregaPerguntas(String path) throws IOException {
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
    public void setPorta(int porta){
        this.port=porta;
    }
    public int getPorta(){
        return this.port;
    }

    public synchronized void updateUser(String alcunha, InetAddress add, int port) {
        this.users.get(alcunha).setIp(add);
        this.users.get(alcunha).setPort(port);
    }

    public ArrayList<Desafio> getDesafios() {
        ArrayList<Desafio> des = new ArrayList<>();
        for (Desafio d : desafiosLocais.values()) {
            des.add(d);
        }
        return des;
    }

    public Desafio getDesafio(String nome) {
        return this.desafiosLocais.get(nome);
    }

    public boolean existeDesafio(String nome) {
        return desafiosLocais.containsKey(nome);
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
    
    public void registaServidor(InetAddress ip, int porta){
        this.servidores.put(ip, porta);
    }

    public Map<InetAddress, Integer> getServidores() {
        return this.servidores;
    }

    public synchronized void registaServidores(HashMap<InetAddress, Integer> svs) {
        for(InetAddress i : svs.keySet()){
            this.servidores.put(i, svs.get(i));
        }
    }

    public synchronized void  addDesafiosGlobais(HashMap<String, LocalDateTime> des) {
        
    }

    public synchronized void addRankingGlobal(HashMap<String, Integer> rank) {
        for(String s: rank.keySet()){
            if(this.rankingGlobal.containsKey(s)){
                int r=this.rankingGlobal.get(s)+rank.get(s);
                this.rankingGlobal.put(s,r);
            }
            else
                this.rankingGlobal.put(s,rank.get(s));
        }
    }

    public synchronized void addDesafiosGlobais(HashMap<String, LocalDateTime> des, InetAddress byName, int porta) {
        HashMap<InetAddress,Integer>aux= new HashMap<>();
        aux.put(byName, porta);
        for(String s: des.keySet()){
            this.listaDesafiosServidores.put(s,aux);
            this.desafiosGlobais.put(s, des.get(s));
        }
        
    }

    public synchronized HashMap<InetAddress, Integer> getDesafioByIp(String desafio) {
        return this.listaDesafiosServidores.get(desafio);
    }

    public synchronized void  addDesafioGlobal(String desafio, LocalDateTime data) {
        this.desafiosGlobais.put(desafio, data);
    }
}

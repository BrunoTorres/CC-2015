/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicgame;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author patricia
 */
class Desafio implements Serializable {
    private String nome;
    private ArrayList<Pergunta> questoes ;
    private String pastaMusica;
    private String pastaImagens;

    public Desafio(String nome, String pastaMusica, String pastaImagens) {
        this.nome = nome;
        this.questoes = new ArrayList<>();
        this.pastaMusica = pastaMusica;
        this.pastaImagens = pastaImagens;
    }
    
    public void addPergunta(Pergunta p){
        this.questoes.add(p);
    }
    
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public ArrayList<Pergunta> getQuestoes() {
        return questoes;
    }

    public void setQuestoes(ArrayList<Pergunta> questoes) {
        this.questoes = questoes;
    }

    public String getPastaMusica() {
        return pastaMusica;
    }

    public void setPastaMusica(String pastaMusica) {
        this.pastaMusica = pastaMusica;
    }

    public String getPastaImagens() {
        return pastaImagens;
    }

    public void setPastaImagens(String pastaImagens) {
        this.pastaImagens = pastaImagens;
    }
    
    
    
}

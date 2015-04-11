/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicgame;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author patricia
 */
class Pergunta implements Serializable {
    private String musica;
    private String imagem;
    private String pergunta;
    private ArrayList<String> respostas;
    private int respostaCerta;

    public Pergunta(String musica, String imagem, String pergunta, ArrayList<String> respostas, int respostaCerta) {
        this.musica = musica;
        this.imagem = imagem;
        this.pergunta = pergunta;
        this.respostas = respostas;
        this.respostaCerta = respostaCerta;
    }

    public void setMusica(String musica) {
        this.musica = musica;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public void setPergunta(String pergunta) {
        this.pergunta = pergunta;
    }

    public void setRespostas(ArrayList<String> respostas) {
        this.respostas = respostas;
    }

    public void setRespostaCerta(int respostaCerta) {
        this.respostaCerta = respostaCerta;
    }

    public String getMusica() {
        return musica;
    }

    public String getImagem() {
        return imagem;
    }

    public String getPergunta() {
        return pergunta;
    }

    public ArrayList<String> getRespostas() {
        return respostas;
    }

    public int getRespostaCerta() {
        return respostaCerta;
    }

    @Override
    public String toString() {
        return "Pergunta{" + "musica=" + musica + ", imagem=" + imagem + ", pergunta=" + pergunta + ", respostas=" + respostas + ", respostaCerta=" + respostaCerta + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.musica);
        hash = 37 * hash + Objects.hashCode(this.imagem);
        hash = 37 * hash + Objects.hashCode(this.pergunta);
        hash = 37 * hash + Objects.hashCode(this.respostas);
        hash = 37 * hash + this.respostaCerta;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Pergunta other = (Pergunta) obj;
        if (!Objects.equals(this.musica, other.musica)) {
            return false;
        }
        if (!Objects.equals(this.imagem, other.imagem)) {
            return false;
        }
        if (!Objects.equals(this.pergunta, other.pergunta)) {
            return false;
        }
        if (!Objects.equals(this.respostas, other.respostas)) {
            return false;
        }
        if (this.respostaCerta != other.respostaCerta) {
            return false;
        }
        return true;
    }
    
    
    
}

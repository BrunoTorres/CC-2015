/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicgame;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author patricia
 */
public class Desafio implements Serializable {

    private String nome;
    private ArrayList<Pergunta> questoes;
    private HashMap<String, Utilizador> users;
    private HashMap<String, byte[]> labels;
    private HashMap<String, Utilizador> usersEnd;
    private boolean status; // false->não foi feito true->já ocorreu
    private boolean haveWinner;

    private byte[] ano;
    private byte dia;
    private byte mes;
    private byte hora;
    private byte minuto;
    private byte segundo;

    private SimpleStringProperty dataString;
    private SimpleStringProperty horaString;

    public Desafio(String nome, byte[] ano, byte mes, byte dia, byte hora, byte minuto, byte segundo) {
        this.nome = nome;
        this.ano = ano;
        this.mes = mes;
        this.dia = dia;
        this.hora = hora;
        this.minuto = minuto;
        this.segundo = segundo;
        this.users = new HashMap<>();
        this.labels = new HashMap<>();
        this.questoes = new ArrayList<>();
        this.usersEnd = new HashMap<>();
        this.status = false;
        this.haveWinner = false;

        this.dataString = new SimpleStringProperty();
        this.horaString = new SimpleStringProperty();

        //this.dataString.setValue(this.getLocalDate().toLocalDate().toString());
        //this.horaString.setValue(this.getLocalDate().toLocalTime().toString());
    }

    public Desafio() {
    }

    public void remUtilizadoresEnd(String alcunha) {
        this.usersEnd.remove(alcunha);
    }

    public void remUtilizadores(String alcunha) {
        this.users.remove(alcunha);
    }

    public int getTamanhoUtilizadoresEnd() {
        return this.usersEnd.size();
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
    
    public boolean getHaveWinner() {
        return this.haveWinner;
    }

    public void setHaveWinner(boolean status) {
        this.haveWinner = status;
    }

    public int getTamanhoUsers() {
        return this.users.size();
    }

    public Map<String, Utilizador> getUserEnd() {
        return this.usersEnd;
    }

    public StringProperty getDataProperty() {
        return this.dataString;
    }

    public void setDataProperty() {
        this.dataString.setValue(this.getLocalDate().toLocalDate().toString());
    }

    public StringProperty getHoraProperty() {
        return this.horaString;
    }

    public void setHoraProperty() {
        this.horaString.setValue(this.getLocalDate().toLocalTime().toString());
    }

    public void addUserEnd(Utilizador u) {
        this.usersEnd.put(u.getAlcunha(), u);
    }

    public final LocalDateTime getLocalDate() {
        int anos;
        anos = new BigInteger(this.ano).intValue();

        LocalDateTime da = LocalDateTime.of(anos, this.mes, this.dia, this.hora, this.minuto, this.segundo);
        return da;

    }

    public void removeUtilizador(String alc) {
        this.users.remove(alc);
    }

    public void addUser(Utilizador u, byte[] label) {
        users.put(u.getAlcunha(), u);
        labels.put(u.getAlcunha(), label);
    }

    public boolean addPergunta(Pergunta p) {
        boolean flag = true;
        for (Pergunta pp : this.questoes) {
            if (p.getPergunta().equals(pp.getPergunta())) {
                flag = false;
                break;
            }
        }
        if (flag) {
            this.questoes.add(p);
        }
        return flag;
    }

    public HashMap<String, Utilizador> getUsers() {
        return users;
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

    public byte[] getAno() {
        return ano;
    }

    public void setAno(byte[] ano) {
        this.ano = ano;
    }

    public byte getDia() {
        return dia;
    }

    public void setDia(byte dia) {
        this.dia = dia;
    }

    public byte getMes() {
        return mes;
    }

    public void setMes(byte mes) {
        this.mes = mes;
    }

    public byte getHora() {
        return hora;
    }

    public void setHora(byte hora) {
        this.hora = hora;
    }

    public byte getMinuto() {
        return minuto;
    }

    public void setMinuto(byte minuto) {
        this.minuto = minuto;
    }

    public byte getSegundo() {
        return segundo;
    }

    public void setSegundo(byte segundo) {
        this.segundo = segundo;
    }

    public byte[] getData() {
        return new byte[]{this.ano[0], this.ano[1], this.ano[2], this.mes, this.dia};
    }

    public byte[] getTempo() {
        return new byte[]{this.hora, this.minuto, this.segundo};
    }

    public String getStringDataFromByte() {
        int horaAux, minAux, segAux;
        StringBuilder sb = new StringBuilder();
        sb.append(new BigInteger(this.ano).intValue());
        sb.append(this.mes);
        sb.append(this.dia);
        System.out.println(sb.toString());
        return sb.toString();
    }

    public String getStringHoraFromByte() {

        StringBuilder sb = new StringBuilder();
        sb.append(this.hora);
        sb.append(this.minuto);
        sb.append(this.segundo);
        System.out.println(sb.toString());
        return sb.toString();
    }

    /*
    
     byte[] resdata = new byte[6];
     System.arraycopy(ano, 0, resdata, 0, 2);
     System.arraycopy(mes, 0, resdata, 2, 2);
     System.arraycopy(dia, 0, resdata, 4, 2);
    
     */
    public byte[] getLabelByUser(Utilizador u) {
        return this.labels.get(u.getAlcunha());
    }

    public Pergunta getPergunta(int id) {
        return this.questoes.get(id);
    }

    public int getNumeroRespostas(int id) {
        return this.questoes.get(id).getRespostas().size();
    }

    public String getResposta(int numQ, int i) {
        return this.questoes.get(numQ).getRespostaIndice(i);
    }

    public byte[] getImagemQuestao(String pImage, int i) throws IOException {

        //System.out.println("nome imagem = " + this.questoes.get(i).getImagem());
        String img = pImage.concat(this.questoes.get(i).getImagem());
        File f = new File(img);
        byte[] r = Files.readAllBytes(f.toPath());
        //r = Files.readAllBytes(f.toPath());

        return r;
    }

    public byte[] getMusicaQuestao(String pMusica, int i) throws IOException { // retorna array com TODOS os bytes de um ficheiro de som de uma questão
        String m = pMusica.concat(this.questoes.get(i).getMusica());

        File f = new File(m);
        byte[] r = Files.readAllBytes(f.toPath());
        //r = Files.readAllBytes(f.toPath());

        return r;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("nome====== ").append(this.nome);
        for (Pergunta p : this.questoes) {
            p.toString();
        }
        return sb.toString();
    }

}

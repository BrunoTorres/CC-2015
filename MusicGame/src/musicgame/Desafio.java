/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicgame;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.util.converter.LocalDateTimeStringConverter;

/**
 *
 * @author patricia
 */
class Desafio implements Serializable {

    private String nome;
    private ArrayList<Pergunta> questoes;
    private HashMap<String, Utilizador> users;
    private HashMap<String, byte[]> labels;
    private byte[] ano;
    private byte[] dia;
    private byte[] mes;
    private byte[] hora;
    private byte[] minuto;
    private byte[] segundo;

    public Desafio(String nome, byte[] ano, byte[] dia, byte[] mes, byte[] hora, byte[] minuto, byte[] segundo) {
        this.nome = nome;
        this.ano = ano;
        this.dia = dia;
        this.mes = mes;
        this.hora = hora;
        this.minuto = minuto;
        this.segundo = segundo;
        this.users = new HashMap<>();
        this.labels = new HashMap<>();
        this.questoes = new ArrayList<>();
    }
    
    public LocalDateTime getLocalDate(){
        int anos,ano2,mes,dia,hora,min,seg;
        System.out.println("locall");
        anos=this.ano[1];
        System.out.println("anoo "+ anos);
        ano2=this.ano[0];
        ano2*=10;
        anos+=ano2+2000;
        //System.out.println("anooooo "+  PDU.byteArrayToInt(this.ano));
        mes=PDU.byteArrayToInt(this.mes);
        dia=PDU.byteArrayToInt(this.dia);
        hora=PDU.byteArrayToInt(this.hora);
        min=PDU.byteArrayToInt(this.minuto);
        seg=PDU.byteArrayToInt(this.segundo);
        
        LocalDateTime d = LocalDateTime.of(anos, mes, dia, hora, min, seg);
        System.out.println("************************************* * "  +d.toString());
        return d;
        
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

    public byte[] getDia() {
        return dia;
    }

    public void setDia(byte[] dia) {
        this.dia = dia;
    }

    public byte[] getMes() {
        return mes;
    }

    public void setMes(byte[] mes) {
        this.mes = mes;
    }

    public byte[] getHora() {
        return hora;
    }

    public void setHora(byte[] hora) {
        this.hora = hora;
    }

    public byte[] getMinuto() {
        return minuto;
    }

    public void setMinuto(byte[] minuto) {
        this.minuto = minuto;
    }

    public byte[] getSegundo() {
        return segundo;
    }

    public void setSegundo(byte[] segundo) {
        this.segundo = segundo;
    }

    
    public String getData() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.ano).append(this.mes).append(this.dia);
        return sb.toString();
    }

    public String getTempo() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.hora).append(this.minuto).append(this.segundo);
        return sb.toString();
    }

    public String getDataByte() {

        StringBuilder sb = new StringBuilder();
        sb.append(this.ano[0]).append(this.ano[1]);
        sb.append(this.mes[0]).append(this.mes[1]);
        sb.append(this.dia[0]).append(this.dia[1]);
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
    public String toString(){
        StringBuilder sb= new StringBuilder();
        sb.append("nome====== ").append(this.nome);
        for(Pergunta p :this.questoes)
            p.toString();
        return sb.toString();
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicgame;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author patricia
 */
class BD implements Serializable{
    
    private HashMap<String,Utilizador> users;
    private HashMap<String,Desafio> desafios;
    private ArrayList<Pergunta> perguntas;

    public BD() {
        this.users =  new HashMap<>();
        this.desafios = new HashMap<>();
    }
    
    public void addUser(Utilizador u){
        users.put(u.getAlcunha(), u);
    }
    
    public void addDesafio(Desafio d){
        desafios.put(d.getNome(), d);
    }
    
    public void addPergunta(Pergunta p){
        perguntas.add(p);
    }
    
    public void removeUser(String alcunha){
        users.remove(alcunha);
    }
    
    public void removeDesafio(String nd){
        desafios.remove(nd);
    }
    
    public void removePergunta(Pergunta p){
        perguntas.remove(p);
    }

    public boolean existeUser(String alc) {
        return users.containsKey(alc);
    }

    public Utilizador getUser(String alc) throws UserInexistenteException {
        if (existeUser(alc)){
            return users.get(alc);
        }
        else throw new UserInexistenteException("Utilizador inexistente!");
    }
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import musicgame.Desafio;
import musicgame.MusicClient;
import musicgame.Utilizador;

public class ListarDesafiosController implements Initializable {

    @FXML
    private TableColumn<Desafio, String> tcHora;

    @FXML
    private TableColumn<Desafio, String> tcDesafio;
    
    @FXML
    private TableColumn<Desafio, String> tcData;

    @FXML
    private Button buttonEntrar;
    
    private Utilizador user;
    private Stage atual;
    private Stage anterior;

    public ListarDesafiosController() {
    }

    public void setAnterior(Stage ant) {
        this.anterior = ant;
    }

    public void setAtual(Stage at) {
        this.atual = at;
    }

    public void setUser(Utilizador u) {
        this.user = u;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            ArrayList<Desafio> desafios = MusicClient.menuListChallenge();
            tcDesafio.setCellValueFactory(new PropertyValueFactory<>("nome"));
            //tcData.setCellValueFactory(new PropertyValueFactory<>("getData"));
            //tcHora.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLocalDate().toLocalTime().toString()));
        } catch (IOException ex) {
            Alert al = new Alert(AlertType.ERROR);
            al.setTitle("ERRO");
            al.setContentText("ERRO IO");
            al.showAndWait();
        }
    }
    
    @FXML
    public void buttonEntrarAction(){
        
    }

}

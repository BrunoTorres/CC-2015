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
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import musicgame.Desafio;
import musicgame.MusicClient;
import musicgame.Utilizador;

public class ListarDesafiosController implements Initializable {

    @FXML
    private TableView<Desafio> tableDesafios;

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
            tableDesafios.setItems(FXCollections.observableArrayList(desafios));
            System.err.println("SIZE DES: " + desafios.size());
            //tcDesafio.setText("COISO");
            //if (desafios.size() > 0) {
            //  System.out.println("Data:" + desafios.get(0).getDataProperty().get());
            tcDesafio.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNome()));
            tcData.setCellValueFactory(cellData -> cellData.getValue().getDataProperty());
            tcHora.setCellValueFactory(cellData -> cellData.getValue().getHoraProperty());
            //}
        } catch (IOException ex) {
            Alert al = new Alert(AlertType.ERROR);
            al.setTitle("ERRO");
            al.setContentText("ERRO IO");
            al.showAndWait();
        }
    }

    @FXML
    public void buttonEntrarAction() throws IOException {
        if (tableDesafios.getSelectionModel().getSelectedItems().size() == 0) {
            Desafio d = tableDesafios.getSelectionModel().getSelectedItem();
            MusicClient.acceptChallenge(d.getNome());
        }
        else{
            Alert al = new Alert(AlertType.INFORMATION);
            al.setTitle("Nenhum desafio selecionado");
            al.setContentText("Selecione um desafio");
            al.showAndWait();
        }
    }

}

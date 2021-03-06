/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import musicgame.Desafio;
import musicgame.MusicClient;
import musicgame.ServerUnreachableException;
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

    private ArrayList<Desafio> desafios;

    public ListarDesafiosController() {
    }

    public void setAnterior(Stage ant) {
        this.anterior = ant;
        this.atual.setOnCloseRequest((WindowEvent event) -> {
            anterior.show();
        });
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
            desafios = MusicClient.menuListChallenge();
            tableDesafios.setItems(FXCollections.observableArrayList(desafios));
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
        } catch (ServerUnreachableException ex) {
            Logger.getLogger(ListarDesafiosController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    public void buttonEntrarAction() throws IOException {
        if (tableDesafios.getSelectionModel().getSelectedItems().size() != 0) {
            try {
                Desafio d = tableDesafios.getSelectionModel().getSelectedItem();
                if (d.getLocalDate().isAfter(LocalDateTime.now())) {
                    MusicClient.acceptChallenge(d.getNome());
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/Jogar.fxml"));
                    Parent root = loader.load();
                    JogarController jogarC = loader.getController();
                    Scene scene = new Scene(root);
                    Stage stage = new Stage();

                    stage.setScene(scene);
                    stage.show();
                    this.atual.hide();
                    stage.setTitle("MusicGame");
                    jogarC.setUser(this.user);
                    jogarC.setAtual(stage);
                    jogarC.setAnterior(this.anterior);
                    jogarC.setDesafio(d);
                } else {
                    Alert a = new Alert(AlertType.INFORMATION);
                    a.setTitle("Erro");
                    a.setHeaderText("Impossível juntar-se a este desafio");
                    a.setContentText("Data expirada");
                    a.showAndWait();
                    desafios = MusicClient.menuListChallenge();
                    tableDesafios.setItems(FXCollections.observableArrayList(desafios));
                }
            } catch (SocketTimeoutException ex) {
                Logger.getLogger(ListarDesafiosController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ServerUnreachableException ex) {
                Logger.getLogger(ListarDesafiosController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            Alert al = new Alert(AlertType.INFORMATION);
            al.setTitle("Nenhum desafio selecionado");
            al.setContentText("Selecione um desafio");
            al.showAndWait();
        }
    }

}

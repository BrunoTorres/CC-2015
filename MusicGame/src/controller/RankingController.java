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
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import musicgame.MusicClient;
import musicgame.Utilizador;

public class RankingController implements Initializable {

    @FXML
    private TableView<Utilizador> tableRanking;

    @FXML
    private TableColumn<Utilizador, String> tcPos;

    @FXML
    private TableColumn<Utilizador, String> tcNick;

    @FXML
    private TableColumn<Utilizador, String> tcScore;

    private Utilizador user;
    private Stage atual;
    private Stage anterior;

    public RankingController() {
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            ArrayList<Utilizador> ranking = (ArrayList) MusicClient.menuListRankings();
            tableRanking.setItems(FXCollections.observableArrayList(ranking));
            
            tcPos.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(String.valueOf(tableRanking.getItems().indexOf(cellData.getValue()) + 1)));
            tcNick.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAlcunha()));
            tcScore.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getPontuacao())));
            //}
        } catch (IOException ex) {
            Alert al = new Alert(Alert.AlertType.ERROR);
            al.setTitle("ERRO");
            al.setContentText("ERRO IO");
            al.showAndWait();
        }
    }

}

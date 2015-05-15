/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import musicgame.Desafio;
import musicgame.MusicClient;
import musicgame.ServerUnreachableException;
import musicgame.Utilizador;



public class Menu_Controller implements Initializable {

    @FXML
    private Button buttonRanking;

    @FXML
    private Button buttonSair;

    @FXML
    private Button buttonCriar;

    @FXML
    private Button buttonListar;

    private Utilizador user;
    private Stage atual;
    private Stage anterior;

    public Menu_Controller() {
    }

    public void setAnterior(Stage ant) {
        this.anterior = ant;
        /*this.atual.setOnCloseRequest((WindowEvent event) -> {
         anterior.show();
         });*/
    }

    public void setAtual(Stage at) {
        this.atual = at;
    }

    public void setUser(Utilizador u) {
        this.user = u;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    private void criarDesafioAction(ActionEvent event) throws IOException {
        TextInputDialog desafio = new TextInputDialog();
        desafio.setTitle("Criar novo desafio");
        desafio.setHeaderText(null);
        desafio.setContentText("Nome do desafio:");
        String name;

        Optional<String> res = desafio.showAndWait();
        if (res.isPresent()) {
            try {
                name = res.get();

                Desafio d = MusicClient.menuMakeChallenge(name);
                if (d != null) {
                    Alert al = new Alert(AlertType.INFORMATION);
                    al.setTitle("Criar desafio");
                    al.setContentText("Nome: " + d.getNome() + "\nData: " + d.getLocalDate().toLocalDate().toString() + "\nHora: " + d.getLocalDate().toLocalTime().toString());
                    al.setHeaderText("Desafio criado");
                    al.showAndWait();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/Jogar.fxml"));
                    Parent root = loader.load();
                    JogarController jogarC = loader.getController();
                    Scene scene = new Scene(root);
                    Stage stage = new Stage();

                    stage.setScene(scene);
                    stage.show();
                    this.atual.hide();
                    stage.setTitle("MusicGame");
                    jogarC.setAtual(stage);
                    jogarC.setAnterior(this.atual);
                    jogarC.setDesafio(d);
                } else {
                    Alert al = new Alert(AlertType.ERROR);
                    al.setTitle("ERRO");
                    al.setHeaderText("Desafio NÃO criado");
                    al.setContentText("Desafio já existente");
                    al.showAndWait();
                }
            } catch (SocketTimeoutException ex) {
                Logger.getLogger(Menu_Controller.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ServerUnreachableException ex) {
                Logger.getLogger(Menu_Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void listarDesafiosAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/ListarDesafios.fxml"));
        Parent root = loader.load();
        ListarDesafiosController listarC = loader.getController();
        Scene scene = new Scene(root);
        Stage stage = new Stage();

        stage.setScene(scene);
        stage.show();
        this.atual.hide();
        // stage.setResizable(false);
        stage.setTitle("Lista de desafios disponíveis");
        listarC.setAtual(stage);
        listarC.setAnterior(this.atual);
    }

    @FXML
    private void listarRankingAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/Ranking.fxml"));
        Parent root = loader.load();
        RankingController listarR = loader.getController();
        Scene scene = new Scene(root);
        Stage stage = new Stage();

        stage.setScene(scene);
        stage.show();
        this.atual.hide();
        // stage.setResizable(false);
        stage.setTitle("Ranking de jogadores");
        listarR.setAtual(stage);
        listarR.setAnterior(this.atual);

    }

    @FXML
    private void logoutAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/Login.fxml"));
        Parent root = loader.load();
        Login_Controller log_c = loader.getController();
        Scene scene = new Scene(root);
        Stage stage = new Stage();

        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);
        stage.setTitle("Login");

        log_c.setAtual(stage);
        log_c.setAnterior(this.atual);
        this.atual.close();
    }
}

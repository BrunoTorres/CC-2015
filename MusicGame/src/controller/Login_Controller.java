/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import musicgame.Campo;
import musicgame.MusicClient;
import static musicgame.MusicClient.sendPDU;
import musicgame.PDU;
import musicgame.UserInexistenteException;
import musicgame.Utilizador;

/**
 *
 * @author JoaoMano
 */
public class Login_Controller extends Application implements Initializable {

    @FXML
    private AnchorPane formPanel;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ImageView imageLogin;

    @FXML
    private TextField tf_login;

    @FXML
    private PasswordField pf_pass;

    @FXML
    private Button button_auth;

    @FXML
    private Button button_limpar;

    @FXML
    private Hyperlink labelRegistar;

    @FXML
    private AnchorPane nickPanel;

    @FXML
    private Button buttonRegistar;

    @FXML
    private TextField tfNickname;

    @FXML
    private Button buttonCancel;

    private String cenas;

    //private FuncionarioDAO funcionarioDAO;
    //private SGH habitat;
    private Stage atual;
    private Stage anterior;

    public Login_Controller() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //habitat = new SGH();

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/Login.fxml"));

        Parent root = loader.load();

        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(false);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @FXML
    void registarAction(ActionEvent event) {
        formPanel.setVisible(false);
        nickPanel.setVisible(true);
    }

    @FXML
    void cancelarButtonAction(ActionEvent event) {
        nickPanel.setVisible(false);
        formPanel.setVisible(true);

        limparButtonAction();
    }

    @FXML
    void registarButtonAction(ActionEvent event) {

    }

    @FXML
    private void limparButtonAction() {
        tf_login.setText("");
        pf_pass.setText("");
        tfNickname.setText("");
    }

    @FXML
    private void autenticaButtonAction() throws IOException {
        try {
            ArrayList<Campo> campos = new ArrayList<>();
            int score;
            InetAddress addr = InetAddress.getLocalHost();
            Utilizador u = MusicClient.menuLogin(new Utilizador(null, tf_login.getText(), pf_pass.getText().getBytes(), addr, 55000));

            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/Menu.fxml"));
            Parent root = loader.load();
            Menu_Controller menu = loader.getController();
            menu.setAnterior(this.atual);
            menu.setAtual(atual);
            menu.setUser(u);
            
            Stage newStage = new Stage();
            Scene scene = new Scene(root);

            newStage.setScene(scene);
            newStage.show();
            newStage.setResizable(false);
            
            //MusicClient.menuMakeChallenge("Desafio1");
            
            //this.atual.hide();

        } catch (SocketTimeoutException | UserInexistenteException ex) {
            String msg = ex.getMessage();
            Alert al = new Alert(Alert.AlertType.ERROR);
            al.setTitle("Dados inválidos");
            switch (msg) {
                case "Password":
                    al.setContentText("Password errada");
                    break;
                case "Utilizador":
                    al.setContentText("Utilizador inexistente");
                    break;
                default:
                    al.setTitle("Timeout");
                    al.setContentText("Tempo de pedido excedido");
            }
            al.showAndWait();
        }
    }

    public void setNome(String nome) {
        this.cenas = nome;
        System.err.println(nome);
    }

    public void setAtual(Stage atual) {
        this.atual = atual;
    }

    public void setAnterior(Stage anterior) {
        this.anterior = anterior;
        this.anterior.close();
    }

}

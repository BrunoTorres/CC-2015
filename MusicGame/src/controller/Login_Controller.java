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
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.ResourceBundle;
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
import musicgame.UserInexistenteException;
import musicgame.Utilizador;

/**
 *
 * @author JoaoMano
 */
public class Login_Controller extends Application implements Initializable {

    @FXML
    private AnchorPane loginPanel;

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
    private TextField tfName;

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
    private void registarAction(ActionEvent event) {
        formPanel.setVisible(false);
        nickPanel.setVisible(true);
    }
    

    @FXML
    private void cancelarButtonAction(ActionEvent event) {
        nickPanel.setVisible(false);
        formPanel.setVisible(true);

        limparButtonAction();
    }

    @FXML
    private void registarButtonAction(ActionEvent event) throws UnknownHostException, IOException {
        try {
            if (!tf_login.getText().isEmpty() && !tfName.getText().isEmpty() && !pf_pass.getText().isEmpty()) {
                boolean success;
                success = MusicClient.menuRegista(tfName.getText(), tf_login.getText(), pf_pass.getText());

                if (success) {
                    Alert al = new Alert(Alert.AlertType.INFORMATION);
                    al.setTitle("Registo");
                    al.setContentText("Registo efetuado com sucesso! Pode fazer login.");
                    al.showAndWait();
                    nickPanel.setVisible(false);
                    formPanel.setVisible(true);
                    tf_login.requestFocus();
                } else {
                    Alert al = new Alert(Alert.AlertType.ERROR);
                    al.setTitle("Registo");
                    al.setContentText("Registo não efetuado! Tente novamente.");
                    al.showAndWait();
                }
            } else {
                Alert al = new Alert(Alert.AlertType.ERROR);
                al.setTitle("Registo");
                al.setContentText("Preencha todos os campos.");
                al.showAndWait();
            }
        } catch (SocketTimeoutException ex) {
            Alert al = new Alert(Alert.AlertType.ERROR);
            al.setTitle("Timeout");
            al.setContentText("Tempo de pedido excedido");
            al.showAndWait();
        }
    }

    @FXML
    private void limparButtonAction() {
        tf_login.setText("");
        pf_pass.setText("");
        tfName.setText("");
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

            Stage newStage = new Stage();
            Scene scene = new Scene(root);

            newStage.setScene(scene);
            newStage.show();
            newStage.setResizable(false);

            menu.setAtual(newStage);
            menu.setAnterior(this.atual);

            menu.setUser(u);

            //MusicClient.menuMakeChallenge("Desafio1");
            this.button_auth.getScene().getWindow().hide();

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
    }

}

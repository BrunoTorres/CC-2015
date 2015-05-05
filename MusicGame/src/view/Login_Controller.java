/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.uminho.lei.dss.view;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
//import pt.uminho.lei.dss.db.HabitatDAO;
import pt.uminho.lei.dss.db.PersistenceException;
import pt.uminho.lei.dss.model.Funcionario;
import pt.uminho.lei.dss.model.SGH;

/**
 *
 * @author JoaoMano
 */
public class Login_Controller implements Initializable {

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
    
    private String cenas;

    //private FuncionarioDAO funcionarioDAO;
    private SGH habitat;
    private Stage atual;
    private Stage anterior;
    
    public Login_Controller() {}

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        habitat = new SGH();
      
    }

    @FXML
    private void limparButtonAction() {
        tf_login.setText("");
        pf_pass.setText("");
    }

    @FXML
    private void autenticaButtonAction() throws PersistenceException, IOException {
        Funcionario f = habitat.autenticaUtilizador(tf_login.getText(), pf_pass.getText());
        if (f!=null) {
            f.setUltimoLogin(LocalDateTime.now());
            habitat.setFuncionario(f);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Menu.fxml"));
            Parent root = loader.load();
            Menu_Controller menu_c = loader.getController();
            habitat.setF(f);
            menu_c.setHabitat(habitat);
            //menu_c.setFuncionario(f);
                    
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setOnCloseRequest((WindowEvent event) -> {
                this.habitat.closeConnection();
            });
            menu_c.setAnterior(this.atual);
            menu_c.setAtual(stage);
            
            stage.setScene(scene);
            stage.show();
            stage.setResizable(false);
            stage.setTitle("Menu");
        } else {
            Alert diag = new Alert(Alert.AlertType.ERROR);
            diag.setTitle("Erro");
            diag.setHeaderText(null);
            diag.setContentText("Username ou Password Invalidos.");
            diag.showAndWait();
        }
    }

    
    public void setNome(String nome){
        this.cenas = nome;
        System.err.println(nome);
    }
    
    public void setAtual(Stage atual){
        this.atual = atual;
    }

    public void setAnterior(Stage anterior) {
        this.anterior = anterior;
        this.anterior.close();
    }
    
    
}

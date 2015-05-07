/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;
;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import musicgame.Utilizador;import java.net.URL;
import java.util.Optional;
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
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import musicgame.MusicClient;
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
    
    public Menu_Controller(){
    }
    
    public void setAnterior(Stage ant){
        this.anterior = ant;
    }
    
    public void setAtual(Stage at){
        this.atual = at;
    }
    
    public void setUser(Utilizador u){
        this.user = u;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }

    @FXML
    private void criarDesafioAction(ActionEvent event) {
        TextInputDialog desafio = new TextInputDialog();
        desafio.setTitle("Criar novo desafio");
        desafio.setHeaderText(null);
        desafio.setContentText("Nome do desafio:");
        String name;
        
        Optional<String> res = desafio.showAndWait();
        if(res.isPresent()){
            try {
                name = res.get();
                System.out.println("NAME: " + name);
                // criarDesafio(name);
                MusicClient.menuMakeChallenge(name);
            } catch (IOException ex) {
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
       // stage.setResizable(false);
        stage.setTitle("Lista de desafios disponíveis");
        listarC.setAnterior(this.atual);
        listarC.setAtual(stage);
    }

    @FXML
    private void listarRankingAction(ActionEvent event) {

    }

    @FXML
    private void logoutAction(ActionEvent event) {

    }

    
}
/*
    @FXML
    private void projetosButtonAction() throws PersistenceException, IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Projetos.fxml"));
        Parent root = loader.load();
        Projetos_Controller proj_c = loader.getController();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        
        stage.setScene(scene);
        stage.show();
       // stage.setResizable(false);
        stage.setTitle("Projetos");
        proj_c.setAnterior(this.atual);
        proj_c.setAtual(stage);
        proj_c.setHabitat(habitat);
        
    }
*/
   
    
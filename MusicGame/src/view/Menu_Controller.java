/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.uminho.lei.dss.view;
;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import pt.uminho.lei.dss.db.PersistenceException;
import pt.uminho.lei.dss.model.SGH;

/**
 * FXML Controller class
 *
 * @author joaorua
 */
public class Menu_Controller implements Initializable {

    @FXML
    private Label l_utilizador;
    
    @FXML
    private Label l_data;
    
    @FXML
    private Label l_hora;
    
    @FXML
    private Button b_projectos;
    
    @FXML
    private Button b_voluntarios;
    
    @FXML
    private Button b_stock;
    
    @FXML
    private Button b_eventos;
    
    @FXML
    private Button b_candidaturas;
    
    @FXML
    private Button b_doadores;
    
    @FXML
    private Button b_funcionarios;
    
    @FXML
    private Button b_logout;

    //private Funcionario funcionario;
    private Stage atual;
    private Stage anterior;
    private SGH habitat;

    public SGH getHabitat() {
        return habitat;
    }

    public void setHabitat(SGH habitat) {
        this.habitat = habitat;
        this.l_utilizador.setText(habitat.getF().getUserName());
        permissoes(habitat.getF().getPermissao());
    }
    
    public void setAtual(Stage atual) {
        this.atual = atual;
    }
    
    public void setAnterior(Stage ultjanela) {
        this.anterior = ultjanela;
        this.anterior.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        String data = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        l_data.setText(data);
        String hora = new SimpleDateFormat("HH:mm:ss").format(new Date());
        l_hora.setText(hora);
    }

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
   
    @FXML
    private void voluntariosButtonAction() throws PersistenceException, IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Voluntarios.fxml"));
        Parent root = loader.load();
        Voluntarios_Controller vol_c = loader.getController();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        
        stage.setScene(scene);
        stage.show();
        stage.setTitle("Voluntario");
        vol_c.setAnterior(this.atual);
        vol_c.setAtual(stage);
        vol_c.setHabitat(habitat);
    }
    
    @FXML
    private void stockButtonAction() throws PersistenceException, IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Stock.fxml"));
        Parent root = loader.load();
        Stock_Controller stock_c = loader.getController();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        
        stage.setScene(scene);
        stage.show();
        stage.setTitle("Stock");
        stock_c.setAnterior(this.atual);
        stock_c.setAtual(stage);
        stock_c.setHabitat(habitat);
    }
    
    @FXML
    private void eventosButtonAction() throws PersistenceException, IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Eventos.fxml"));
        Parent root = loader.load();
        Eventos_Controller eventosc = loader.getController();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        
        stage.setScene(scene);
        stage.show();
        // stage.setResizable(false);
        stage.setTitle("Eventos");
        eventosc.setAnterior(this.atual);
        eventosc.setAtual(stage);
        eventosc.setHabitat(habitat);
    }
    
    @FXML
    private void candidaturasButtonAction() throws PersistenceException, IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Candidatura.fxml"));
        Parent root = loader.load();
        Candidatura_Controller cand = loader.getController();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        
        stage.setScene(scene);
        stage.show();
       // stage.setResizable(false);
        stage.setTitle("Candidaturas");
        cand.setAnterior(this.atual);
        cand.setAtual(stage);
        cand.setHabitat(habitat);
        
    }
    
    @FXML
    private void doadoresButtonAction() throws PersistenceException, IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Doadores.fxml"));
        Parent root = loader.load();
        Doadores_Controller doador_c = loader.getController();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        
        stage.setScene(scene);
        stage.show();
        //stage.setResizable(false);
        stage.setTitle("Doadores");
        doador_c.setAnterior(this.atual);
        doador_c.setAtual(stage);
        doador_c.setHabitat(habitat);
    }
    
    @FXML
    private void funcionariosButtonAction() throws PersistenceException, IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Funcionarios.fxml"));
        Parent root = loader.load();
        Funcionarios_Controller fun_c = loader.getController();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        
        stage.setScene(scene);
        stage.show();
        //stage.setResizable(false);
        stage.setTitle("Funcionario");
        fun_c.setAnterior(this.atual);
        fun_c.setAtual(stage);
        fun_c.setHabitat(habitat);
        
    }
    
    @FXML
    private void logoutButtonAction() throws PersistenceException, IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
        Parent root = loader.load();
        Login_Controller log_c = loader.getController();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        
        this.habitat.closeConnection();
        
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);
        stage.setTitle("Login");
        log_c.setAnterior(this.atual);
        log_c.setAtual(stage);
        this.atual.close();
    }
    
    private void permissoes(int idfuncao){
        switch(idfuncao){
            case 1:
                b_funcionarios.setVisible(false);
                break;
            case 2:
                b_funcionarios.setVisible(false);
                break;
            case 3:
                b_funcionarios.setVisible(false);
                break;
            default:
                b_funcionarios.setVisible(true);
                break;
        }
    }
}

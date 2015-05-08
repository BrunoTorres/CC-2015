/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import musicgame.Utilizador;



public class JogarController extends Application implements Initializable{

     @FXML
    private Button butQuit;

    @FXML
    private Label labelNumPergunta;

    @FXML
    private Button butOK;

    @FXML
    private ImageView imagePergunta;

    @FXML
    private RadioButton resposta2;

    @FXML
    private RadioButton resposta1;

    @FXML
    private Text labelPergunta;

    @FXML
    private Label labelTimer;
    
    @FXML
    private Label labelTimerJogo;
    
    @FXML
    private Label labelNomeDesafio;

    @FXML
    private RadioButton resposta3;

    private int timerPergunta;
    private Timeline timeLine;
    private int timerJogo;
    private Timeline tlineJogo;
    
    private Stage atual;
    private Stage anterior;
    private Utilizador u;
    
    private LocalDateTime data;
    
    public JogarController(LocalDateTime data, Utilizador u){
        this.data = data;
        this.u = u;
        LocalDateTime n = LocalDateTime.now();
        if(this.data.isAfter(n)){
            this.timerJogo = (int) n.until(this.data, ChronoUnit.SECONDS);
            this.labelTimerJogo.setText(String.valueOf(this.timerJogo));
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        //labelTimer = new Label();
        /*labelTimer.setText("20");
        timerPergunta = Integer.parseInt(labelTimer.getText());
        timeLine = new Timeline();*/

        tlineJogo.setCycleCount(Timeline.INDEFINITE);
        tlineJogo.getKeyFrames().add(new KeyFrame(Duration.seconds(1), (ActionEvent event) -> {
            timerJogo--;
            labelTimerJogo.setText(String.valueOf(timerJogo));
            if(timerJogo <= 15){
                labelTimerJogo.setTextFill(Color.RED);
            }
            if (timerJogo <= 0) {
                tlineJogo.stop();
                
            }
        }));
        timeLine.playFromStart();
        
        imagePergunta.setImage(new Image(JogarController.class.getResourceAsStream("../musicgame/imagens/000004.jpg")));
        
        //labelPergunta.setText();
        
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/Jogar.fxml"));
        Parent root = loader.load();
        
        Scene scene = new Scene(root);
        
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(false);
    }
    
    public static void main(String[] args){
        launch(args);
    }

}

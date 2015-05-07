/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
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
    private RadioButton resposta3;

    private int timerPergunta;
    private Timeline timeLine;
    private int timerJogo;
    private Timeline tlineJogo;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        //labelTimer = new Label();
        labelTimer.setText("20");
        timerPergunta = Integer.parseInt(labelTimer.getText());
        timeLine = new Timeline();

        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.getKeyFrames().add(new KeyFrame(Duration.seconds(1), (ActionEvent event) -> {
            timerPergunta--;
            labelTimer.setText(String.valueOf(timerPergunta));
            if(timerPergunta <= 15){
                labelTimer.setTextFill(Color.RED);
            }
            if (timerPergunta <= 0) {
                timeLine.stop();
            }
        }));
        timeLine.playFromStart();
        
        imagePergunta.setImage(new Image(JogarController.class.getResourceAsStream("../musicgame/imagens/000004.jpg")));
        
        //labelPergunta.setText();
        
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Jogar.fxml"));
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

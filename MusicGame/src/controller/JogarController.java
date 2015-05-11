/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import musicgame.Desafio;
import musicgame.InsuficientPlayersException;
import musicgame.MusicClient;
import musicgame.Pergunta;
import musicgame.Utilizador;

public class JogarController implements Initializable {

    @FXML
    private Button butQuit;

    @FXML
    private Label labelNumPergunta;


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

    @FXML
    private AnchorPane panelJogo;

    @FXML
    private AnchorPane panelWait;

    @FXML
    private AnchorPane panelPergunta;

    private int timerPergunta;
    private Timeline timeLine;
    private int timerJogo;
    private Timeline tlineJogo;

    private Stage atual;
    private Stage anterior;
    private Utilizador user;

    private LocalDateTime data;
    private Desafio d;
    private Pergunta p;
    private int nQuestion = 1;
    private MediaPlayer mp;

    private boolean quit;

    public JogarController() {
        this.quit = false;
    }

    public JogarController(Desafio d, Utilizador u) {
        /*this.data = d.getLocalDate();
         this.user = u;
         LocalDateTime n = LocalDateTime.now();
         if (this.data.isAfter(n)) {
         this.timerJogo = (int) n.until(this.data, ChronoUnit.SECONDS);
         this.labelTimerJogo.setText(String.valueOf(this.timerJogo));
         }*/
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        //labelTimer = new Label();
        /*labelTimer.setText("20");
         timerPergunta = Integer.parseInt(labelTimer.getText());
         timeLine = new Timeline();*/

        //imagePergunta.setImage(new Image(JogarController.class.getResourceAsStream("../musicgame/imagens/000004.jpg")));
        //labelPergunta.setText();
    }

    public void setAnterior(Stage ant) {
        this.anterior = ant;
        this.atual.setOnCloseRequest((WindowEvent event) -> {
            if(mp!=null){
                mp.dispose();
            }
            quit=true;
            anterior.show();

        });
    }

    public void setAtual(Stage at) {
        this.atual = at;
    }

    public void setUser(Utilizador u) {
        this.user = u;
    }

    @FXML
    private void butQuitAction(ActionEvent event) {
        this.quit = true;
        this.anterior.show();
        this.atual.close();
    }

    public void setDesafio(Desafio d) {
        this.d = d;
        this.labelNomeDesafio.setText(d.getNome());
        this.data = this.d.getLocalDate();
        LocalDateTime n = LocalDateTime.now();
        tlineJogo = new Timeline();
        if (this.data.isAfter(n)) {
            this.timerJogo = (int) n.until(this.data, ChronoUnit.SECONDS);
            this.labelTimerJogo.setText(String.valueOf(this.timerJogo));
        }
        tlineJogo.setCycleCount(Timeline.INDEFINITE);
        tlineJogo.getKeyFrames().add(new KeyFrame(Duration.seconds(1), (ActionEvent event) -> {
            timerJogo--;
            labelTimerJogo.setText(String.valueOf(timerJogo));
            if (timerJogo <= 15) {
                labelTimerJogo.setTextFill(Color.RED);
            }
            if (timerJogo <= 0) {
                tlineJogo.stop();
                this.panelWait.setVisible(false);
                this.panelJogo.setVisible(true);
                this.panelPergunta.setVisible(true);
                jogar();
            }

        }));
        tlineJogo.playFromStart();
    }

    private void apresentaPergunta(Pergunta pg) {
        if (pg != null) {
            this.labelPergunta.setText(pg.getPergunta());
            this.resposta1.setText(pg.getRespostaIndice(0));
            this.resposta2.setText(pg.getRespostaIndice(1));
            this.resposta3.setText(pg.getRespostaIndice(2));
            this.imagePergunta.setImage(new Image("file:".concat(pg.getImagem())));
            this.labelNumPergunta.setText(String.valueOf(this.nQuestion));
            this.nQuestion++;
            Media m = new Media("file:///".concat(pg.getMusica()).replace("\\", "%5C"));
            mp = new MediaPlayer(m);
            mp.play();
        }
    }

    private void jogar() {
        try {
            this.p = MusicClient.jogar(quit);
            apresentaPergunta(this.p);
        } catch (SocketTimeoutException ex) {
            Logger.getLogger(JogarController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JogarController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedAudioFileException | LineUnavailableException | InsuficientPlayersException ex) {
            Logger.getLogger(JogarController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}

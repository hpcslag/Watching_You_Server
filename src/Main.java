/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.github.sarxos.webcam.Webcam;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.List;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import us.sosia.video.stream.agent.StreamServerAgent;

/**
 *
 * @author hpcslag
 */
public class Main extends Application {
    
    public static int webcam_index = 0;
    
    @Override
    public void start(Stage primaryStage) {
        Text title = new Text("Webcam Zugriffskontrolle Management");
        title.setTextAlignment(TextAlignment.CENTER);
        title.setY(25);
        title.setX(5);
        title.setFont(Font.font("Arial Black",FontWeight.BOLD,18));
        title.setWrappingWidth(300);
        
        Text description = new Text(
                "Hallo, vor Webcam-Dienst starten, müssen Sie Webcam-Gerät zu überprüfen Erfolg installiert sind.\n" +
                "\n" +
                "Wenn Sie gerade nicht, was in \"Webcam Gerät\" zu sehen ist, bedeutet es, das Gerät konnte nicht gefunden werden.");
        description.setTextAlignment(TextAlignment.CENTER);
        description.setY(75);
        description.setX(5);
        description.setFont(Font.font("Calibri"));
        description.setWrappingWidth(300);
        
        Label w_selector_text = new Label("WebCam Gerät: ");
        w_selector_text.setFont(Font.font("Calibri",FontWeight.BOLD,14));
        w_selector_text.setTranslateY(170);
        w_selector_text.setTranslateX(25);
        
        ChoiceBox select_device = new ChoiceBox();
        select_device.setItems(FXCollections.observableArrayList("Bitte wählen Geräte",new Separator()));
        select_device.getItems().addAll(Webcam.getWebcams());
        select_device.setTranslateY(167);
        select_device.setTranslateX(125);
        select_device.getSelectionModel().selectFirst();
        select_device.getSelectionModel().selectedIndexProperty().addListener(
                new ChangeListener<Number>(){
                    public void changed(ObservableValue observable,Number old_value,Number new_value){
                        webcam_index = new_value.intValue()-2;
                    }
                }
        );
        
        Label type_password_label = new Label("Passwort eingeben: ");
        type_password_label.setFont(Font.font("Calibri",FontWeight.BOLD,14));
        type_password_label.setTranslateY(205);
        type_password_label.setTranslateX(25);
        
        PasswordField type_pass = new PasswordField();
        type_pass.setTranslateY(202);
        type_pass.setTranslateX(145);
        
        Text ip_running = new Text();
        ip_running.setFont(Font.font("Calibri",FontWeight.BOLD,18));
        ip_running.setFill(Color.RED);
        ip_running.setTranslateY(300);
        ip_running.setTranslateX(25);
        
        Button serv_btn = new Button();
        serv_btn.setText("Start Server");
        serv_btn.setFont(Font.font("Calibri",FontWeight.BOLD,14));
        serv_btn.setStyle("-fx-text-fill: green;");
        serv_btn.setTranslateY(250);
        serv_btn.setTranslateX(25);
        serv_btn.setOnAction(new EventHandler<ActionEvent>() {
            public boolean server_is_start = false;
            @Override
            public void handle(ActionEvent event) {
                if(server_is_start){
                    stopServer();
                    serv_btn.setText("Start Server");
                    serv_btn.setStyle("-fx-text-fill: green;");
                    type_pass.setDisable(false);
                    select_device.setDisable(false);
                    ip_running.setText("");
                }else{
                    runServer();
                    serv_btn.setText("Stop Server");
                    serv_btn.setStyle("-fx-text-fill: red;");
                    type_pass.setDisable(true);
                    select_device.setDisable(true);
                    ip_running.setText("Server is running in: \nlocalhost:3333");
                }
                server_is_start = !server_is_start;
                primaryStage.show();
            }
        });
                
        Label author = new Label("Author: MacTaylor 2016.08.01");
        author.setTranslateY(330);
        author.setTranslateX(25);
        
        Group group = new Group();
        group.getChildren().add(title);
        group.getChildren().add(description);
        group.getChildren().add(w_selector_text);
        group.getChildren().add(select_device);
        group.getChildren().add(type_password_label);
        group.getChildren().add(type_pass);
        group.getChildren().add(serv_btn);
        group.getChildren().add(ip_running);
        group.getChildren().add(author);
        
        Scene scene = new Scene(group, 300, 350);
        
        primaryStage.setResizable(false);
        primaryStage.setTitle("WZW V1.0");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        launch(args);
        /*try{
            Webcam webcam = Webcam.getWebcams().get(webcam_index);
            webcam.open();
            BufferedImage image = webcam.getImage();
            ImageIO.write(image, "JPG", new File("C:\\Users\\hpcslag\\Desktop\\test.jpg"));
        }catch(IOException e){
            System.out.println("Can't Handle webcam");
        }*/
    }
    
    public static StreamServerAgent serverAgent;
    
    public static void runServer(){
        Webcam.setAutoOpenMode(true);
        Webcam webcam = Webcam.getWebcams().get(webcam_index);
        Dimension dimension = new Dimension(320, 240);
        webcam.setViewSize(dimension);
        
        serverAgent = new StreamServerAgent(webcam, dimension);
        serverAgent.start(new InetSocketAddress("localhost", 3333));
    }
    
    public static void stopServer(){
        serverAgent.stop();
    }
}

package Client.UI;

import Client.Login.Main;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientPageController implements Initializable {

    @FXML
    ScrollPane clientScrollPane;


    @Override
    public void initialize(URL location, ResourceBundle resources)  {

        if(Client.Login.Main.isNewUser){
            try {
                Socket sock = new Socket(Main.HUB_IP, Main.PORT);
                DataInputStream dis = new DataInputStream(sock.getInputStream());
                DataOutputStream dout = new DataOutputStream(sock.getOutputStream());

                ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());

                dout.writeUTF("#NEWUSER");
                dout.writeUTF(Main.USERNAME);
                if(dis.readUTF().equals( "#NOFILES")){
                    //TODO NOTHING
                }else{
                    //TODO: Some Operation
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }else{

            // TODO: if not NEWUSER
        }

        VBox vbox = new VBox(50);
        vbox.setFillWidth(true);

        try {
                Reflection reflection = new Reflection();
                reflection.setFraction(0.75);

                Label l = new Label("Recommeded");
                l.setEffect(reflection);
                vbox.getChildren().add(l);
                vbox.getChildren().add(createSection());
                vbox.getChildren().add(createSection());

                l = new Label("Miscellanous");
                l.setEffect(reflection);
                vbox.getChildren().add(l);

                for (int i = 2; i < 4; i++) {

                    vbox.getChildren().add(createSection());

                }
        }catch(Exception e){
            e.printStackTrace();
        }
        StackPane pane = new StackPane(vbox);
        pane.setPadding(new Insets(15));
        clientScrollPane.setContent(pane);
    }


    HBox createSection() throws Exception{
        HBox hbox  = new HBox(15);


        for(int i=0;i<4;i++){
            AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("../Layouts/thumbNailLayout.fxml"));
            ((JFXButton)(anchorPane.getChildren().get(1))).setText("Vide Info "+i);
            hbox.getChildren().add(anchorPane);
        }

        return hbox;
    }


}

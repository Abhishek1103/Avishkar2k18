package ui;

import com.jfoenix.controls.JFXButton;
import constants.Constants;
import constants.Flags;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import login.LayoutController;
import org.omg.CORBA.MARSHAL;
import utility.GetTeacherMapService;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static constants.Constants.SERVER_IP;

public class ChatPreviewLayoutController implements Initializable {

    @FXML
    VBox vboxForButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GetTeacherMapService sr = new GetTeacherMapService();
        sr.start();
        sr.setOnSucceeded(e-> {
            try {
                if (Flags.isStudent) {
                    System.out.println("Loading student chat preview");
                    studentLoader();
                }else {
                    teacherLoader();
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        });
    }


    private void studentLoader() throws IOException {
        HashMap<String, String> map = LayoutController.messageUsers;
        System.out.println("MessageUsers map: "+map);
        if(map!=null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                AnchorPane pane = createItem(entry.getKey());
                vboxForButton.getChildren().add(pane);
            }
        }

        //Now set message according to the name which is appended as name:msg
    }

    //for teacher list of all student name from which he got message

    private void teacherLoader() {
        HashMap<String, String> map = MainTeacherPageController.queryUsername;
        for(Map.Entry<String, String> entry: map.entrySet()){
            AnchorPane pane = createItem(entry.getKey());
            vboxForButton.getChildren().add(pane);
        }
    }


    private AnchorPane createItem(String name){
        AnchorPane pane = null;
        try{
            pane = FXMLLoader.load(getClass().getClassLoader().getResource("layouts/messageSenderItem.fxml"));
            Label l = (Label)(pane.getChildren().get(0));
            l.setText(name);

            JFXButton button = (JFXButton)(pane.getChildren().get(2));
            button.setOnAction(e->{
                // Todo: Open the chat box
                try {
                    MainTeacherPageController.currentChatUser = name;
                    Stage playWindow = new Stage();
                    playWindow.initModality(Modality.APPLICATION_MODAL);
                    Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("layouts/chatFrame.fxml"));
                    Scene sc = new Scene(root);
                    playWindow.setScene(sc);
                    playWindow.showAndWait();

                    playWindow.setOnCloseRequest(ev -> MainTeacherPageController.currentChatUser = "");
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        return pane;
    }
}

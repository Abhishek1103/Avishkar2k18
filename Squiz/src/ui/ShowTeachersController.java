package ui;

import com.jfoenix.controls.JFXButton;
import constants.Constants;
import data.Teacher;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utility.GetTeacherMapService;

import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ShowTeachersController implements Initializable {

    @FXML
    ScrollPane scrollPane;

    @FXML
    AnchorPane mainAnchorPane;

    VBox vBox;

    protected static boolean isMessagesClicked = false;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try{

            GetTeacherMapService getTeacherMapService = new GetTeacherMapService();
            getTeacherMapService.start();

            getTeacherMapService.setOnSucceeded(e -> {
                try {
                    init();

                }catch (Exception ex){
                    ex.printStackTrace();
                }
            });

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void init() throws Exception {
        // TODO: initialize the UI
        vBox = new VBox(10);
        vBox.setPadding(new Insets(15));

        HashMap<String, byte[]> map = Constants.TEACHER_MAP;
        HashMap<String, Teacher> stringTeacherMap = new HashMap<>();

        String encodedKey = new BufferedReader(new FileReader(Constants.SERVER_SECRET_KEY_PATH)).readLine();
        System.out.println("Encoded key: "+encodedKey);
        SecretKey serverSecretKey = Constants.aes.decodeKey(encodedKey);

        for(Map.Entry<String, byte[]> entry: map.entrySet()){
            String teacherName = entry.getKey();
            byte[] encryptedTeacher = entry.getValue();
            ByteArrayInputStream bais = new ByteArrayInputStream(encryptedTeacher);
            Teacher teacherObj = (Teacher)Constants.aes.decryptWithAES(bais, serverSecretKey);
            stringTeacherMap.put(teacherName, teacherObj);
            AnchorPane pane = createItem(teacherName, teacherObj);

            vBox.getChildren().add(pane);
        }

        MainStudentPageController.stringTeacherMap = stringTeacherMap;

        scrollPane.setContent(vBox);

    }


    private AnchorPane createItem(String teacherName, Teacher teacher)throws Exception{
        AnchorPane pane = FXMLLoader.load(getClass().getClassLoader().getResource("layouts/teacherItemLayout.fxml"));

        Label l = (Label)(pane.getChildren().get(0));
        JFXButton button = (JFXButton)(pane.getChildren().get(1));
        JFXButton chatButton = (JFXButton)(pane.getChildren().get(2));

        l.setText(teacherName);

        button.setOnAction(e -> {
            // TODO: Show subects of the selected teacher
            try{
                ShowTeachersSubjectsController.teacherName = teacherName;
                AnchorPane p = FXMLLoader.load(getClass().getClassLoader().getResource("layouts/showTeachersSubjectsLayout.fxml"));
                //mainAnchorPane.getChildren().remove(scrollPane);
                scrollPane.setContent(p);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        });

        chatButton.setOnAction(e -> {
            ChatFrameController.receipientName = teacher.getTeacherName();
            MainTeacherPageController.currentChatUser = teacher.getTeacherName();
            try {
                System.out.println("Chat button clicked");
                Stage playWindow = new Stage();
                playWindow.initModality(Modality.APPLICATION_MODAL);
                Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("layouts/chatFrame.fxml"));
                Scene sc = new Scene(root);
                playWindow.setScene(sc);
                playWindow.showAndWait();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        });

        return pane;
    }


//    private void initMessages() throws Exception{
//        vBox = new VBox(10);
//        vBox.setPadding(new Insets(15));
//
//        HashMap<String, byte[]> map = Constants.TEACHER_MAP;
//        HashMap<String, Teacher> stringTeacherMap = new HashMap<>();
//
//        String encodedKey = new BufferedReader(new FileReader(Constants.SERVER_SECRET_KEY_PATH)).readLine();
//        System.out.println("Encoded key: "+encodedKey);
//        SecretKey serverSecretKey = Constants.aes.decodeKey(encodedKey);
//    }
//
//    private AnchorPane createItemM() throws Exception{
//        AnchorPane pane = FXMLLoader.load(getClass().getClassLoader().getResource("layouts/"))
//    }

}

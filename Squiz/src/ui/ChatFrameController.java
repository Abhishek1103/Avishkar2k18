package ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import constants.Constants;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import utility.SendMessageService;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ChatFrameController implements Initializable
{

    @FXML
    AnchorPane mainAnchorPane, textAreaAnchorPane;
    @FXML
    Label nameLabel;
    @FXML
    JFXTextField inputTextField;
    @FXML
    JFXButton sendButton;

    protected  static String teacherName;
    int chatPort;

    protected static String receipientName;

    public static JFXTextArea msgTextArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {

            msgTextArea = new JFXTextArea();
            msgTextArea.setPrefHeight(650);
            msgTextArea.setPrefWidth(548);

            textAreaAnchorPane.getChildren().setAll(msgTextArea);

            initChatFrameController();
            nameLabel.setText(receipientName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initChatFrameController() throws IOException {
        this.chatPort = 13001;
        Socket sock = new Socket(Constants.SERVER_IP,13001);
        DataInputStream dis = new DataInputStream(sock.getInputStream());
        DataOutputStream dout = new DataOutputStream(sock.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
        ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());

        dout.writeUTF("#GETLOGCOPY");
        System.out.println("Sent #GETLOGCOPY");
        dout.writeUTF(MainTeacherPageController.currentChatUser);
        System.out.println("CurrentChat user: "+MainTeacherPageController.currentChatUser);
        dout.writeUTF(Constants.USERNAME);
        System.out.println("Sent username: " +Constants.USERNAME);

        String message = dis.readUTF();
        System.out.println("Received String: "+message);
        msgTextArea.appendText("\n"+message+"\n");


    }

    private void setMessageArea() throws IOException { // for student

    }

    public void sendButtonClicked() throws IOException {
        String msg = inputTextField.getText();
        msgTextArea.appendText(msg+"\n");
        SendMessageService service = new SendMessageService(MainTeacherPageController.currentChatUser,msg);
        service.start();
        service.setOnSucceeded(e -> {
            System.out.println("Send Message succeded");
        });

    }

    /*
    * Do these addition in abhishek code
    * Add chat button to student when clicked a teacher
    * Add received button to dashboard of teacher
    * */

    public static void appendToTextArea(String msg){
        msgTextArea.appendText(msg);
    }
}

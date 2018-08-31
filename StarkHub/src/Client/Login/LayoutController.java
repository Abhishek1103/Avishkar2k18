package Client.Login;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sun.security.util.Password;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

public class LayoutController implements Initializable {

    @FXML
    AnchorPane pane1, pane2, rootPane;
    @FXML
    ImageView signInImg, signUpImg;
    @FXML
    Button signIn, signUp;
    @FXML
    JFXTextField signInUsernameTxt, signUpUsername, signUpName;
    @FXML
    JFXPasswordField signInPass, signUpPass;

    String userHome;

    JFXPopup invalidUsernamePopup;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userHome = System.getProperty("user.home");

        invalidUsernamePopup = initInvalidUsernamePopup();

        pane1.setVisible(false);
    }

    public void signInClicked(){
        if(!pane2.isVisible()) {
            pane2.setVisible(true);
            pane1.setVisible(false);
        }
    }

    public void signUpClicked(){
        if(!pane1.isVisible()) {
            pane1.setVisible(true);
            pane2.setVisible(false);
        }
    }

    public void signInSubmit(){

        String userName = signInUsernameTxt.getText();
        String pass = signInPass.getText();



        File f = new File(userHome+"/.starkHub/credentials.cfg");
        if(f.exists() && f.isFile()){
            try {
                Scanner sc = new Scanner(f);
                String savedUsername = sc.nextLine();
                String savedPassword = sc.nextLine();

                if(savedUsername.equals(userName) && savedPassword==pass){
                    Main.USERNAME = userName;
                    startMainPage();
                }else{
                    System.out.println("AUTHENTICATION FAILED");
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        else{
            System.out.println("New user...SignUp !!    ");
        }

    }

    public void signUpSubmit(){

        System.out.println("SignUpClicked..!!");

        String name = signUpName.getText();
        String userName = signUpUsername.getText();
        String pass = signUpPass.getText();
        boolean res;
        if(res = authenticateSignUp(userName)) {
            System.out.println("Auth: "+res);
            File f = new File(userHome + "/.starkHub/credentials.cfg");

            if (f.exists() && f.isFile() && f.length() != 0) {
                try {
                    System.out.println("User Already Exists");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Creating new User");
                try {
                    createNewUser(userName, pass, name);
                    Main.USERNAME = userName;
                    startMainPage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else{
            System.out.println("Auth: "+res);
            signUpUsername.setText("");
            invalidUsernamePopup.show(signUpUsername,JFXPopup.PopupVPosition.TOP,JFXPopup.PopupHPosition.LEFT);
        }

    }

    public void createNewUser(String userName, String passWord, String name) throws Exception{
        File f = new File(userHome+"/.starkHub/credentials.cfg");
        new File(userHome+"/.starkHub").mkdir();
        PrintWriter pw = new PrintWriter(f);
        pw.println(userName);
        pw.println(passWord);
        pw.println(name);
        pw.close();
    }

    public void exitClicked(){
        System.exit(0);
    }


    public void startMainPage(){
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("../Layouts/mainFrame.fxml"));
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
        Stage curStage = (Stage) rootPane.getScene().getWindow();

        //curStage.initStyle(StageStyle.DECORATED);
        curStage.setScene(new Scene(root));
    }


    boolean authenticateSignUp(String username){
        boolean result = false;
        try {
            Socket hubConn = new Socket(Main.HUB_IP, Main.PORT);
            DataInputStream din = new DataInputStream(hubConn.getInputStream());
            DataOutputStream dout = new DataOutputStream(hubConn.getOutputStream());

            ObjectInputStream ois = new ObjectInputStream(hubConn.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(hubConn.getOutputStream());

            dout.writeUTF("#USERNAME");
            dout.writeUTF(username);
            result = din.readBoolean();

            dout.close();
            din.close();
            hubConn.close();

        }catch( Exception e){
            e.printStackTrace();
        }


        return result;
    }

    JFXPopup initInvalidUsernamePopup(){
        Label l = new Label("Username already taken..!!");
        l.setStyle("-fx-foreground-color:#ff0000");
        VBox vbox = new VBox(l);
        vbox.setPadding(new Insets(10));
        JFXPopup p = new JFXPopup(vbox);
        return p;
    }

}

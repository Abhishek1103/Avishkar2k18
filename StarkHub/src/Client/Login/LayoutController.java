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
import javafx.scene.control.Tooltip;
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

    public static String USERNAME = "";


    JFXPopup invalidUsernamePopup, emptyPopup;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userHome = System.getProperty("user.home");

        invalidUsernamePopup = initInvalidUsernamePopup();
        emptyPopup = initEmptyPopup();
        signUpPass.setTooltip(new Tooltip("-> Password should be alphanumeric and 8 chars in length.\n-> Should have a special char .*_@#\n"));
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

        if(userName==null || userName.isEmpty()){
            emptyPopup.show(signInUsernameTxt, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT);
            return;
        }

        if(pass == null || pass.isEmpty()){
            emptyPopup.show(signInPass, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT);
            return;
        }

        File f = new File(userHome+"/starkhub/"+USERNAME+"/credentials.cfg");
        if(f.exists() && f.isFile()){
            try {
                BufferedReader br = new BufferedReader(new FileReader(f));
                String savedUsername = br.readLine();
                String savedPassword = br.readLine();

                System.out.println("userName from file: "+savedUsername);
                System.out.println("password from file: "+savedPassword);

                if(savedUsername.equals(userName) && savedPassword.equals(pass)){
                    Main.USERNAME = userName;
                    Main.isNewUser = false;
                    USERNAME = userName;
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
        if(!preValidation(name, userName,pass)){
            return;
        }
        boolean res;
        if(res = authenticateSignUp(userName)) {
            System.out.println("Auth: "+res);
            File f = new File(userHome + "/starkhub/"+userName+"/credentials.cfg");

            if (f.exists() && f.isFile() && f.length() != 0) {
                try {
                    System.out.println("A User Already Exists");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Creating new User");
                try {
                    createNewUser(userName, pass, name);
                    Main.USERNAME = userName;
                    USERNAME = userName;
                    Main.isNewUser = true;
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
        File f = new File(userHome+"/starkhub/"+userName+"/credentials.cfg");
        new File(userHome+"/starkhub").mkdir();
        new File(userHome+"/starkhub/"+userName+"/thumbnails").mkdirs();
        new File(userHome+"/starkhub/"+userName+"/playlists").mkdirs();
        new File(userHome+"/starkhub/"+userName+"/mychannels").mkdirs();
        new File(userHome+"/starkhub/"+userName+"/temp").mkdirs();
        new File(userHome+"/starkhub/"+userName+"/watchLater").mkdirs();
        new File(userHome+"/starkhub/"+userName+"/history").mkdirs();
        new File(userHome+"/starkhub/"+userName+"/comments").mkdirs();
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

    JFXPopup initEmptyPopup(){
        Label l = new Label("This is a required Feild\nand Cannot be empty");
        l.setWrapText(true);
        VBox vbox = new VBox(l);
        vbox.setPadding(new Insets(10));
        JFXPopup p = new JFXPopup(vbox);
        return p;
    }

    boolean preValidation(String name, String userName, String pass){
        if(name == null || name.isEmpty()){
            emptyPopup.show(signUpName, JFXPopup.PopupVPosition.TOP ,JFXPopup.PopupHPosition.LEFT);
           return false;
        }if(userName == null || userName.isEmpty()){
            emptyPopup.show(signUpUsername, JFXPopup.PopupVPosition.TOP ,JFXPopup.PopupHPosition.LEFT);
            return false;
        }if(pass == null || pass.isEmpty()){
            emptyPopup.show(signUpPass, JFXPopup.PopupVPosition.TOP ,JFXPopup.PopupHPosition.LEFT);
            return false;
        }
        if(pass.length()<8){
            Label l = new Label("Password too short.\nMust be 8 chars");
            l.setWrapText(true);
            VBox vbox = new VBox(l);
            vbox.setPadding(new Insets(10));
            JFXPopup p = new JFXPopup(vbox);
            p.show(signUpPass, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT);
            signUpPass.clear();
            return false;
        }
        if(!name.matches("[a-zA-Z ]+")){
            Label l = new Label("Name cannot contain digits or special chars");
            l.setWrapText(true);
            VBox vbox = new VBox(l);
            vbox.setPadding(new Insets(10));
            JFXPopup p = new JFXPopup(vbox);
            p.show(signUpName, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT);
            return false;
        }

        return true;
    }


}

package login;


import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;

import constants.Constants;
import constants.Flags;
import encryption.AES;
import encryption.RSA;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

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
    @FXML
    JFXRadioButton signInStudentRadioButton,signUpStudentRadioButton, signInTeacherRadioButton,signUpTeacherRadioButton;

    @FXML
    ToggleGroup signInToggleGroup, signUpToggleGroup;

    String userHome;

    public static String USERNAME = "";

    JFXPopup invalidUsernamePopup, emptyPopup;



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        signInToggleGroup = new ToggleGroup();
        signUpToggleGroup = new ToggleGroup();

        signInStudentRadioButton.setToggleGroup(signInToggleGroup);
        signInTeacherRadioButton.setToggleGroup(signInToggleGroup);
        signUpStudentRadioButton.setToggleGroup(signUpToggleGroup);
        signUpTeacherRadioButton.setToggleGroup(signUpToggleGroup);

        Constants.USER_HOME = System.getProperty("user.home");

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

    // Submitting signIn details
    public void signInSubmit(){

        String userName = signInUsernameTxt.getText();
        String pass = signInPass.getText();

        if(signUpStudentRadioButton.isSelected()){
            Flags.isStudent = true;
            Flags.isTeacher = false;
        }
        else if(signUpTeacherRadioButton.isSelected()){
            Flags.isTeacher = true;
            Flags.isStudent = false;
        }else{
            System.out.println("Select a choice");
            // TODO: Show Popup
            return;
        }


        if(userName==null || userName.isEmpty()){
            emptyPopup.show(signInUsernameTxt, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT);
            return;
        }

        if(pass == null || pass.isEmpty()){
            emptyPopup.show(signInPass, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT);
            return;
        }

        String path = "";

        if(Flags.isTeacher){
            path = Constants.USER_HOME +Constants.SQUIZ_DIR+"teacher/"+userName+"/credentials.cfg";
        }else if(Flags.isStudent){
            path = Constants.USER_HOME +Constants.SQUIZ_DIR+"student/"+userName+"/credentials.cfg";
        }
        File f = new File(path);
        if(f.exists() && f.isFile()){
            try {

                boolean isAuthorised = readFromCredentialsFile(userName, pass, path);

                if(isAuthorised){
                    // TODO: Open main page
                    Constants.USERNAME = userName;
                    Constants.USER_DIR = path.substring(0, path.lastIndexOf('/')+1);
                    if(Flags.isTeacher){
                        startTeacherMainPage();
                    }else if(Flags.isStudent){
                        startStudentMainPage();
                    }
                }else{
                    System.out.println("Authorisation failed");
                    // TODO: show a GUI alert
                }

            }catch(Exception e){
                e.printStackTrace();
            }
        }
        else{
            System.out.println("New user...SignUp !!    ");
        }

    }

    // Submitting signUp details
    public void signUpSubmit(){

        System.out.println("SignUpClicked..!!");

        String name = signUpName.getText();
        String userName = signUpUsername.getText();
        String pass = signUpPass.getText();
        if(!preValidation(name, userName,pass)){
            System.out.println("Invalid Entries");
            return;
        }
        boolean res;
        if(res = authenticateSignUp(userName)) {
            System.out.println("Auth: "+res);

            if(signUpStudentRadioButton.isSelected()){
                Flags.isStudent = true;
                Flags.isTeacher = false;
            }
            else if(signUpTeacherRadioButton.isSelected()){
                Flags.isTeacher = true;
                Flags.isStudent = false;
            }else{
                System.out.println("Select a choice");
                // TODO: Show Popup
                return;
            }

            String path = "";

            if(Flags.isTeacher){
                path = Constants.USER_HOME +Constants.SQUIZ_DIR+"teacher/"+userName+"/credentials.cfg";
            }else if(Flags.isStudent){
                path = Constants.USER_HOME +Constants.SQUIZ_DIR+"student/"+userName+"/credentials.cfg";
            }


            System.out.println("Creating new User");
            try {
                createDirectories(userName);
                writeCredentialsFile(userName, pass, name, path);
                Constants.USERNAME = userName;
                Constants.USER_DIR = path.substring(0, path.lastIndexOf('/')+1);
                System.out.println("User Directory: "+Constants.USER_DIR);

                // TODO: Generate keys, save them locally


                Constants.rsa = new RSA();
                Constants.rsa.generateKeyPair();
                Constants.rsa.savePrivateKey("Private.key");
                Constants.rsa.savePublicKey("Public.key");

                Constants.PUBLIC_KEY_PATH = Constants.USER_DIR+"keys/Public.key";
                Constants.PRIVATE_KEY_PATH = Constants.USER_DIR+"keys/Private.key";

                Constants.aes = new AES();
                Constants.aes.generateAESKey();
                String encodedAESKey = Constants.aes.getEncodedKey();
                String aesKeyPath = Constants.USER_DIR+"keys/Secretkey.key";
                Constants.SECRET_KEY_PATH = aesKeyPath;

                PrintWriter pw = new PrintWriter(new FileWriter(new File(aesKeyPath)));
                pw.println(encodedAESKey);
                pw.close();

                // TODO: contact the server


                if(Flags.isTeacher){
                    startTeacherMainPage();
                }else if(Flags.isStudent){
                    startStudentMainPage();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }else{
            System.out.println("Auth: "+res);
            signUpUsername.setText("");
            invalidUsernamePopup.show(signUpUsername, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT);
        }

    }

    // Creating a new user and directory structure
    public void createDirectories(String userName){
        String type="";
        if(Flags.isTeacher)
            type = "teacher";
        else if(Flags.isStudent)
            type = "student";

        try {
            makeDirectory(Constants.USER_HOME + Constants.SQUIZ_DIR + type + "/" + userName + "/");
            makeDirectory(Constants.USER_HOME + Constants.SQUIZ_DIR + type + "/" + userName + "/"+"subjects/");
            makeDirectory(Constants.USER_HOME + Constants.SQUIZ_DIR + type + "/" + userName + "/"+"keys");
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void exitClicked(){
        System.exit(0);
    }


    // Starting the main view of the application
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

        // Action on the application to be closed
        curStage.setOnCloseRequest(e -> {
            try {

//                ByeService bye = new ByeService();
//                bye.start();
//
//                ArrayList<Video> watchLater, history;
//                watchLater = MainPageController.watchLaterList;
//                history = MainPageController.historyList;
//                ArrayList<Pair<Client.DataClasses.Video, String>> premList = MainPageController.premiumVideoList;
//
//                HashMap<String, String> subscribedChannelMap = MainPageController.subscribedChannelMap;
//                HashMap<String, Channel> myChannelMap = MainPageController.myChannelMap;
//
//                System.out.println("WatchLater: "+watchLater);
//                System.out.println("Histroy: "+history);
//                System.out.println("My channels: "+myChannelMap);
//                System.out.println("Subscriptions: "+subscribedChannelMap );
//
//
//                File f = new File(System.getProperty("user.home") + "/starkhub/"+ Main.USERNAME +"/watchLater/list");
//                if(!f.exists())
//                    f.createNewFile();
//                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
//                oos.writeObject(watchLater);
//                oos.close();
//                System.out.println("Object written");
//                System.out.println("Add to watch later ArrayList Written");
//
//                f = new File(System.getProperty("user.home") + "/starkhub/"+ Main.USERNAME +"/history/list");
//                if(!f.exists()) {
//                    f.createNewFile();
//                }
//                ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(f));
//                os.writeObject(history);
//                os.close();
//                System.out.println("Object written");
//                System.out.println("History  ArrayList Written");
//
//                f = new File(System.getProperty("user.home") + "/starkhub/"+ Main.USERNAME +"/mychannels/list");
//                if(!f.exists()) {
//                    f.createNewFile();
//                }
//                os = new ObjectOutputStream(new FileOutputStream(f));
//                os.writeObject(myChannelMap);
//                os.close();
//                System.out.println("Object written");
//                System.out.println("MyChannelMap Written");
//
//                f = new File(System.getProperty("user.home") + "/starkhub/"+ Main.USERNAME +"/subscriptions/list");
//                if(!f.exists()) {
//                    f.createNewFile();
//                }
//                os = new ObjectOutputStream(new FileOutputStream(f));
//                os.writeObject(subscribedChannelMap);
//                os.close();
//                System.out.println("Object written");
//                System.out.println("SubscribedChannelsMap Written");
//
//
//                f = new File(System.getProperty("user.home") + "/starkhub/"+ Main.USERNAME +"/prem/list");
//                if(!f.exists()) {
//                    f.createNewFile();
//                }
//                os = new ObjectOutputStream(new FileOutputStream(f));
//                os.writeObject(premList);
//                os.close();
//                System.out.println("Object written");
//                System.out.println("PremiumVideoList Written");
//
//
//                serverThread.interrupt();

            }catch(Exception ex){
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }
        });

    }

    void startStudentMainPage(){
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("layouts/mainStudentPage.fxml"));
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            Stage curStage = (Stage) rootPane.getScene().getWindow();

            curStage.setScene(new Scene(root));

            curStage.setOnCloseRequest(e -> {
                // TODO: Actions to be taken upon closing of application
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void startTeacherMainPage(){
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("layouts/mainTeacherPage.fxml"));
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            Stage curStage = (Stage) rootPane.getScene().getWindow();

            curStage.setScene(new Scene(root));

            curStage.setOnCloseRequest(e -> {
                // TODO: Actions to be taken upon closing of application
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    // Athenticating signUp from the HUB
    boolean authenticateSignUp(String username){
        boolean result = false;
        try {
            Socket serverConn = new Socket(Constants.SERVER_IP, Constants.SERVER_PORT);
            DataInputStream din = new DataInputStream(serverConn.getInputStream());
            DataOutputStream dout = new DataOutputStream(serverConn.getOutputStream());

            ObjectInputStream ois = new ObjectInputStream(serverConn.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(serverConn.getOutputStream());

            dout.writeBoolean(Flags.isTeacher);
            dout.writeUTF("#AUTH");
            dout.writeUTF(username);
            result = din.readBoolean();

            dout.close();
            din.close();
            serverConn.close();

        }catch( Exception e){
            e.printStackTrace();
        }
        return result;
    }


    // POPUP: for invalid username
    JFXPopup initInvalidUsernamePopup(){
        Label l = new Label("Username already taken..!!");
        l.setStyle("-fx-foreground-color:#ff0000");
        VBox vbox = new VBox(l);
        vbox.setPadding(new Insets(10));
        JFXPopup p = new JFXPopup(vbox);
        return p;
    }


    // POPUP: for invalid empty field
    JFXPopup initEmptyPopup(){
        Label l = new Label("This is a required Feild\nand Cannot be empty");
        l.setWrapText(true);
        VBox vbox = new VBox(l);
        vbox.setPadding(new Insets(10));
        JFXPopup p = new JFXPopup(vbox);
        return p;
    }


    // Prevalidation before connecting to Server
    boolean preValidation(String name, String userName, String pass){
        if(name == null || name.isEmpty()){
            emptyPopup.show(signUpName, JFXPopup.PopupVPosition.TOP , JFXPopup.PopupHPosition.LEFT);
           return false;
        }if(userName == null || userName.isEmpty()){
            emptyPopup.show(signUpUsername, JFXPopup.PopupVPosition.TOP , JFXPopup.PopupHPosition.LEFT);
            return false;
        }if(pass == null || pass.isEmpty()){
            emptyPopup.show(signUpPass, JFXPopup.PopupVPosition.TOP , JFXPopup.PopupHPosition.LEFT);
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


    private void writeCredentialsFile(String userName, String pass, String name, String path)  {
        try {
            String hashedPassword = getSHA256(pass);
            PrintWriter printWriter = new PrintWriter(new FileWriter(new File(path)));
            printWriter.println(userName);
            printWriter.println(hashedPassword);
            printWriter.println(name);

            printWriter.close();

            System.out.println("Credentials file Written");

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String getSHA256(String text) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(text.getBytes());

        byte[] byteData = messageDigest.digest();

        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            stringBuffer.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return stringBuffer.toString();
    }


    private void makeDirectory(String path)throws Exception{
        new File(path).mkdirs();
    }



    private boolean readFromCredentialsFile(String userName, String pass, String path) throws Exception{
        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(path)));
        String readUserName = bufferedReader.readLine();
        String readPass = bufferedReader.readLine();

        String hashedPass = getSHA256(pass);

        if(readUserName.equals(userName) && readPass.equals(hashedPass)){
            return true;
        }else return false;
    }

}

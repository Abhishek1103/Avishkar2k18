package ui;

import constants.Constants;
import constants.Flags;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class DashboardForTeacher implements Initializable
{

    @FXML
    Label ratingLabel;
    @FXML
    Button buttonOne, buttonTwo, buttonThree, buttonFour;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try{
            Socket socket = new Socket(Constants.SERVER_IP,7001);
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            //Get rank
            dos.writeBoolean(Flags.isTeacher);
            dos.writeUTF("#GETRATING");
            dos.writeUTF(Constants.USERNAME);
            double rating = dis.readDouble();
            ratingLabel.setText(""+rating);
            //button par quiz ke name laga dena and button press par results aa jai ge teacher ke pass
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void buttonOneClicked() {
        try{
            Socket socket = new Socket(Constants.SERVER_IP,7001);
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            //Get rank
            dos.writeBoolean(Flags.isTeacher);
            dos.writeUTF("#FETCHRESULT");
            dos.writeUTF(Constants.USERNAME);
            dos.writeUTF(buttonOne.getText().trim()); // could get by doing buttonOne.getText();

            dis.readBoolean();

            HashMap<String, Integer> resultMap = (HashMap<String, Integer>) ois.readObject();
            ShowResultsToTeacherFromDash.resMap = resultMap;
            show();
            /*
             * CODE TO DISPLAY THIS
             * */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buttonTwoClicked() {
        try{
            Socket socket = new Socket(Constants.SERVER_IP,7001);
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            //Get rank
            dos.writeBoolean(Flags.isTeacher);
            dos.writeUTF("#FETCHRESULT");
            dos.writeUTF(Constants.USERNAME);
            dos.writeUTF(buttonTwo.getText().trim()); // could get by doing buttonOne.getText();
            System.out.println("QuizName: "+buttonTwo.getText().trim());
            dis.readBoolean();

            HashMap<String, Integer> resultMap = (HashMap<String, Integer>) ois.readObject();
            ShowResultsToTeacherFromDash.resMap = resultMap;
            show();
            /*
             * CODE TO DISPLAY THIS
             * */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buttonThreeClicked() {
        try{
            Socket socket = new Socket(Constants.SERVER_IP,7001);
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            //Get rank
            dos.writeBoolean(Flags.isTeacher);
            dos.writeUTF("#FETCHRESULT");
            dos.writeUTF(Constants.USERNAME);
            dos.writeUTF(buttonThree.getText().trim()); // could get by doing buttonOne.getText();
            System.out.println("QuizName: "+buttonThree.getText().trim());
            dis.readBoolean();

            HashMap<String, Integer> resultMap = (HashMap<String, Integer>) ois.readObject();
            ShowResultsToTeacherFromDash.resMap = resultMap;
            show();
            /*
             * CODE TO DISPLAY THIS
             * */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buttonFourClicked() {
        try{
            Socket socket = new Socket(Constants.SERVER_IP,7001);
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            //Get rank
            dos.writeBoolean(Flags.isTeacher);
            dos.writeUTF("#FETCHRESULT");
            dos.writeUTF(Constants.USERNAME);
            dos.writeUTF(buttonFour.getText().trim()); // could get by doing buttonOne.getText();
            System.out.println("QuizName: "+buttonFour.getText().trim());
            dis.readBoolean();

            HashMap<String, Integer> resultMap = (HashMap<String, Integer>) ois.readObject();
            ShowResultsToTeacherFromDash.resMap = resultMap;
            show();

            /*
             * CODE TO DISPLAY THIS
             * */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void show() throws Exception{
        Stage playWindow = new Stage();
        playWindow.initModality(Modality.APPLICATION_MODAL);
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("layouts/showResultsToTeacherFromDash.fxml"));
        Scene sc = new Scene(root);
        playWindow.setScene(sc);
        playWindow.showAndWait();
    }
}

//package ui;
//
//import constants.Constants;
//import constants.Flags;
//import javafx.fxml.FXML;
//import javafx.fxml.Initializable;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.net.Socket;
//import java.net.URL;
//import java.util.ResourceBundle;
//
//public class DashboardForStudent implements Initializable
//{
//
//    @FXML
//    Label rankingLabel;
//    @FXML
//    Button buttonOne, buttonTwo, buttonThree, buttonFour;
//
//    @Override
//    public void initialize(URL location, ResourceBundle resources) {
//        try {
//            Socket socket = new Socket(Constants.SERVER_IP, 7001);
//            try {
//                DataInputStream dis = new DataInputStream(socket.getInputStream());
//                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
//                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
//                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
//                //Get rank
//                dos.writeBoolean(!Flags.isStudent);
//                dos.writeUTF("#GETRANK");
//                dos.writeUTF(Constants.USERNAME);
//                dos.writeUTF(teacher name);
//                dos.writeUTF(quiz name);
//                boolean rankExist = dis.readBoolean();
//                if (rankExist) {
//                    //set the rankLabel
//                    int rank = dis.readInt();
//                    rankingLabel.setText("" + rank);
//                } else {
//                    //set label to no rank
//                    rankingLabel.setText("NA");
//                }
//
//                //button par quiz ki ranks thi but it can change
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//}

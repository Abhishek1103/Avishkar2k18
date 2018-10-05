package utility;

import com.jfoenix.controls.JFXListView;
import constants.Constants;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import ui.ShowResultsToTeacherController;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class GetResultsTeacherService extends Service {

    JFXListView<AnchorPane> listView;
    String quizName;

    public GetResultsTeacherService(JFXListView<AnchorPane> listView, String quizName){
        this.listView = listView;
        this.quizName = quizName;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                try{

                    Socket socket = new Socket(Constants.SERVER_IP, Constants.SERVER_PORT);
                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                    DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
                    ObjectInputStream ois= new ObjectInputStream(socket.getInputStream());
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

                    dout.writeBoolean(true);
                    dout.writeUTF("#FETCHRESULTS");
                    System.out.println("Sent FETCHRESULTS");
                    dout.writeUTF(Constants.USERNAME);
                    dout.writeUTF(quizName);
                    boolean b = dis.readBoolean();
                    HashMap<String, Integer> map = (HashMap<String, Integer>)ois.readObject();
                    dout.writeUTF(Constants.USERNAME);
                    double rating = dis.readDouble();
                    System.out.println("Received Rating: "+rating);
                    ShowResultsToTeacherController.rating = rating;
                    System.out.println(map);
                    ShowResultsToTeacherController.res = map;
//                    for(Map.Entry<String, Integer> ent:map.entrySet()){
//                        AnchorPane pane = FXMLLoader.load(getClass().getClassLoader().getResource("layouts/ResultItemLayout.fxml"));
//                        Label namel = (Label)(pane.getChildren().get(0));
//                        Label marksl = (Label)(pane.getChildren().get(1));
//                        namel.setText(ent.getKey());
//                        marksl.setText("Total Marks: "+ent.getValue());
//                        Platform.runLater(()-> {
//                            listView.getItems().add(pane);
//                        });
//
//                    }

                    ois.close();
                    oos.close();
                    socket.close();

                }catch (Exception ex){
                    ex.printStackTrace();
                }

                return null;
            }
        };
    }
}

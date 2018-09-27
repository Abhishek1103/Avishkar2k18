package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import jdk.nashorn.internal.runtime.ECMAException;
import login.Main;
import org.omg.PortableInterceptor.INACTIVE;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ShowResultsToTeacherFromDash implements Initializable {

    @FXML
    ScrollPane scrollPane;

    public static HashMap<String, Integer> resMap;

    VBox vBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        vBox = new VBox(10);
        vBox.setPadding(new Insets(15));

        try{
            init();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void init(){


        HashMap<String, Integer> map = resMap;
        for(Map.Entry<String, Integer> entry: map.entrySet()){
            try{

                String studName = entry.getKey();
                int marks = entry.getValue();

                AnchorPane pane = FXMLLoader.load(getClass().getClassLoader().getResource("layouts/ResultItemLayout.fxml"));
                Label namelbl = (Label)(pane.getChildren().get(0));
                namelbl.setText(studName);
                Label markLbl = (Label)(pane.getChildren().get(1));
                markLbl.setText("Total Marks: "+marks);
                vBox.getChildren().add(pane);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        scrollPane.setContent(vBox);
    }
}



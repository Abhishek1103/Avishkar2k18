package ui;

import com.jfoenix.controls.JFXListView;
import constants.Constants;
import data.Quiz;
import data.Subject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import utility.GetResultsTeacherService;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ShowResultsToTeacherController implements Initializable {

    @FXML
    ScrollPane scrollPane;

    HashMap<String, Subject> map;
    public static HashMap<String, Integer> res;
    public static double rating;

    VBox v;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        map = Constants.SUBJECT_MAP;
        v = new VBox(10);
        v.setPadding(new Insets(15));
        init();
    }


    private void init(){

        for(Map.Entry<String, Subject> entry: map.entrySet()){
            Subject s = entry.getValue();
            HashMap<String, Quiz> m = s.getQuizHashMap();

            for(Map.Entry<String, Quiz> en: m.entrySet()){
                String name = en.getKey();
                System.out.println("QuizName: "+name);
                Label l = new Label(name);
                l.setFont(Font.font(18));
                Label ll = new Label("Rating: "+rating);
                ll.setFont(Font.font(15));
                JFXListView<AnchorPane> listView = new JFXListView<>();
                listView.setPrefWidth(850);
                listView.setPrefHeight(200);
                System.out.println("Get Results Teacher Service started");
                GetResultsTeacherService serv = new GetResultsTeacherService(listView, name);
                serv.start();
                serv.setOnSucceeded(e -> {
                    try {
                        for (Map.Entry<String, Integer> ent : res.entrySet()) {
                            AnchorPane pane = FXMLLoader.load(getClass().getClassLoader().getResource("layouts/ResultItemLayout.fxml"));
                            Label namel = (Label) (pane.getChildren().get(0));
                            Label marksl = (Label) (pane.getChildren().get(1));
                            namel.setText(ent.getKey());
                            marksl.setText("Total Marks: " + ent.getValue());
                            Platform.runLater(() -> {
                                listView.getItems().add(pane);
                                ll.setText("Rating: "+rating);
                            });

                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                });
                VBox vBox = new VBox();
                vBox.getChildren().addAll(l,ll,listView);
                v.getChildren().add(vBox);
            }
        }
        scrollPane.setContent(v);
    }

}

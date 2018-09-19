package Client.UI;

import Client.DataClasses.Channel;
import Client.DataClasses.Video;
import Client.Utility.SendData;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class PremiumPageController implements Initializable {

    @FXML
    JFXListView<Label> allVideosListView;

    @FXML
    JFXListView<AnchorPane> selectedVideoListView;

    @FXML
    JFXButton makePremiumButton, addButton;

    ArrayList<Pair<Video, String> > premList, list;
    HashMap<String,Channel> map;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try{
            allVideosListView.setExpanded(true);
            allVideosListView.setPadding(new Insets(10));
            allVideosListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

            selectedVideoListView.setExpanded(true);
            selectedVideoListView.setPadding(new Insets(10));
            selectedVideoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);


             map = MainPageController.myChannelMap;
             premList = new ArrayList<>();
             list = new ArrayList<>();

            for(Map.Entry<String, Channel> entry: map.entrySet()){
                Channel c = entry.getValue();
                ArrayList<Video> vl = c.getVideoList();
                for(Video v: vl){
                    list.add(new Pair<>(v, c.getChannelName()));

                    Label l = new Label();
                    l.setText(c.getChannelName()+": "+v.getVideoName());
                    allVideosListView.getItems().add(l);
                }
            }


        }catch (Exception e){
            e.printStackTrace();
        }




    }


    AnchorPane createSelectedItem(String string) throws Exception{

        AnchorPane pane = FXMLLoader.load(getClass().getResource("../Layouts/selectedVideoPremLayout.fxml"));
        JFXButton btn = (JFXButton)(pane.getChildren().get(0));
        Label l = (Label)(pane.getChildren().get(1));

        l.setText(string);

        btn.setOnAction(e -> {
            String s = ((Label)(pane.getChildren().get(1))).getText();
            allVideosListView.getItems().add(new Label(s));
            selectedVideoListView.getItems().remove(pane);
        });

        return pane;
    }


    public void addButtonClicked(){
        ObservableList<Label> ll = allVideosListView.getSelectionModel().getSelectedItems();

        for(Label l: ll){
            try {
                selectedVideoListView.getItems().add(createSelectedItem(l.getText()));
            }catch (Exception e){
                e.printStackTrace();
            }

            allVideosListView.getItems().remove(l);
        }
    }


    public void makePremiumButtonClicked(){
        ObservableList<AnchorPane> list = selectedVideoListView.getSelectionModel().getSelectedItems();

        for(AnchorPane p: list) {

            String s = ((Label) (p.getChildren().get(1))).getText();
            String channelName = s.substring(0,s.indexOf(':'));
            String vidName = s.substring(s.indexOf(':')+1).trim();

            Channel c = map.get(channelName);

            ArrayList<Video> l = c.getVideoList();
            for(Video v: l){
                if(v.getVideoName().equals(vidName)){
                    premList.add(new Pair<>(v, channelName));
                    break;
                }
            }
        }

        MainPageController.premiumVideoList = premList;

        SendData sd = new SendData();
        sd.start();

        sd.setOnSucceeded(e -> {
            System.out.println("Premeium list successfully sent");
        });

    }

}

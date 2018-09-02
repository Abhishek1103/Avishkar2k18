package Client.UI;

import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class ShowHistoryController implements Initializable {

    // TODO: Could be used both for History and Trending

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        if(MainPageController.IS_HISTORY){
            // TODO: History stuff
        }
        else if(MainPageController.IS_TRENDING){
            // TODO: Trending stuff
        }

    }
}

package Test.autoCompleteTextFeild;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import org.controlsfx.control.textfield.TextFields;

import java.net.URL;
import java.util.ResourceBundle;

public class AutoCompController implements Initializable {


    @FXML
    TextField txtFeild;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        String[] possibleOptions = {"Hello", "Hi", "Hell", "Lion", "Leo","Abhishek", "Abhinav", "Abhilasha"};

        TextFields.bindAutoCompletion(txtFeild, possibleOptions);
    }
}

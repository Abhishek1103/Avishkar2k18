package Client.UI;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPopup;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class MainPageController implements Initializable {


    public static int SEARCH_CRITERIA = 0;


    @FXML
    JFXButton btn;
    @FXML
    AnchorPane contentAnchorPane, menuBtnAnchor, filterAnchorPane;
    @FXML
    ImageView menuButton;

    JFXPopup popup;
    JFXPopup filterPopup;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            AnchorPane pane  = FXMLLoader.load(getClass().getResource("../Layouts/clientPage.fxml"));
            contentAnchorPane.getChildren().setAll(pane);
        } catch (Exception e) {
            e.printStackTrace();
        }


        popup = initPopup();
        filterPopup = initFilterPopup();

    }


    // Create JFXButton
    protected JFXButton createButton(String text){
        JFXButton button = new JFXButton(text);

        button.setButtonType(JFXButton.ButtonType.RAISED);
        button.setPrefWidth(150);
        button.setPadding(new Insets(10));

        button.setOnAction(e -> testMethod() );

        return button;
    }



    // Initialize Popup
    protected JFXPopup initPopup(){
        VBox vbox = new VBox(10);
        vbox.getChildren().add(createDashboardButton("Dashboard"));
        vbox.getChildren().add(createCreatorDashboardButton("Creator Dashboard"));
        vbox.getChildren().add(createMyCHannelsButton("My Channels"));
        vbox.getChildren().add(createLogOutButton("Log Out"));
        JFXPopup popup = new JFXPopup(vbox);
        return popup;
    }



    // Initialize Filter Popup
    protected JFXPopup initFilterPopup(){
        VBox vbox = new VBox(10);
        vbox.getChildren().add(createDefaultSearchButton("Default"));
        vbox.getChildren().add(createChannelSearchButton("Channel"));
        vbox.getChildren().add(createVideoSearchButton("Video"));
        vbox.getChildren().add(createTagSearchButton("Tags"));
        JFXPopup popup = new JFXPopup(vbox);
        return popup;
    }



    // Show Popup
    public void openMenu(MouseEvent e){
        popup.show(menuBtnAnchor, JFXPopup.PopupVPosition.TOP,JFXPopup.PopupHPosition.LEFT, e.getX(), e.getY());
    }

    // Show filter Popup
    public void openFilterMenu(){
        filterPopup.show(filterAnchorPane,JFXPopup.PopupVPosition.TOP,JFXPopup.PopupHPosition.LEFT);
    }


    // Test method
    void testMethod(){
        System.out.println("On action working");
    }



    // Creating DashBoard Button
    protected JFXButton createDashboardButton(String text){
        text = "Dashboard";
        JFXButton button = new JFXButton(text);

        button.setButtonType(JFXButton.ButtonType.RAISED);
        button.setPrefWidth(150);
        button.setPadding(new Insets(10));

        button.setOnAction(e -> dashBoardButtonClicked() );

        return button;
    }



    // Creating Creator Dashboard Button
    protected JFXButton createCreatorDashboardButton(String text){
        text = "Creator Dashboard";
        JFXButton button = new JFXButton(text);

        button.setButtonType(JFXButton.ButtonType.RAISED);
        button.setPrefWidth(150);
        button.setPadding(new Insets(10));

        button.setOnAction(e -> creatorDashBoardButtonClicked() );

        return button;
    }



    // Creating MyCHannels Button
    protected JFXButton createMyCHannelsButton(String text){
        text = "My Channels";
        JFXButton button = new JFXButton(text);

        button.setButtonType(JFXButton.ButtonType.RAISED);
        button.setPrefWidth(150);
        button.setPadding(new Insets(10));

        button.setOnAction(e -> myChannelsButtonClicked() );

        return button;
    }




    // Creating LogOut Button
    protected JFXButton createLogOutButton(String text){
        text = "Log Out";
        JFXButton button = new JFXButton(text);

        button.setButtonType(JFXButton.ButtonType.RAISED);
        button.setPrefWidth(150);
        button.setPadding(new Insets(10));

        button.setOnAction(e -> logOutButtonClicked() );

        return button;
    }



    // Creating Default Search Button
    protected JFXButton createDefaultSearchButton(String text){
        JFXButton button = new JFXButton(text);

        button.setButtonType(JFXButton.ButtonType.RAISED);
        button.setPrefWidth(150);
        button.setPadding(new Insets(10));

        button.setOnAction(e -> {
            SEARCH_CRITERIA = 0;
        } );

        return button;
    }


    // Creating ChannelSearch Button
    protected JFXButton createChannelSearchButton(String text){
        JFXButton button = new JFXButton(text);

        button.setButtonType(JFXButton.ButtonType.RAISED);
        button.setPrefWidth(150);
        button.setPadding(new Insets(10));

        button.setOnAction(e -> {
            SEARCH_CRITERIA = 1;
        } );

        return button;
    }


    // Creating VideoSearch Button
    protected JFXButton createVideoSearchButton(String text){
        JFXButton button = new JFXButton(text);

        button.setButtonType(JFXButton.ButtonType.RAISED);
        button.setPrefWidth(150);
        button.setPadding(new Insets(10));

        button.setOnAction(e -> {
            SEARCH_CRITERIA = 2;
        } );

        return button;
    }



    // Creating VideoSearch Button
    protected JFXButton createTagSearchButton(String text){
        JFXButton button = new JFXButton(text);

        button.setButtonType(JFXButton.ButtonType.RAISED);
        button.setPrefWidth(150);
        button.setPadding(new Insets(10));

        button.setOnAction(e -> {
            SEARCH_CRITERIA = 3;
        } );

        return button;
    }


    public void addChannel(){
        try {
            AnchorPane pane = FXMLLoader.load(getClass().getResource("../Layouts/addChannelLayout.fxml"));
            contentAnchorPane.getChildren().setAll(pane);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void addVideo(){

    }

    public void homeButtonClicked(){

    }

    public void trendingButtonClicked(){

    }

    public void historyButtonClicked(){

    }

    public void subscriptionsButtonClicked(){

    }

    public void playlistButtonClicked(){

    }

    public void watchLaterButtonClicked(){

    }

    void logOutButtonClicked(){
        System.out.println("Logout Clicked");
    }

    void myChannelsButtonClicked(){
        System.out.println("My Channels clicked");
    }

    void dashBoardButtonClicked(){
        System.out.println("Dashboard Clicked");
    }

    void creatorDashBoardButtonClicked(){
        System.out.println("Creator Dashboard clicked");
    }

}

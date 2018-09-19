package Client.UI;

import Client.DataClasses.Channel;
import Client.Login.LayoutController;
import Client.Login.Main;
import Client.Utility.InitializeListsAndMapsService;
import Client.Utility.NotificationServiceAtLogin;
import Client.Utility.NotificationServiceRealTime;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPopup;
import hubFramework.Video;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class MainPageController implements Initializable {


    public static int SEARCH_CRITERIA = 0;
    public static boolean IS_HISTORY = false;
    public static boolean IS_TRENDING = false;

    @FXML
    JFXButton btn, notificationButton, addChannelButton, addVideoButton;
    @FXML
    AnchorPane contentAnchorPane, menuBtnAnchor, filterAnchorPane, rootAnchorPane;
    @FXML
    ImageView menuButton;
    @FXML
    Circle notificationCircle;

    JFXPopup popup;
    JFXPopup filterPopup;

    public static JFXPopup notificationPopup;
    public static HashMap<String, Video> notificationMap;
    public static VBox notificationVbox;
    public static ArrayList<Video> watchLaterList, historyList;
    public static HashMap<String, String> subscribedChannelMap;
    public static HashMap<String,Channel> myChannelMap;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        setToolTipTexts();

        InitializeListsAndMapsService initializeListsAndMapsService = new InitializeListsAndMapsService();
        initializeListsAndMapsService.start();

        watchLaterList = new ArrayList<>();
        subscribedChannelMap = new HashMap<>();
        myChannelMap = new HashMap<>();
        notificationPopup = initNotificationPopup();
        // TODO: Start the notification Services
        NotificationServiceAtLogin notificationServiceAtLogin = new NotificationServiceAtLogin(notificationCircle);
        notificationServiceAtLogin.start();

        NotificationServiceRealTime notificationServiceRealTime = new NotificationServiceRealTime(notificationCircle);
        notificationServiceRealTime.start();

        try {
            AnchorPane pane  = FXMLLoader.load(getClass().getResource("../Layouts/clientPage.fxml"));
            contentAnchorPane.getChildren().setAll(pane);
        } catch (Exception e) {
            e.printStackTrace();
        }

        notificationMap = new HashMap<>();

        popup = initPopup();
        filterPopup = initFilterPopup();
        notificationPopup = initNotificationPopup();

        LayoutController.isNotifReady = true;
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


    // Initialize NotificationPopup
    protected JFXPopup initNotificationPopup(){
        notificationVbox = new VBox(2);
        Label l = new Label("Notifications");
        notificationVbox.getChildren().add(l);
        JFXPopup popup = new JFXPopup(notificationVbox);
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
        try {
            AnchorPane pane = FXMLLoader.load(getClass().getResource("../Layouts/addVideoLayout.fxml"));
            contentAnchorPane.getChildren().setAll(pane);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void homeButtonClicked(){

        try {
            AnchorPane pane  = FXMLLoader.load(getClass().getResource("../Layouts/clientPage.fxml"));
            contentAnchorPane.getChildren().setAll(pane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void trendingButtonClicked(){
        IS_HISTORY = false;
        IS_TRENDING = true;
        try {
            AnchorPane pane = FXMLLoader.load(getClass().getResource("../Layouts/showHistoryPage.fxml"));
            contentAnchorPane.getChildren().setAll(pane);
        }catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void historyButtonClicked(){
        IS_HISTORY = true;
        IS_TRENDING = false;
        try {
            AnchorPane pane = FXMLLoader.load(getClass().getResource("../Layouts/showHistoryPage.fxml"));
            contentAnchorPane.getChildren().setAll(pane);
        }catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void subscriptionsButtonClicked(){
        System.out.println("Subscriptions Button Clicked");
        try{
            AnchorPane pane = FXMLLoader.load(getClass().getResource("../Layouts/showSubscriptionsPage.fxml"));
            contentAnchorPane.getChildren().setAll(pane);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void playlistButtonClicked(){

    }

    public void watchLaterButtonClicked(){
        try{
            AnchorPane pane = FXMLLoader.load(getClass().getResource("../Layouts/showWatchLaterPage.fxml"));
            contentAnchorPane.getChildren().setAll(pane);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void notificationButtonPressed(){
        //TODO: The line below throws Error
        notificationCircle.setOpacity(0.0);
        notificationPopup.show(notificationButton,JFXPopup.PopupVPosition.TOP,JFXPopup.PopupHPosition.LEFT);
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


    public void setToolTipTexts(){

        Tooltip tt = new Tooltip();
        tt.setText("Create a new Channel");
        tt.setStyle("-fx-font: normal bold 11 Langdon; "
                + "-fx-base: #AE3522; "
                + "-fx-text-fill: orange;");

        addChannelButton.setTooltip(tt);

        Tooltip t = new Tooltip();
        t.setStyle("-fx-font: normal bold 11 Langdon; "
                + "-fx-base: #AE3522; "
                + "-fx-text-fill: orange;");
        t.setText("Add Video to a Channel");
        addVideoButton.setTooltip(t);
    }



}

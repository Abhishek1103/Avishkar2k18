package timerthreadtest;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class StartTimerTest extends Application{
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("timerTestLayout.fxml"));
        //primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("Squiz");
        Scene scene = new Scene(root);

        final double[] xoffset = new double[1];  // This is because variables used in lambda expression should
        final double[] yoffset = new double[1];  // final or effectively final. And IDE suggested this method

        root.setOnMousePressed(e -> {
            xoffset[0] = e.getSceneX();
            yoffset[0] = e.getSceneY();
        });

        root.setOnMouseDragged(e -> {
            primaryStage.setX(e.getScreenX() - xoffset[0]);
            primaryStage.setY(e.getScreenY() - yoffset[0]);
        });


        scene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}



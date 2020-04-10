package sample;

import Controllers.ControllerCS;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../View/sample.fxml"));
        primaryStage.setTitle("My app for client-server");
        primaryStage.setScene(new Scene(root, 600, 505));
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                ControllerCS.closeWindow();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}

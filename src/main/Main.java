package main;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        View v = new View(primaryStage);
        Controller c = new Controller(v);
        v.setController(c);
        c.start();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

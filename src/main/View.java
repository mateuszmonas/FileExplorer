package main;

import com.sun.istack.internal.NotNull;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.File;

public class View{

    private GridPane pane;
    private Stage primaryStage;
    private Controller controller;

    public View(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void start(){
        pane = new GridPane();
        pane.setGridLinesVisible(true);
        primaryStage.setTitle("FileNameEditor");
        primaryStage.setScene(new Scene(pane, 300, 275));
        primaryStage.show();
    }

    void changeDirectory(String path){
        controller.setPath(path);
    }

    public void showFiles(@NotNull File[] files){
        pane.getChildren().clear();
        int i = 0;
        for (File file : files) {
            Label label = new Label();
            label.setText(file.getName());
            label.setOnMouseClicked(event -> {
                if(file.isDirectory()) changeDirectory(file.getPath());
            });
            pane.add(label,0, i);
            i++;
        }
    }

}

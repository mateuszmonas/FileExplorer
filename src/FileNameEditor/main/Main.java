package FileNameEditor.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application{

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../layout/fxml_layout.fxml"));
        ViewContract.Model model = new Model();
        ViewContract.Controller controller = new Controller(model);
        loader.setController(controller);
        stage.setTitle("FileNameEditor");
        stage.setScene(new Scene(loader.load(), 690, 490));
        stage.setResizable(false);
        stage.show();
        model.start(controller);
        System.out.println(System.getProperty("os.name"));
    }

    public static void main(String[] args) {
        launch(args);
    }
}


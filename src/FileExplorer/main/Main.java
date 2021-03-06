package FileExplorer.main;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../layout/fxml_layout.fxml"));
        ViewContract.Model model = new Model();
        ViewContract.Controller controller = new Controller(model);
        loader.setController(controller);
        stage.setTitle("FileExplorer");
        stage.setScene(new Scene(loader.load(), 690, 490));
        stage.setResizable(false);
        stage.show();
        model.start(controller, new DialogHelperImpl());
    }
}


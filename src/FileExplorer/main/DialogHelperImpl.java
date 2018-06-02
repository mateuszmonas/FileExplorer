package FileExplorer.main;


import javafx.scene.control.Alert;

class DialogHelperImpl implements DialogHelper {

    @Override
    public void fileAlreadyExistsDialog() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Something went wrong");
        alert.setHeaderText(null);
        alert.setContentText("File with the same name already exists");
        alert.showAndWait();
    }

    @Override
    public void fileWasNotCreatedDialog() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Something went wrong");
        alert.setHeaderText(null);
        alert.setContentText("File was not created");
        alert.showAndWait();
    }

    @Override
    public void fineCouldNotBeMovedToTrashDialog() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Something went wrong");
        alert.setHeaderText(null);
        alert.setContentText("File could not be moved to trash");
        alert.showAndWait();
    }

    @Override
    public void fileCouldNotBeDeletedDialog() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Something went wrong");
        alert.setHeaderText(null);
        alert.setContentText("File could not be deleted");
        alert.showAndWait();
    }

    @Override
    public void destinationDirectoryDoesNotExistDialog() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Something went wrong");
        alert.setHeaderText(null);
        alert.setContentText("Destination directory does not exist");
        alert.showAndWait();
    }

    @Override
    public void somethingWentWrongDialog() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Something went wrong");
        alert.setHeaderText(null);
        alert.setContentText("Something went wrong");
        alert.showAndWait();
    }

}

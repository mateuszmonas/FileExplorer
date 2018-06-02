package FileExplorer.main;


public interface DialogHelper {
    void fileAlreadyExistsDialog();

    void fileWasNotCreatedDialog();

    void fineCouldNotBeMovedToTrashDialog();

    void fileCouldNotBeDeletedDialog();

    void destinationDirectoryDoesNotExistDialog();

    void somethingWentWrongDialog();
}

package FileExplorer.main;

import FileExplorer.nodes.FileNodeSelectable;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import FileExplorer.nodes.FileLabelSelectable;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Controller implements Initializable, ViewContract.Controller {

    @FXML private ScrollPane scrollPaneA;
    @FXML private ScrollPane scrollPaneB;
    private ViewContract.Model model;
    @FXML private VBox fileListA;
    @FXML private VBox fileListB;
    @FXML private TextField filePathA;
    @FXML private TextField filePathB;
    @FXML private Button goToParentButtonA;
    @FXML private Button goToParentButtonB;
    @FXML private Button copyFilesButtonA;
    @FXML private Button copyFilesButtonB;
    @FXML private Pane drawingPane;
    private TextField[] filePaths = new TextField[2];
    private VBox[] fileLists= new VBox[2];

    Controller(ViewContract.Model model) {
        this.model = model;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileLists[0] = fileListA;
        fileLists[1] = fileListB;
        filePaths[0]= filePathA;
        filePaths[1]= filePathB;
        //on my distro the window is shifted to the left a bit and hides the names of the files
        //so we add padding
        if(System.getProperty("os.name").equals("Linux")){
            fileListA.setPadding(new Insets(0,0,0,5));
        }
        MouseEventsHelper helper = new MouseEventsHelper(drawingPane, fileLists, fileEventHelper);
        helper.handleSelectionRectangle(0);
        helper.handleSelectionRectangle(1);
        helper.handleContextMenu(0);
        helper.handleContextMenu(1);
        handleKeyEvents(scrollPaneA, 0);
        handleKeyEvents(scrollPaneB, 1);
        filePathA.setOnKeyPressed(event -> { if(event.getCode()==KeyCode.ENTER){
            directoryClickedTwice(filePaths[0].getText(), 0);
            fileLists[0].requestFocus();
        } });
        filePathB.setOnKeyPressed(event -> { if(event.getCode()==KeyCode.ENTER) {
            directoryClickedTwice(filePaths[1].getText(), 1);
            fileLists[1].requestFocus();
        } });
        copyFilesButtonA.setOnMouseClicked(event -> fileEventHelper.copyFilesToClipboardEvent(0));
        copyFilesButtonB.setOnMouseClicked(event -> fileEventHelper.copyFilesToClipboardEvent(1));
        goToParentButtonA.setOnMouseClicked(event -> goToParentDirectory(0));
        goToParentButtonB.setOnMouseClicked(event -> goToParentDirectory(1));
    }

    private void goToParentDirectory(int whichList){
        model.goToParentDirectory(whichList);
    }

    private void handleKeyEvents(Control pane, int whichList){
        pane.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN).match(event)) {
                fileEventHelper.copyFilesToClipboardEvent(whichList);
            }
            else if (new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN).match(event)) {
                fileEventHelper.pasteFilesFromClipboardEvent(whichList);
            }
            else if (new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN).match(event)) {
                fileEventHelper.cutFilesEvent(whichList);
            }
            else if (new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN).match(event)) {
                fileLists[whichList].getChildrenUnmodifiable().forEach(node -> {
                    if(node instanceof FileLabelSelectable) ((FileLabelSelectable) node).setSelected(true);
                });
            }
            else if (new KeyCodeCombination(KeyCode.DELETE, KeyCombination.SHIFT_DOWN).match(event)){
                fileEventHelper.deleteFilesEvent(whichList);
            }
            else if (event.getCode()==KeyCode.DELETE){
                fileEventHelper.moveFilesToTrash(whichList);
            }
        });
    }

    private FileEventHelper fileEventHelper = new FileEventHelper() {
        @Override
        public void deleteFilesEvent(int whichList) {
            model.deleteFiles(fileLists[whichList].getChildrenUnmodifiable().stream()
                    .filter(file -> file instanceof FileLabelSelectable && ((FileLabelSelectable) file).isSelected())
                    .map(file -> ((FileLabelSelectable) file).getFile()).collect(Collectors.toList()));
        }

        @Override
        public void moveFilesToTrash(int whichList) {
            model.moveFilesToTrash(
                    fileLists[whichList].getChildrenUnmodifiable().stream()
                            .filter(file -> file instanceof FileLabelSelectable && ((FileLabelSelectable) file).isSelected())
                            .map(file -> ((FileLabelSelectable) file).getFile()).collect(Collectors.toList()));
        }

        @Override
        public void pasteFilesFromClipboardEvent(int whichList) {
            model.pasteFilesFromClipboard(whichList);
        }

        @Override
        public void cutFilesEvent(int whichList) {
            List<File> files = fileLists[whichList].getChildrenUnmodifiable().stream().filter(node ->
                    node instanceof FileLabelSelectable && ((FileLabelSelectable) node).isSelected()
            ).map(node -> ((FileLabelSelectable)node).getFile()
            ).collect(Collectors.toList());
            model.cutFiles(files);
        }

        @Override
        public void moveFilesEvent(List<File> files, String path) {
            model.moveFiles(files, path);
        }

        @Override
        public void moveFilesEvent(List<File> files, int whichList) {
            model.moveFiles(files, whichList);
        }

        @Override
        public void copyFilesToClipboardEvent(int whichList) {
            List<File> files = fileLists[whichList].getChildrenUnmodifiable().stream().filter(node ->
                    node instanceof FileLabelSelectable && ((FileLabelSelectable) node).isSelected()
            ).map(node -> ((FileLabelSelectable)node).getFile()
            ).collect(Collectors.toList());
            model.copyFilesToClipboard(files);
        }

        @Override
        public void createNewFile(int whichList) {
            //add new node to the list
            long l = fileLists[whichList].getChildrenUnmodifiable().stream()
                    .filter(node -> node instanceof FileNodeSelectable && ((FileNodeSelectable) node).getFile().getName().startsWith("new File"))
                    .count();
            TextField tf = new TextField("new File " + l);
            tf.setPadding(new Insets(0));
            tf.setOnKeyReleased(event -> {
                if(event.getCode()==KeyCode.ENTER){
                    model.createFile(tf.getText(), whichList);
                }
            });
            tf.focusedProperty().addListener((observableValue, oldPropertyValue, newPropertyValue) -> {
                if(!newPropertyValue){
                    //whe field goes out of focus without clicking enter we restore the old label
                    fileLists[whichList].getChildren().remove(tf);
                }
            });
            fileLists[whichList].getChildren().add(tf);
            tf.requestFocus();
            tf.selectAll();
        }

        @Override
        public void renameFile(FileNodeSelectable nodeToRename, int whichList) {
            TextField tf = new TextField(nodeToRename.getFile().getName());
            tf.setPadding(new Insets(0));
            tf.setOnKeyReleased(event -> {
                if(event.getCode()==KeyCode.ENTER){
                    model.renameFile(nodeToRename.getFile(), tf.getText());
                }
            });
            tf.focusedProperty().addListener((observableValue, oldPropertyValue, newPropertyValue) -> {
                if(!newPropertyValue){
                    //whe field goes out of focus without clicking enter we restore the old label
                    replaceNode(tf, (Node) nodeToRename, whichList);
                }
            });
            replaceNode(nodeToRename, tf, whichList);
            tf.requestFocus();
            tf.selectAll();
        }
    };

    private void replaceNode(Object nodeToReplace, Node nodeToReplaceWith, int whichList){
        ObservableList<Node> nodes = fileLists[whichList].getChildren();
        for(int i = 0;i<nodes.size();i++){
            if(nodes.get(i)==nodeToReplace){
                nodes.set(i, nodeToReplaceWith);
                break;
            }
        }
    }

    @Override
    public void displayPath(String path, int whichList){
        filePaths[whichList].setText(path);
    }

    private void directoryClickedTwice(String path, int whichList){
        model.enterDirectory(path, whichList);
    }

    private void viewFiles(File[] files, int whichList){
        for (File file : files) {
            FileLabelSelectable label = new FileLabelSelectable(file);
            //createLabelContextMenu(label, whichList);
            //class used to check if mouse position while released is same as while pressed
            final MousePosition pressedMousePosition = MousePosition.ZERO;
            label.addEventHandler(MouseEvent.MOUSE_PRESSED ,event -> pressedMousePosition.setPosition(event.getSceneX(), event.getSceneY()));
            label.addEventHandler(MouseEvent.MOUSE_CLICKED ,event -> {
                ObservableList<Node> nodes = label.getParent().getChildrenUnmodifiable();
                if(pressedMousePosition.equals(event.getSceneX(), event.getSceneY())) {
                    if (event.isShiftDown()) {
                        int clickedItemPosition = 0;
                        int firstSelectedItemPosition = 0;
                        int lastSelectedItemPosition = nodes.size() - 1;
                        //get clicked item position
                        for (Node n : nodes) {
                            if (label.equals(n)) {
                                break;
                            }
                            clickedItemPosition++;
                        }
                        //get first selected item position
                        for (Node n : nodes) {
                            if (n instanceof FileLabelSelectable && ((FileLabelSelectable) n).isSelected()) {
                                break;
                            }
                            firstSelectedItemPosition++;
                        }
                        //get last selected item position
                        while (lastSelectedItemPosition > firstSelectedItemPosition) {
                            if (nodes.get(lastSelectedItemPosition) instanceof FileLabelSelectable && ((FileLabelSelectable) nodes.get(lastSelectedItemPosition)).isSelected()) {
                                break;
                            }
                            lastSelectedItemPosition--;
                        }
                        //if control is not pressed down then clear all selections
                        if (!event.isControlDown()) {
                            nodes.forEach(n -> {
                                if (n instanceof FileLabelSelectable && !label.equals(n))
                                    ((FileLabelSelectable) n).setSelected(false);
                            });
                        }
                        //if clicked item is higher than both last and first selected item
                        //then select all items between clicked item and last selected item
                        if (clickedItemPosition < lastSelectedItemPosition && clickedItemPosition < firstSelectedItemPosition) {
                            for (int i = clickedItemPosition; i <= lastSelectedItemPosition; i++) {
                                ((FileLabelSelectable) nodes.get(i)).setSelected(true);
                            }
                        }
                        //if clicked item is lower than first selected item
                        //then select all items between clicked item and first selected item
                        else {
                            for (int i = firstSelectedItemPosition; i <= clickedItemPosition; i++) {
                                ((FileLabelSelectable) nodes.get(i)).setSelected(true);
                            }
                        }
                    } else if (event.isControlDown()) {
                        label.setSelected(!label.isSelected());
                    } else {
                        if (event.getButton()==MouseButton.PRIMARY && label.isSelected()) {
                            directoryClickedTwice(label.getFile().getPath(), whichList);
                        } else {
                            if(event.getButton()!=MouseButton.SECONDARY) {
                                nodes.forEach(n -> {
                                    if (n instanceof FileLabelSelectable && !label.equals(n))
                                        ((FileLabelSelectable) n).setSelected(false);
                                });
                            }
                            label.setSelected(true);
                        }
                    }
                }
            });
            fileLists[whichList].addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
                if (event.getPickResult().getIntersectedNode().equals(label) && label.isSelected()) {
                    label.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
                }
            });
            label.setOnMouseEntered(event -> label.onMouseHoverEnter());
            label.setOnMouseExited(event -> label.onMouseHoverLeave());
            fileLists[whichList].getChildren().add(label);
        }
    }


    /**
     * displays the names of all the files in the given path
     * @param files array of files to display
     */
    @Override
    public void displayFiles(File[] files, int whichList){
        fileLists[whichList].getChildren().clear();
        if(files!=null) {
            viewFiles(files, whichList);
        } else {
            Label label = new Label();
            label.setText("directory does not exist");
            fileLists[whichList].getChildren().add(label);
        }
    }

}

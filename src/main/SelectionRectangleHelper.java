package main;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import nodes.SelectableFileLabel;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * sole purpose of this class is containing all the code responsible for the selection rectangle
 */
class SelectionRectangleHelper {

    final private Pane drawingPane;
    final private VBox fileLists[];
    final private Rectangle selectionRectangle = new Rectangle();
    final private Delta dragDelta = new Delta();

    SelectionRectangleHelper(Pane drawingPane, VBox fileLists[]) {
        this.drawingPane = drawingPane;
        this.fileLists=fileLists;

    }

    private void setSelectionRectangleDimensions(double startX, double startY, double width, double height){
        selectionRectangle.setX(startX);
        selectionRectangle.setY(startY);
        selectionRectangle.setWidth(width);
        selectionRectangle.setHeight(height);
    }

    /**
     *
     * @param pane pane to which events should be added
     */
    void handleSelectionRectangle(Pane pane, FileEventHelper.MoveFilesEvent moveFilesEvent){
        selectionRectangle.setOpacity(0.5);
        selectionRectangle.setFill(Color.BLUE);
        final ArrayList<SelectableFileLabel> nodesSelectedBeforeDrawing = new ArrayList<>();
        final ArrayList<SelectableFileLabel> draggedNodes = new ArrayList<>();
        final ArrayList<SelectableFileLabel> onePaneChildNodes = new ArrayList<>();
        final ArrayList<SelectableFileLabel> allNodes = new ArrayList<>();
        pane.setOnMousePressed(event->{
            dragDelta.startX = event.getSceneX();
            dragDelta.startY = event.getSceneY();
            onePaneChildNodes.addAll(pane.getChildrenUnmodifiable().stream().filter(node -> node instanceof SelectableFileLabel).map(node -> (SelectableFileLabel)node).collect(Collectors.toList()));
            boolean selectedNodeWasClicked = onePaneChildNodes.stream().anyMatch(
                    node -> node.contains(dragDelta.startX, dragDelta.startY) && node.isSelected());
            //if control is pressed or if clicked node was already selected
            //don't remove the selection from previously selected nodes
            if (event.isControlDown() || event.isShiftDown() || selectedNodeWasClicked) {
                nodesSelectedBeforeDrawing.addAll(onePaneChildNodes.stream().filter(SelectableFileLabel::isSelected).collect(Collectors.toList()));
                if(selectedNodeWasClicked){
                    draggedNodes.addAll(nodesSelectedBeforeDrawing);
                }
            } else {
                onePaneChildNodes.forEach(node -> {
                    if(node.contains(event.getSceneX(), event.getSceneY()))
                        node.setSelected(false);
                });
            }
        });
        pane.setOnMouseDragged(event-> {
            //check if first node clicked was not already selected
            //drawing selection grid and selecting nodes
            if(nodesSelectedBeforeDrawing.stream().noneMatch(
                    node -> node.contains(dragDelta.startX, dragDelta.startY))) {
                ObservableList<Node> nodes = drawingPane.getChildren();
                nodes.remove(selectionRectangle);
                dragDelta.x = event.getSceneX();
                dragDelta.y = event.getSceneY();
                //we have to calculate where the current position of the cursor
                //is relative to where it was clicked
                if (dragDelta.x > dragDelta.startX && dragDelta.y > dragDelta.startY) {
                    setSelectionRectangleDimensions(dragDelta.startX, dragDelta.startY, dragDelta.x - dragDelta.startX, dragDelta.y - dragDelta.startY);
                } else if (dragDelta.x > dragDelta.startX && dragDelta.y < dragDelta.startY) {
                    setSelectionRectangleDimensions(dragDelta.startX, dragDelta.y, dragDelta.x - dragDelta.startX, dragDelta.startY - dragDelta.y);
                } else if (dragDelta.x < dragDelta.startX && dragDelta.y > dragDelta.startY) {
                    setSelectionRectangleDimensions(dragDelta.x, dragDelta.startY, dragDelta.startX - dragDelta.x, dragDelta.y - dragDelta.startY);
                } else if (dragDelta.x < dragDelta.startX && dragDelta.y < dragDelta.startY) {
                    setSelectionRectangleDimensions(dragDelta.x, dragDelta.y, dragDelta.startX - dragDelta.x, dragDelta.startY - dragDelta.y);
                }
                nodes.add(selectionRectangle);
                onePaneChildNodes.forEach(node -> {
                        Bounds nodeBounds = node.localToScene(node.getBoundsInLocal());
                        double nodeMinX = nodeBounds.getMinX();
                        double nodeMaxX = nodeBounds.getMaxX();
                        double nodeMaxY = nodeBounds.getMaxY();
                        double nodeMinY = nodeBounds.getMinY();
                        double selectionMinX = dragDelta.startX < dragDelta.x ? dragDelta.startX : dragDelta.x;
                        double selectionMaxX = dragDelta.startX > dragDelta.x ? dragDelta.startX : dragDelta.x;
                        double selectionMinY = dragDelta.startY < dragDelta.y ? dragDelta.startY : dragDelta.y;
                        double selectionMaxY = dragDelta.startY > dragDelta.y ? dragDelta.startY : dragDelta.y;
                        //check if node is in the selection rectangle
                        if (selectionMinY <= nodeMaxY && selectionMaxY >= nodeMinY && selectionMinX <= nodeMaxX && selectionMaxX >= nodeMinX) {
                            node.setSelected(true);
                        } else if (!nodesSelectedBeforeDrawing.contains(node)) {
                            node.setSelected(false);
                        }
                });
            }
            //dragging nodes around
            else{
                pane.getScene().setCursor(Cursor.CLOSED_HAND);
            }
        });
        //after the mouse is released remove the rectangle and clear its position
        pane.setOnMouseReleased(event -> {
            if(!draggedNodes.isEmpty()){
                SelectableFileLabel currentNode = Stream.concat(fileLists[0].getChildrenUnmodifiable().stream(), fileLists[1].getChildrenUnmodifiable().stream())
                        .filter(node -> node instanceof SelectableFileLabel && node.contains(event.getSceneX(), event.getSceneY()))
                        .map(node -> (SelectableFileLabel) node).findAny().orElse(null);
                if (currentNode != null && !draggedNodes.contains(currentNode)) {
                    moveFilesEvent.moveFilesEvent(draggedNodes.stream().map(SelectableFileLabel::getFile).collect(Collectors.toList()), currentNode.getFile().getPath());
                } else {
                    for (int i = 0; i < 2; i++) {
                        if(fileLists[i]!=pane && fileLists[i].contains(fileLists[i].sceneToLocal(event.getSceneX(), event.getSceneY()))){
                            moveFilesEvent.moveFilesEvent(draggedNodes.stream().map(SelectableFileLabel::getFile).collect(Collectors.toList()), i);
                            break;
                        }
                    }
                }
            }
            drawingPane.getChildren().remove(selectionRectangle);
            setSelectionRectangleDimensions(0,0,0,0);
            dragDelta.reset();
            nodesSelectedBeforeDrawing.clear();
            draggedNodes.clear();
            onePaneChildNodes.clear();
            pane.getScene().setCursor(Cursor.DEFAULT);
        });
    }

    private class Delta{
        double startX = 0;
        double startY = 0;
        double x = 0;
        double y = 0;
        void reset(){
            startY=0;
            startX=0;
            x=0;
            y=0;
        }
    }
}

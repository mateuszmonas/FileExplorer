package main;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import nodes.SelectableFileLabel;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * sole purpose of this class is containing all the code responsible for the selection rectangle
 */
abstract class SelectionRectangleHelper {

    final private static Rectangle selectionRectangle = new Rectangle();
    final private static Delta dragDelta = new Delta();

    private static void setSelectionRectangleDimensions(double startX, double startY, double width, double height){
        selectionRectangle.setX(startX);
        selectionRectangle.setY(startY);
        selectionRectangle.setWidth(width);
        selectionRectangle.setHeight(height);
    }

    /**
     *
     * @param pane pane to which events should be added
     * @param drawingPane pane on which drawing will be happening
     */
    static void handleSelectionRectangle(Pane pane, Pane drawingPane){
        selectionRectangle.setOpacity(0.5);
        selectionRectangle.setFill(Color.BLUE);
        final ArrayList<Node> nodesSelectedBeforeDrawing = new ArrayList<>();
        final MousePosition pressedMousePosition = new MousePosition();
        pane.setOnMousePressed(event->{
            dragDelta.startX = event.getSceneX();
            dragDelta.startY = event.getSceneY();
            nodesSelectedBeforeDrawing.clear();
            //if control is pressed or if clicked node was already selected
            //don't remove the selection from previously selected nodes
            if (event.isControlDown() ||
                    pane.getChildrenUnmodifiable().stream().anyMatch(
                            node -> node instanceof SelectableFileLabel &&
                                    ((SelectableFileLabel) node).areCoordinatesInsideNode(dragDelta.startX, dragDelta.startY) &&
                                    ((SelectableFileLabel) node).isSelected())) {
                nodesSelectedBeforeDrawing.addAll(pane.getChildrenUnmodifiable().stream().filter(node -> node instanceof SelectableFileLabel && ((SelectableFileLabel) node).isSelected()).collect(Collectors.toList()));
            } else {
                pane.getChildrenUnmodifiable().forEach(node -> {
                    if(node instanceof SelectableFileLabel && !((SelectableFileLabel)node).areCoordinatesInsideNode(event.getSceneX(), event.getSceneY()))
                        ((SelectableFileLabel)node).setSelected(false);
                });
            }
        });
        pane.setOnMouseDragged(event-> {
            //check if first node clicked was not already selected
            //drawing selection grid and selecting nodes
            if(nodesSelectedBeforeDrawing.stream().noneMatch(
                    node -> node instanceof SelectableFileLabel && ((SelectableFileLabel) node).areCoordinatesInsideNode(dragDelta.startX, dragDelta.startY))) {
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
                pane.getChildrenUnmodifiable().forEach(node -> {
                    if (node instanceof SelectableFileLabel) {
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
                            ((SelectableFileLabel) node).setSelected(true);
                        } else {
                            if (!nodesSelectedBeforeDrawing.contains(node)) {
                                ((SelectableFileLabel) node).setSelected(false);
                            }
                        }
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
            drawingPane.getChildren().remove(selectionRectangle);
            setSelectionRectangleDimensions(0,0,0,0);
            dragDelta.reset();
            pane.getScene().setCursor(Cursor.DEFAULT);
        });
    }

    private static class Delta{
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

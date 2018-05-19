package main;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

class FileLabel extends Label {
    private boolean isSelected = false;

    FileLabel() {
        super();
    }

    boolean isSelected() {
        return isSelected;
    }

    void setSelected(boolean selected) {
        if(selected) setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        else setBackground(null);
        isSelected = selected;
    }

    boolean areCoordinatesInsideThenode(double x, double y){
        Bounds Bounds = localToScene(getBoundsInLocal());
        double minX = Bounds.getMinX();
        double maxX = Bounds.getMaxX();
        double maxY = Bounds.getMaxY();
        double minY = Bounds.getMinY();
        return x>minX && x<maxX && y>minY && y<maxY;
    }
}

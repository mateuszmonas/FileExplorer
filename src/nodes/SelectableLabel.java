package nodes;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public class SelectableLabel extends Label {
    private boolean isSelected = false;

    public SelectableLabel() {
        super();
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        if(selected) setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        else setBackground(null);
        isSelected = selected;
    }

    public boolean areCoordinatesInsideThenode(double x, double y){
        Bounds Bounds = localToScene(getBoundsInLocal());
        double minX = Bounds.getMinX();
        double maxX = Bounds.getMaxX();
        double maxY = Bounds.getMaxY();
        double minY = Bounds.getMinY();
        return x>minX && x<maxX && y>minY && y<maxY;
    }
}

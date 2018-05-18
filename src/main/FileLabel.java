package main;

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
}

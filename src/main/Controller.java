package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Controller {

    private View view;
    private String[] path = new String[2];

    Controller(View view) {
        path[0] = "C:";
        path[1] = "D:";
        this.view = view;
    }

    void start(){
        getFiles(0);
        getFiles(1);
    }

    /**
     * Gets all files in the given path
     * and updates the view
     */
    void getFiles(int whichList){
        File folder = new File(path[whichList]);
        File[] fileList = folder.listFiles();
        view.displayFiles(fileList, whichList);
    }
}

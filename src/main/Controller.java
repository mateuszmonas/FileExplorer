package main;

import java.io.File;
import java.io.IOException;

public class Controller {

    private View view;
    private String path;

    Controller(View view) {
        path="D:";
        this.view = view;
    }

    void start(){
        getFiles();
    }

    /**
     * Gets all files in the given path
     * and updates the view
     */
    void getFiles(){
        File folder = new File(path);
        File[] fileList = folder.listFiles();
        view.displayFiles(fileList);
    }
}

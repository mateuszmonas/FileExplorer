package main;

import java.io.File;

class Controller {

    private View view;
    private String path;

    Controller(View view) {
        this.view = view;
    }

    void start(){
        path="D:";
        view.start();
        getFiles();
    }

    /**
     * sets path currently operated on
     * @param path filesystem path
     */
    void setPath(String path) {
        this.path = path;
    }

    /**
     * Gets all files in the given path
     * and updates the view
     */
    void getFiles(){
        File folder = new File(path);
        File[] fileList = folder.listFiles();
        view.displayFiles(fileList, path);
    }
}

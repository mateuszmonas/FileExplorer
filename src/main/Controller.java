package main;

import java.io.File;

class Controller {

    private View view;
    private String path;

    Controller(View view) {
        this.view = view;
    }

    void start(){
        view.start();
        path="D:";
        getFiles(path);
    }

    /**
     * sets path currently operated on
     * @param path filesystem path
     */
    void setPath(String path) {
        this.path = path;
        getFiles(path);
    }

    /**
     * Gets all files in the given path
     * and updates the view
     * @param path filesystem path
     */
    private void getFiles(String path){
        File folder = new File(path);
        File[] fileList = folder.listFiles();
        view.showFiles(fileList, path);
    }
}

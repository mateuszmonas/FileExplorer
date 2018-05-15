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

    void setPath(String path) {
        this.path = path;
        getFiles(path);
    }

    private void getFiles(String path){
        File folder = new File(path);
        //get files in the directory
        File[] fileList = folder.listFiles();
        view.showFiles(fileList, path);
    }
}

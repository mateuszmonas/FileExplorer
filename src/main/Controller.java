package main;

import java.io.File;

public class Controller {

    View view;
    String path;

    public Controller(View view) {
        this.view = view;
    }

    public void start(){
        view.start();
        path="D:";
        getFiles(path);
    }

    public void setPath(String path) {
        this.path = path;
        getFiles(path);
    }

    private void getFiles(String path){
        File folder = new File(path);
        //get files in the directory
        File[] fileList = folder.listFiles();
        view.showFiles(fileList);
    }
}

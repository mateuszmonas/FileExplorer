package main;

import file.FileTransferable;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Controller {

    private View view;
    private String[] paths = new String[2];

    Controller(View view) {
        paths[0] = "C:\\";
        paths[1] = "D:\\";
        this.view = view;
    }

    void changeDirectory(String path, int whichList){
        paths[whichList] = path;
        getFiles(whichList);
    }

    void start(){
        getFiles(0);
        getFiles(1);
    }

    @SuppressWarnings("unchecked")
    void pasteFilesFromClipboard(int whichList){
        Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable t = c.getContents(this);
        if (t == null)
            return;
        try {
                if(Arrays.stream(t.getTransferDataFlavors()).anyMatch(dataFlavor -> dataFlavor.equals(DataFlavor.javaFileListFlavor))) {
                    File dest = new File(paths[whichList]);
                    ((List<File>) t.getTransferData(DataFlavor.javaFileListFlavor)).forEach(file -> {
                        try {
                            System.out.println(dest.getPath());
                            System.out.println(file.getPath());
                            if(file.isFile()){
                                FileUtils.copyFileToDirectory(file,dest);
                            }else if (file.isDirectory()) {
                                FileUtils.copyDirectoryToDirectory(file, dest);
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    });
                }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        getFiles(whichList);
    }

    void copyFilesToClipboard(List<File> files){
        FileTransferable ft = new FileTransferable(files);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ft, (clipboard, contents) -> System.out.println("Lost ownership"));
    }

    void moveFiles(List<File> files, String path){
        files.forEach(file -> {
            try {
                FileUtils.moveDirectoryToDirectory(file, new File(path), false);
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    /**
     * Gets all files in the given paths
     * and updates the view
     */
    private void getFiles(int whichList){
        File folder = new File(paths[whichList]);
        File[] fileList = folder.listFiles();
        if(fileList!=null) {
            fileList = Arrays.stream(fileList).filter(file -> !file.isHidden()).toArray(File[]::new);
        }
        view.displayPath(paths[whichList], whichList);
        view.displayFiles(fileList, whichList);
    }
}

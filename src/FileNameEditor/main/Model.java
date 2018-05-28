package FileNameEditor.main;

import FileNameEditor.file.FileTransferable;
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

public class Model implements ViewContract.Model {

    private ViewContract.Controller controller;
    private String[] paths = new String[2];
    private boolean cuttingFiles = false;

    public Model() {
        if(System.getProperty("os.name").equals("Linux")){
            paths[0] = "/media/storage/tests";
            paths[1] = "/media/storage/tests";
        } else if(System.getProperty("os.name").startsWith("Windows")){
            paths[0] = "D:\\";
            paths[1] = "D:\\";
        }
    }

    @Override
    public void changeDirectory(String path, int whichList){
        if(new File(path).isDirectory()) {
            paths[whichList] = path;
        }
        getFiles();
    }

    @Override
    public void start(ViewContract.Controller controller){
        this.controller=controller;
        getFiles();
    }

    @Override
    public void cutFiles(List<File> files){
        //set cutting files to true
        cuttingFiles = true;
        //add files to clipboard
        FileTransferable ft = new FileTransferable(files);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ft, lostOwnership);
    }

    @Override
    public void deleteFiles(List<File> files){
        files.forEach(
                file -> {
                    try {
                        FileUtils.forceDelete(file);
                    }catch (IOException e){
                        e.printStackTrace();
                        System.out.println("could not delete file");
                    }
                }
        );
        getFiles();
    }

    @Override
    public void moveFilesToTrash(List<File> files){
        if(com.sun.jna.platform.FileUtils.getInstance().hasTrash()){
            try {
                com.sun.jna.platform.FileUtils.getInstance().moveToTrash(files.toArray(new File[0]));
            } catch (Exception e){
                e.printStackTrace();
            }
        } else {
            System.out.println("could not move files to trash");
        }
        getFiles();
    }

    @Override
    public void createFile(String name, int whichList){
        File newFile = new File(paths[whichList] + File.separator + name);
        if(!newFile.mkdir()){
            System.out.println("file was not created");
        }
        getFiles();
    }



    @SuppressWarnings("unchecked")
    @Override
    public void pasteFilesFromClipboard(int whichList){
        Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable t = c.getContents(this);
        File dest = new File(paths[whichList]);
        if (t == null || !checkDestDirectory(dest))
            return;
        try {
            List<File> filesFromClipboard = new ArrayList<>();
            if(Arrays.stream(t.getTransferDataFlavors()).anyMatch(dataFlavor -> dataFlavor.equals(DataFlavor.javaFileListFlavor))) {
                filesFromClipboard.addAll((List<File>) t.getTransferData(DataFlavor.javaFileListFlavor));
            }
            if(cuttingFiles){
                moveFiles(filesFromClipboard, whichList);
            } else {
                filesFromClipboard.forEach(file -> {
                    try {
                        if (file.isDirectory()) {
                            FileUtils.copyDirectoryToDirectory(file, dest);
                        } else if (file.isFile()){
                            FileUtils.copyFileToDirectory(file, dest);
                        }
                    }catch (Exception e){e.printStackTrace();}
                });
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        getFiles();
    }


    @Override
    public void copyFilesToClipboard(List<File> files){
        cuttingFiles=false;
        FileTransferable ft = new FileTransferable(files);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ft, lostOwnership);
    }

    private boolean checkDestDirectory(File dest){
        if (dest.exists()) return true;
        else {
            System.out.println("Destination directory does not exist");
            return false;
        }
    }


    @Override
    public void moveFiles(List<File> files, String path){
        File dest = new File(path);
        if(checkDestDirectory(dest)) {
            files.forEach(file -> {
                try {
                    if (file.isDirectory()) {
                        FileUtils.moveDirectoryToDirectory(file, new File(path), false);
                    } else if (file.isFile()) {
                        FileUtils.moveFileToDirectory(file, new File(path), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        getFiles();
    }


    @Override
    public void moveFiles(List<File> files, int whichList){
        File dest = new File(paths[whichList]);
        if(checkDestDirectory(dest) && !paths[0].equals(paths[1])) {
            files.forEach(file -> {
                try {
                    if (file.isDirectory()) {
                        FileUtils.moveDirectoryToDirectory(file, dest, false);
                    } else if (file.isFile()) {
                        FileUtils.moveFileToDirectory(file, dest, false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        getFiles();
    }

    /**
     * Gets all files in the given paths
     * and updates the controller
     */
    private void getFiles(){
        for(int whichList = 0;whichList<2;whichList++) {
            File folder = new File(paths[whichList]);
            File[] fileList = folder.listFiles();
            if (fileList != null) {
                fileList = Arrays.stream(fileList).filter(file -> !file.isHidden()).toArray(File[]::new);
            }
            controller.displayPath(paths[whichList], whichList);
            controller.displayFiles(fileList, whichList);
        }
    }

    private ClipboardOwner lostOwnership = (clipboard, contents) -> {
            System.out.println("Lost ownership");
            cuttingFiles = false;
        };

}

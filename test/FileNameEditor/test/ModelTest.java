package FileNameEditor.test;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import FileNameEditor.main.*;

public class ModelTest {
    @Rule
    public TemporaryFolder folder= new TemporaryFolder();

    private ViewContract.Model model;

    public ModelTest() {
        model = new Model();
        model.start(new ViewContract.Controller() {
            @Override
            public void displayPath(String path, int whichList) {

            }

            @Override
            public void displayFiles(File[] files, int whichList) {

            }
        });
    }

    @Test
    public void checkIfFilesMoved() throws IOException {
        File source = folder.newFolder("source");
        File dest = folder.newFolder("dest");
        File subFolder = new File(source, "subFolder");
        File fileOne = new File(source, "test.txt");
        File fileTwo = new File(subFolder, "test2.txt");
        Assert.assertTrue(subFolder.mkdir());
        Assert.assertTrue(fileOne.mkdir());
        Assert.assertTrue(fileTwo.mkdir());
        model.moveFiles(Collections.singletonList(source), dest.getPath());
        Assert.assertTrue(new File(dest.getPath() + File.separator + source.getName()).exists());
        Assert.assertTrue(new File(dest.getPath() + File.separator + source.getName() + File.separator + subFolder.getName()).exists());
        Assert.assertTrue(new File(dest.getPath() + File.separator + source.getName() + File.separator + subFolder.getName()).exists());
        Assert.assertTrue(new File(dest.getPath() + File.separator + source.getName() + File.separator + fileOne.getName()).exists());
        Assert.assertTrue(new File(dest.getPath() + File.separator + source.getName() + File.separator + subFolder.getName() + File.separator + fileTwo.getName()).exists());
    }

    @Test
    public void checkIfFileCreated(){
        String fileName = "Created Test File";
        String path = folder.getRoot().getPath();
        model.changeDirectory(path, 0);
        model.createFile(fileName, 0);
        Assert.assertTrue(new File(path + File.separator + fileName).exists());
    }

    @Test
    public void checkIfFileDeleted() throws IOException{
        File fileToDelete = folder.newFolder("fileToDelete");
        File fileToDelete2 = folder.newFolder("fileToDelete2");
        Assert.assertTrue(fileToDelete.exists());
        Assert.assertTrue(fileToDelete2.exists());
        model.deleteFiles(Arrays.asList(fileToDelete, fileToDelete2));
        Assert.assertFalse(fileToDelete.exists());
        Assert.assertFalse(fileToDelete2.exists());
    }

    @Test
    public void checkIfFilesCopiedToClipboard() throws IOException, UnsupportedFlavorException {
        File source = folder.newFolder("source");
        File subFolder = new File(source, "subFolder");
        File fileOne = new File(source, "test.txt");
        Assert.assertTrue(subFolder.mkdir());
        Assert.assertTrue(fileOne.mkdir());
        List<File> files = Arrays.asList(subFolder, fileOne);
        model.copyFilesToClipboard(files);
        Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable t = c.getContents(this);
        Assert.assertTrue(Arrays.stream(t.getTransferDataFlavors()).anyMatch(dataFlavor -> dataFlavor.equals(DataFlavor.javaFileListFlavor)));
        Assert.assertEquals(files, t.getTransferData(DataFlavor.javaFileListFlavor));
    }

}

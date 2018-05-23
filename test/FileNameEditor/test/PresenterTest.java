package FileNameEditor.test;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import FileNameEditor.main.*;

public class PresenterTest {
    @Rule
    public TemporaryFolder folder= new TemporaryFolder();

    private ViewContract.Presenter presenter;

    public PresenterTest() {
        presenter=new Controller(new ViewContract.View() {
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
        presenter.moveFiles(Collections.singletonList(source), dest.getPath());
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
        presenter.changeDirectory(path, 0);
        presenter.createFiles(fileName, 0);
        Assert.assertTrue(new File(path + File.separator + fileName).exists());
    }
}

package com.dmytrobilokha.nibee.service.file;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.nio.file.Paths;

import static org.testng.Assert.assertEquals;

@Test(groups = "service.unit")
public class FileServiceTest {

    private FileService fileService;

    @BeforeClass
    public void init() {
        this.fileService = new FileService();
    }

    @DataProvider(name = "fileTypes")
    public Object[][] getFileTypesToTest() {
        return new Object[][]{
                {"/", ""}
                , {"/me.jpg/a", ""}
                , {"/me/a.a", ""}
                , {"/me/img.jpg", "image/jpeg"}
                , {"/me/.jpg", ""}
                , {"/me/.", ""}
                , {"/me/jpg.", ""}
                , {"/me/jpg", ""}
        };
    }

    @Test(dataProvider = "fileTypes")
    public void testContentTypeReturned(String path, String expectedContentType) {
        assertEquals(expectedContentType, fileService.getFileContentType(Paths.get(path)));
    }

}
package com.dmytrobilokha.nibee.service.file;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class FileServiceTest {

    private FileService fileService;
    private final Path resourcePath;
    private final String expectedContentType;

    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(new Object[][]{
                {"/", ""}
                , {"/me.jpg/a", ""}
                , {"/me/a.a", ""}
                , {"/me/img.jpg", "image/jpeg"}
                , {"/me/.jpg", ""}
                , {"/me/.", ""}
                , {"/me/jpg.", ""}
                , {"/me/jpg", ""}
        });
    }

    public FileServiceTest(String resource, String expectedContentType) {
        this.resourcePath = Paths.get(resource);
        this.expectedContentType = expectedContentType;
    }

    @Before
    public void init() {
        this.fileService = new FileService();
    }

    @Test
    public void testContentTypeReturned() {
        assertEquals(expectedContentType, fileService.getFileContentType(resourcePath));
    }

}
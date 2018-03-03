package com.dmytrobilokha.nibee.web.blog;


import com.dmytrobilokha.nibee.service.config.ConfigProperty;
import com.dmytrobilokha.nibee.service.config.ConfigService;
import com.dmytrobilokha.nibee.service.file.FileService;
import com.dmytrobilokha.nibee.service.post.PostService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class PostResourceBlogResponseTest {

    private static final String POST_NAME = "postname";
    private static final String POST_RESOURCE = "post-resource.jpg";
    private static final String RESOURCE_CONTENT = "post/content";
    private static final long FILE_SIZE = 1234L;
    private PostResourceBlogResponse blogResponse;
    private ConfigService mockConfigService;
    private PostService mockPostService;
    private FileService mockFileService;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;

    @Before
    public void init() throws IOException {
        mockConfigService = Mockito.mock(ConfigService.class);
        Mockito.when(mockConfigService.getAsString(ConfigProperty.POSTS_ROOT)).thenReturn("/home/nibee");
        mockPostService = Mockito.mock(PostService.class);
        Mockito.when(mockPostService.findPostPathByName(Mockito.anyString())).thenReturn(Optional.empty());
        mockFileService = Mockito.mock(FileService.class);
        Mockito.when(mockFileService.isFileRegularAndReadable(Mockito.any())).thenReturn(false);
        Mockito.when(mockFileService.getFileContentType(Mockito.any())).thenReturn("");
        Mockito.when(mockFileService.getFileSize(Mockito.any())).thenReturn(FILE_SIZE);
        mockRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getRequestDispatcher(Mockito.anyString()))
                .thenReturn(Mockito.mock(RequestDispatcher.class));
        mockResponse = Mockito.mock(HttpServletResponse.class);
        blogResponse = new PostResourceBlogResponse(mockConfigService, mockPostService, mockFileService
                , POST_NAME, POST_RESOURCE);
    }

    @Test
    public void testSends404WhenPostNotFound() throws IOException {
        Mockito.when(mockFileService.isFileRegularAndReadable(Mockito.any())).thenReturn(true);
        Mockito.when(mockFileService.getFileContentType(Mockito.any())).thenReturn(RESOURCE_CONTENT);
        blogResponse.respond(mockRequest, mockResponse);
        Mockito.verify(mockResponse).sendError(404);
    }

    @Test
    public void testSends404WhenFileIsNotReadable() throws IOException {
        Mockito.when(mockPostService.findPostPathByName(Mockito.anyString())).thenReturn(Optional.of("path"));
        Mockito.when(mockFileService.getFileContentType(Mockito.any())).thenReturn(RESOURCE_CONTENT);
        blogResponse.respond(mockRequest, mockResponse);
        Mockito.verify(mockResponse).sendError(404);
    }

    @Test
    public void testSends404WhenNoContentType() throws IOException {
        Mockito.when(mockPostService.findPostPathByName(Mockito.anyString())).thenReturn(Optional.of("path"));
        Mockito.when(mockFileService.isFileRegularAndReadable(Mockito.any())).thenReturn(true);
        blogResponse.respond(mockRequest, mockResponse);
        Mockito.verify(mockResponse).sendError(404);
    }

    @Test
    public void testStreamsResourceFile() throws IOException {
        Path resourcePath = Paths.get("/home/nibee/path/" + POST_RESOURCE);
        Mockito.when(mockPostService.findPostPathByName(POST_NAME)).thenReturn(Optional.of("path"));
        Mockito.when(mockFileService.getFileContentType(resourcePath)).thenReturn(RESOURCE_CONTENT);
        Mockito.when(mockFileService.isFileRegularAndReadable(resourcePath)).thenReturn(true);
        blogResponse.respond(mockRequest, mockResponse);
        Mockito.verify(mockResponse).setContentType(RESOURCE_CONTENT);
        Mockito.verify(mockResponse).setContentLengthLong(FILE_SIZE);
        Mockito.verify(mockFileService).dumpFileToStream(Mockito.eq(resourcePath), Mockito.any());
    }

}
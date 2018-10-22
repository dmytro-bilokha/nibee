package com.dmytrobilokha.nibee.web.blog;

import com.dmytrobilokha.nibee.service.config.ConfigProperty;
import com.dmytrobilokha.nibee.service.config.ConfigService;
import com.dmytrobilokha.nibee.service.file.FileService;
import com.dmytrobilokha.nibee.service.post.PostService;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Test(groups = "web.unit")
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

    @BeforeMethod
    public void init() throws IOException {
        mockConfigService = mock(ConfigService.class);
        when(mockConfigService.getAsString(ConfigProperty.POSTS_ROOT)).thenReturn("/home/nibee");
        mockPostService = mock(PostService.class);
        when(mockPostService.findPostPathByName(anyString())).thenReturn(null);
        mockFileService = mock(FileService.class);
        when(mockFileService.isFileRegularAndReadable(any())).thenReturn(false);
        when(mockFileService.getFileContentType(any())).thenReturn("");
        when(mockFileService.getFileSize(any())).thenReturn(FILE_SIZE);
        mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getRequestDispatcher(anyString()))
                .thenReturn(mock(RequestDispatcher.class));
        mockResponse = mock(HttpServletResponse.class);
        blogResponse = new PostResourceBlogResponse(mockConfigService, mockPostService, mockFileService
                , POST_NAME, POST_RESOURCE);
    }

    public void sends404WhenPostNotFound() throws IOException {
        when(mockFileService.isFileRegularAndReadable(any())).thenReturn(true);
        when(mockFileService.getFileContentType(any())).thenReturn(RESOURCE_CONTENT);
        blogResponse.respond(mockRequest, mockResponse);
        verify(mockResponse).sendError(404);
    }

    public void sends404WhenFileIsNotReadable() throws IOException {
        when(mockPostService.findPostPathByName(anyString())).thenReturn("path");
        when(mockFileService.getFileContentType(any())).thenReturn(RESOURCE_CONTENT);
        blogResponse.respond(mockRequest, mockResponse);
        verify(mockResponse).sendError(404);
    }

    public void sends404WhenNoContentType() throws IOException {
        when(mockPostService.findPostPathByName(anyString())).thenReturn("path");
        when(mockFileService.isFileRegularAndReadable(any())).thenReturn(true);
        blogResponse.respond(mockRequest, mockResponse);
        verify(mockResponse).sendError(404);
    }

    public void streamsResourceFile() throws IOException {
        Path resourcePath = Paths.get("/home/nibee/path/" + POST_RESOURCE);
        when(mockPostService.findPostPathByName(POST_NAME)).thenReturn("path");
        when(mockFileService.getFileContentType(resourcePath)).thenReturn(RESOURCE_CONTENT);
        when(mockFileService.isFileRegularAndReadable(resourcePath)).thenReturn(true);
        blogResponse.respond(mockRequest, mockResponse);
        verify(mockResponse).setContentType(RESOURCE_CONTENT);
        verify(mockResponse).setContentLengthLong(FILE_SIZE);
        verify(mockFileService).dumpFileToStream(eq(resourcePath), any());
    }

}
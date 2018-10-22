package com.dmytrobilokha.nibee.web.blog;

import com.dmytrobilokha.nibee.data.Post;
import com.dmytrobilokha.nibee.service.config.ConfigProperty;
import com.dmytrobilokha.nibee.service.config.ConfigService;
import com.dmytrobilokha.nibee.service.file.FileService;
import com.dmytrobilokha.nibee.service.post.PostService;
import com.dmytrobilokha.nibee.web.comment.CommentsModelCreator;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

@Test(groups = "web.unit")
public class PostEntryBlogResponseTest {

    private static final String POST_NAME = "postname";
    private PostEntryBlogResponse blogResponse;
    private ConfigService mockConfigService;
    private PostService mockPostService;
    private FileService mockFileService;
    private CommentsModelCreator mockCommentsModelCreator;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;

    @BeforeMethod
    public void init() {
        mockCommentsModelCreator = mock(CommentsModelCreator.class);
        mockConfigService = mock(ConfigService.class);
        when(mockConfigService.getAsString(ConfigProperty.POSTS_ROOT)).thenReturn("/home/nibee");
        mockPostService = mock(PostService.class);
        when(mockPostService.findPostByName(anyString())).thenReturn(null);
        mockFileService = mock(FileService.class);
        when(mockFileService.isFileRegularAndReadable(any())).thenReturn(false);
        mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getRequestURI()).thenReturn("/blog/" + POST_NAME);
        when(mockRequest.getRequestDispatcher(anyString()))
                .thenReturn(mock(RequestDispatcher.class));
        mockResponse = mock(HttpServletResponse.class);
        blogResponse = new PostEntryBlogResponse(mockConfigService, mockPostService, mockFileService
                , mockCommentsModelCreator, POST_NAME);
    }

    public void sends404WhenPostNotFound() throws IOException {
        when(mockFileService.isFileRegularAndReadable(any())).thenReturn(true);
        blogResponse.respond(mockRequest, mockResponse);
        verify(mockResponse).sendError(404);
    }

    public void sends404WhenFileIsNotReadable() throws IOException {
        Post post = createPost();
        when(mockPostService.findPostByName(anyString())).thenReturn(post);
        blogResponse.respond(mockRequest, mockResponse);
        verify(mockResponse).sendError(404);
    }

    public void forwardsToJsp() {
        Post post = createPost();
        when(mockPostService.findPostByName(POST_NAME)).thenReturn(post);
        when(mockFileService.isFileRegularAndReadable(Paths.get("/home/nibee/path/_post_.html")))
                .thenReturn(true);
        blogResponse.respond(mockRequest, mockResponse);
        verify(mockRequest).getRequestDispatcher("/WEB-INF/jsp/postPage.jspx");
    }

    public void setsModelAttribute() {
        Post post = createPost();
        when(mockPostService.findPostByName(POST_NAME)).thenReturn(post);
        when(mockFileService.isFileRegularAndReadable(Paths.get("/home/nibee/path/_post_.html")))
                .thenReturn(true);
        blogResponse.respond(mockRequest, mockResponse);
        ArgumentCaptor<PostModel> modelArgumentCaptor = ArgumentCaptor.forClass(PostModel.class);
        verify(mockRequest).setAttribute(eq("postModel"), modelArgumentCaptor.capture());
        List<PostModel> models = modelArgumentCaptor.getAllValues();
        assertEquals(1, models.size());
        PostModel postModel = models.get(0);
        assertEquals("file://localhost/home/nibee/path/_post_.html", postModel.getEntryFileUrl());
        assertEquals("/blog/" + POST_NAME + '/', postModel.getContentBase());
    }

    private Post createPost() {
        return new Post(POST_NAME, "path", Collections.emptySet(), LocalDateTime.of(2018, 4, 17, 6, 30));
    }

}
package com.dmytrobilokha.nibee.web.blog;

import com.dmytrobilokha.nibee.data.Post;
import com.dmytrobilokha.nibee.service.config.ConfigProperty;
import com.dmytrobilokha.nibee.service.config.ConfigService;
import com.dmytrobilokha.nibee.service.file.FileService;
import com.dmytrobilokha.nibee.service.post.PostService;
import com.dmytrobilokha.nibee.web.comment.CommentsModelCreator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PostEntryBlogResponseTest {

    private static final String POST_NAME = "postname";
    private PostEntryBlogResponse blogResponse;
    private ConfigService mockConfigService;
    private PostService mockPostService;
    private FileService mockFileService;
    private CommentsModelCreator mockCommentsModelCreator;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;

    @Before
    public void init() {
        mockCommentsModelCreator = Mockito.mock(CommentsModelCreator.class);
        mockConfigService = Mockito.mock(ConfigService.class);
        Mockito.when(mockConfigService.getAsString(ConfigProperty.POSTS_ROOT)).thenReturn("/home/nibee");
        mockPostService = Mockito.mock(PostService.class);
        Mockito.when(mockPostService.findPostByName(Mockito.anyString())).thenReturn(null);
        mockFileService = Mockito.mock(FileService.class);
        Mockito.when(mockFileService.isFileRegularAndReadable(Mockito.any())).thenReturn(false);
        mockRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getRequestURI()).thenReturn("/blog/" + POST_NAME);
        Mockito.when(mockRequest.getRequestDispatcher(Mockito.anyString()))
                .thenReturn(Mockito.mock(RequestDispatcher.class));
        mockResponse = Mockito.mock(HttpServletResponse.class);
        blogResponse = new PostEntryBlogResponse(mockConfigService, mockPostService, mockFileService
                , mockCommentsModelCreator, POST_NAME);
    }

    @Test
    public void testSends404WhenPostNotFound() throws IOException {
        Mockito.when(mockFileService.isFileRegularAndReadable(Mockito.any())).thenReturn(true);
        blogResponse.respond(mockRequest, mockResponse);
        Mockito.verify(mockResponse).sendError(404);
    }

    @Test
    public void testSends404WhenFileIsNotReadable() throws IOException {
        Post post = createPost();
        Mockito.when(mockPostService.findPostByName(Mockito.anyString())).thenReturn(post);
        blogResponse.respond(mockRequest, mockResponse);
        Mockito.verify(mockResponse).sendError(404);
    }

    @Test
    public void testForwardsToJsp() {
        Post post = createPost();
        Mockito.when(mockPostService.findPostByName(POST_NAME)).thenReturn(post);
        Mockito.when(mockFileService.isFileRegularAndReadable(Paths.get("/home/nibee/path/_post_.html")))
                .thenReturn(true);
        blogResponse.respond(mockRequest, mockResponse);
        Mockito.verify(mockRequest).getRequestDispatcher("/WEB-INF/jsp/postPage.jspx");
    }

    @Test
    public void testSetsModelAttribute() {
        Post post = createPost();
        Mockito.when(mockPostService.findPostByName(POST_NAME)).thenReturn(post);
        Mockito.when(mockFileService.isFileRegularAndReadable(Paths.get("/home/nibee/path/_post_.html")))
                .thenReturn(true);
        blogResponse.respond(mockRequest, mockResponse);
        ArgumentCaptor<PostModel> modelArgumentCaptor = ArgumentCaptor.forClass(PostModel.class);
        Mockito.verify(mockRequest).setAttribute(Mockito.eq("postModel"), modelArgumentCaptor.capture());
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
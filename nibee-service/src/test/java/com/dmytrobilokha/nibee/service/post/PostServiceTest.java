package com.dmytrobilokha.nibee.service.post;

import com.dmytrobilokha.nibee.dao.post.PostDao;
import com.dmytrobilokha.nibee.data.Post;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PostServiceTest {

    private PostService postService;
    private PostDao postDaoMock;
    private List<Post> mockDaoOutput;

    @Before
    public void init() {
        postDaoMock = Mockito.mock(PostDao.class);
        mockDaoOutput = new ArrayList<>();
        Mockito.when(postDaoMock.findPostByName(Mockito.anyString())).thenReturn(mockDaoOutput);
        Mockito.when(postDaoMock.findPostByTagId(Mockito.anyLong())).thenReturn(mockDaoOutput);
        postService = new PostService(postDaoMock);
    }

    @Test
    public void testReturnsEmptyOptional() {
        Optional<Post> result = postService.findPostByName("name doesnt matter");
        assertFalse(result.isPresent());
    }

    @Test
    public void testReturnsFirstPost() {
        Post post1 = Mockito.mock(Post.class);
        Post post2 = Mockito.mock(Post.class);
        assertTrue(post1 != post2);
        mockDaoOutput.add(post1);
        mockDaoOutput.add(post2);
        Optional<Post> result = postService.findPostByName("name doesnt matter");
        assertTrue(result.isPresent());
        assertTrue(post1 == result.get());
    }

}

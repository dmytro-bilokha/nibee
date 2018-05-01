package com.dmytrobilokha.nibee.service.post;

import com.dmytrobilokha.nibee.dao.post.PostDao;
import com.dmytrobilokha.nibee.data.Post;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PostServiceTest {

    private PostService postService;
    private PostDao postDaoMock;

    @Before
    public void init() {
        postDaoMock = Mockito.mock(PostDao.class);
        Mockito.when(postDaoMock.findPostByName(Mockito.anyString())).thenReturn(null);
        postService = new PostServiceImpl(postDaoMock);
    }

    @Test
    public void testReturnsNull() {
        Post result = postService.findPostByName("name doesnt matter");
        assertNull(result);
    }

    @Test
    public void testReturnsPostByName() {
        Post post1 = Mockito.mock(Post.class);
        Mockito.when(postDaoMock.findPostByName(Mockito.anyString())).thenReturn(post1);
        Post result = postService.findPostByName("name doesnt matter");
        assertTrue(post1 == result);
    }

}

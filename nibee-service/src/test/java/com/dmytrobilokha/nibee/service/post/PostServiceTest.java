package com.dmytrobilokha.nibee.service.post;

import com.dmytrobilokha.nibee.dao.post.PostDao;
import com.dmytrobilokha.nibee.data.Post;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

@Test(groups = "service.unit")
public class PostServiceTest {

    private PostService postService;
    private PostDao postDaoMock;

    @BeforeClass
    public void init() {
        postDaoMock = mock(PostDao.class);
        postService = new PostServiceImpl(postDaoMock);
    }

    @BeforeMethod
    public void setupMockDefaults() {
        when(postDaoMock.findPostByName(anyString())).thenReturn(null);
    }

    @AfterMethod
    public void resetMocks() {
        reset(postDaoMock);
    }

    public void returnsNullWhenPostNotFound() {
        Post result = postService.findPostByName("name doesnt matter");
        assertNull(result);
    }

    public void returnsPostByName() {
        Post post1 = mock(Post.class);
        when(postDaoMock.findPostByName(anyString())).thenReturn(post1);
        Post result = postService.findPostByName("name doesnt matter");
        assertTrue(post1 == result);
    }

}

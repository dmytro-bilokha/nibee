package com.dmytrobilokha.nibee.dao.post;

import com.dmytrobilokha.nibee.dao.AbstractDaoTest;
import com.dmytrobilokha.nibee.data.Post;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PostDaoIT extends AbstractDaoTest {

    private PostDao postDao;

    @BeforeClass
    public static void loadData() {
        executeSqlScripts("post.sql");
    }

    @Before
    public void initPostRepository() {
        postDao = getMapper(PostDao.class);
    }

    @Test
    public void checkFindsPostByName() {
        List<Post> posts = postDao.findPostByName("post-about-rest");
        assertEquals(1, posts.size());
        assertEquals("post-about-rest", posts.get(0).getName());
    }

    @Test
    public void checkFindsPostWithoutTagByName() {
        List<Post> posts = postDao.findPostByName("resources");
        assertEquals(1, posts.size());
        assertEquals("resources", posts.get(0).getName());
    }

    @Test
    public void checkDoesntFindPost() {
        List<Post> posts = postDao.findPostByName("SomeNotExistingInDbNameHere");
        assertTrue(posts.isEmpty());
    }

    @Test
    public void checkFindsPostByTagId() {
        final long id = 1L;
        List<Post> posts = postDao.findPostByTagId(id);
        assertEveryPostHasTagWithId(posts, id);
    }

    private void assertEveryPostHasTagWithId(Collection<Post> posts, long id) {
        assertFalse("Posts list should not be empty", posts.isEmpty());
        for (Post post : posts) {
            boolean containsId = post.getTags()
                    .stream()
                    .anyMatch(t -> Long.valueOf(id).equals(t.getId()));
            assertTrue(post + " should has tag with id=" + id, containsId);
        }
    }

    @Test
    public void checkDoesntFindPostByTagId() {
        List<Post> posts = postDao.findPostByTagId(12345678111L);
        assertTrue(posts.isEmpty());
    }

}

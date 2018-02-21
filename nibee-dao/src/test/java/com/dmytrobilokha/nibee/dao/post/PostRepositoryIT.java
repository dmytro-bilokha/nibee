package com.dmytrobilokha.nibee.dao.post;

import com.dmytrobilokha.nibee.dao.AbstractRepositoryTest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PostRepositoryIT extends AbstractRepositoryTest {

    private PostRepository postRepository;

    @BeforeClass
    public static void loadData() {
        executeSqlScripts("post.sql");
    }

    @Before
    public void initPostRepository() {
        postRepository = getMapper(PostRepository.class);
    }

    @Test
    public void checkFetchesNames() {
        List<String> names = postRepository.getNames();
        assertEquals(2, names.size());
        assertTrue(names.contains("alala"));
        assertTrue(names.contains("lalala"));
    }

    @Test
    public void checkFindsPostByName() {
        List<Post> posts = postRepository.findPostByName("alala");
        assertEquals(1, posts.size());
        assertEquals("alala", posts.get(0).getName());
    }

}

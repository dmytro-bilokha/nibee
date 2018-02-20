package com.dmytrobilokha.nibee.dao.post;

import com.dmytrobilokha.nibee.dao.AbstractRepositoryTest;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PostRepositoryIT extends AbstractRepositoryTest {

    @Test
    public void checkFetchesNames() {
        executeSqlStatement("INSERT INTO post (name, path) VALUES ('alala', 'bebebe')");
        executeSqlStatement("INSERT INTO post (name, path) VALUES ('lalala', 'bebebe1')");
        PostRepository postRepository = getMapper(PostRepository.class);
        List<String> names = postRepository.getNames();
        assertEquals(2, names.size());
        assertTrue(names.contains("alala"));
        assertTrue(names.contains("lalala"));
    }

}

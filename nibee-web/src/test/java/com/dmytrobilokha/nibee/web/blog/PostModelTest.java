package com.dmytrobilokha.nibee.web.blog;

import com.dmytrobilokha.nibee.data.Post;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class PostModelTest {

    @Test
    public void testFormatsCreationDate() {
        Post post = new Post("bla", "blah", Collections.emptySet(), LocalDateTime.of(2018, 4, 17, 6, 30));
        PostModel model = new PostModel("ala", "bebebe", post);
        assertEquals("17 Apr 2018", model.getCreatedOn());
    }

}
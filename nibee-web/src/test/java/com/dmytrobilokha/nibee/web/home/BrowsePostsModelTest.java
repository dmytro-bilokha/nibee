package com.dmytrobilokha.nibee.web.home;

import com.dmytrobilokha.nibee.data.Post;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class BrowsePostsModelTest {

    @Test
    public void checkFormatsDateTime() {
        List<Post> posts = Arrays.asList(
                new Post("bla1", "blah1", Collections.emptySet(), LocalDateTime.of(2018, 4, 17, 6, 30, 12, 3000000))
                , new Post("bla2", "blah2", Collections.emptySet(), LocalDateTime.of(2008, 1, 26, 22, 0, 0, 999000000))
        );
        BrowsePostsModel browsePostsModel = new BrowsePostsModel(posts, true, true);
        assertEquals("18-04-17T06:30:12.003", browsePostsModel.getBackParam());
        assertEquals("08-01-26T22:00:00.999", browsePostsModel.getForwardParam());
    }

    //TODO: add test for dateTime parsing
}
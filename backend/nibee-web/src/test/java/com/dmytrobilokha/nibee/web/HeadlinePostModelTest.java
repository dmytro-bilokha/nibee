package com.dmytrobilokha.nibee.web;

import com.dmytrobilokha.nibee.data.PostWithTags;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.testng.Assert.assertEquals;

@Test(groups = "web.unit")
public class HeadlinePostModelTest {

    public void formatsCreationDate() {
        PostWithTags post =
                new PostWithTags("bla", "blah", Collections.emptySet(), LocalDateTime.of(2018, 4, 17, 6, 30));
        HeadlinePostModel model = new HeadlinePostModel(post);
        assertEquals("17 Apr 2018", model.getCreatedOn());
    }

}

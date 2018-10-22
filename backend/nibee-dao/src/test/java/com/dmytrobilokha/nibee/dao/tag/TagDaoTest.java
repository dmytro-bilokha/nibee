package com.dmytrobilokha.nibee.dao.tag;

import com.dmytrobilokha.nibee.dao.AbstractDaoTest;
import com.dmytrobilokha.nibee.data.Tag;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class TagDaoTest extends AbstractDaoTest {

    private TagDao tagDao;

    @BeforeClass
    public void loadData() {
        executeSqlScripts("post.sql");
    }

    @BeforeMethod
    public void getDao() {
        tagDao = getMapper(TagDao.class);
    }

    @Test
    public void returnsAllTags() {
        List<Tag> tags = tagDao.getAll();
        assertTrue(tags.size() > 2);
    }

    @Test(dependsOnMethods = "returnsAllTags")
    public void returnsAllTagsOrdered() {
        List<Tag> tags = tagDao.getAll();
        assertOrdered(tags);
    }

    private void assertOrdered(List<Tag> tags) {
        List<Tag> orderedTags = new ArrayList<>(tags);
        Collections.sort(orderedTags, Comparator.comparing(Tag::getName));
        assertEquals(orderedTags, tags);
    }

}

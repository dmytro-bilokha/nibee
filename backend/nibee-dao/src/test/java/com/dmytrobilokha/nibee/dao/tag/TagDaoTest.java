package com.dmytrobilokha.nibee.dao.tag;

import com.dmytrobilokha.nibee.dao.AbstractDaoTest;
import com.dmytrobilokha.nibee.data.Tag;
import org.apache.ibatis.exceptions.PersistenceException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

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

    @Test
    public void assignsTagsToPost() {
        int tagsAssignedBefore = calculateTableRows("post_tag");
        tagDao.assignTagToPost(4000L, 1000L);
        tagDao.assignTagToPost(4000L, 3000L);
        int tagsAssignedAfter = calculateTableRows("post_tag");
        assertEquals(2, tagsAssignedAfter - tagsAssignedBefore);
    }

    @Test(expectedExceptions = PersistenceException.class)
    public void duplicatesAreNotAllowed() {
        tagDao.assignTagToPost(4000L, 2000L);
    }

}

package com.dmytrobilokha.nibee.dao.tag;

import com.dmytrobilokha.nibee.dao.AbstractDaoTest;
import com.dmytrobilokha.nibee.data.Tag;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TagDaoIT extends AbstractDaoTest {

    private TagDao tagDao;

    @BeforeClass
    public static void loadData() {
        executeSqlScripts("post.sql");
    }

    @Before
    public void initTagDao() {
        tagDao = getMapper(TagDao.class);
    }

    @Test
    public void checkReturnsAllTagsOrdered() {
        List<Tag> tags = tagDao.getAll();
        assertEquals(3, tags.size());
        assertEquals("REST", tags.get(0).getName());
        assertEquals("SOAP", tags.get(1).getName());
        assertEquals("web service", tags.get(2).getName());
    }
}

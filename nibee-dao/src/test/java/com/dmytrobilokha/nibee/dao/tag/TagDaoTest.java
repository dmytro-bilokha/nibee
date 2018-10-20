package com.dmytrobilokha.nibee.dao.tag;

import com.dmytrobilokha.nibee.dao.AbstractDaoTest;
import com.dmytrobilokha.nibee.data.Tag;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.util.List;

import static org.testng.Assert.assertEquals;

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

    public void returnsAllTagsOrdered() {
        List<Tag> tags = tagDao.getAll();
        assertEquals(3, tags.size());
        assertEquals("REST", tags.get(0).getName());
        assertEquals("SOAP", tags.get(1).getName());
        assertEquals("web service", tags.get(2).getName());
    }

}

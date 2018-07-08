package com.dmytrobilokha.nibee.dao.comment;


import com.dmytrobilokha.nibee.dao.AbstractDaoTest;
import com.dmytrobilokha.nibee.data.Comment;
import org.apache.ibatis.exceptions.PersistenceException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CommentDaoIT extends AbstractDaoTest {

    private static final String COMMENT_TABLE = "post_comment";

    private CommentDao commentDao;

    @BeforeClass
    public static void loadData() {
        executeSqlScripts("post.sql", "post_comment.sql");
    }

    @Before
    public void initPostRepository() {
        commentDao = getMapper(CommentDao.class);
    }

    @Test
    public void testInsertsComment() {
        int numCommentsBefore = calculateTableRows(COMMENT_TABLE);
        Comment comment = new Comment(1L, "nickname", "This is a comment", null);
        assertEquals(1, commentDao.insert(comment));
        int numCommentsAfter = calculateTableRows(COMMENT_TABLE);
        assertEquals(1, numCommentsAfter - numCommentsBefore);
        assertNotNull(comment.getId());
    }

    @Test
    public void testBlocksInsertForNonExsistingPost() {
        Comment comment = new Comment(7777777221L, "nickname", "This is a comment", null);
        try {
            commentDao.insert(comment);
        } catch (PersistenceException ex) {
            return;
        }
        fail("Inserting comment referencing to non-existing post should be blocked");
    }

    @Test
    public void testFindsByPostIdAndReturnsOrdered() {
        List<Comment> comments = commentDao.findCommentByPostId(2L);
        assertTrue(comments.size() > 1);
        LocalDateTime previous = LocalDateTime.MAX;
        for (Comment comment : comments) {
            assertTrue(previous.isAfter(comment.getCreatedOn()));
            previous = comment.getCreatedOn();
        }
    }

}
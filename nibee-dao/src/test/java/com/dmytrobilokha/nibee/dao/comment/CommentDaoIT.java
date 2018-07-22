package com.dmytrobilokha.nibee.dao.comment;


import com.dmytrobilokha.nibee.dao.AbstractDaoTest;
import com.dmytrobilokha.nibee.data.Comment;
import org.apache.ibatis.exceptions.PersistenceException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CommentDaoIT extends AbstractDaoTest {

    private static final String COMMENT_TABLE = "post_comment";
    private static final Long NON_EXISTING_POST_ID = 7777777221L;
    private static final Long NON_EXISTING_COMMENT_ID = 7777777228L;

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
        Comment comment = new Comment(1L, null, "nickname", "This is a comment", null);
        assertEquals(1, commentDao.insert(comment));
        int numCommentsAfter = calculateTableRows(COMMENT_TABLE);
        assertEquals(1, numCommentsAfter - numCommentsBefore);
        assertNotNull(comment.getId());
    }

    @Test
    public void testBlocksInsertForNonExistingPost() {
        Comment comment = new Comment(NON_EXISTING_POST_ID, null, "nickname", "This is a comment", null);
        try {
            commentDao.insert(comment);
        } catch (PersistenceException ex) {
            return;
        }
        fail("Inserting comment referencing to non-existing post should be blocked");
    }

    @Test
    public void testBlocksInsertForNonExistingPostComment() {
        Comment comment = new Comment(2L, NON_EXISTING_COMMENT_ID, "nickname", "This is a comment", null);
        try {
            commentDao.insert(comment);
        } catch (PersistenceException ex) {
            return;
        }
        fail("Inserting comment referencing to non-existing parent post comment should be blocked");
    }

    @Test
    public void testFindsByPostIdAndReturnsOrdered() {
        List<Comment> comments = commentDao.findCommentByPostId(2L);
        assertTrue(comments.size() > 1);
        LocalDateTime previous = LocalDateTime.MIN;
        for (Comment comment : comments) {
            assertTrue(previous.isBefore(comment.getCreatedOn()));
            previous = comment.getCreatedOn();
        }
    }

    @Test
    public void testCountsExistingPostAndComment() {
        assertEquals(1, commentDao.countPostComments(2L, 2L));
    }

    @Test
    public void testCountsNotExistingPostAndComment() {
        assertEquals(0, commentDao.countPostComments(NON_EXISTING_POST_ID, NON_EXISTING_COMMENT_ID));
    }

    @Test
    public void testCountsExistingPostAndNotComment() {
        assertEquals(0, commentDao.countPostComments(2L, NON_EXISTING_COMMENT_ID));
    }

    @Test
    public void testReturnsParentCommentId() {
        List<Comment> comments = commentDao.findCommentByPostId(1L);
        Optional<Comment> childComment = comments.stream()
                .filter(comment -> comment.getParentCommentId() != null)
                .findAny();
        assertTrue(childComment.isPresent());
    }

}
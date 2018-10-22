package com.dmytrobilokha.nibee.dao.comment;

import com.dmytrobilokha.nibee.dao.AbstractDaoTest;
import com.dmytrobilokha.nibee.data.Comment;
import org.apache.ibatis.exceptions.PersistenceException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class CommentDaoTest extends AbstractDaoTest {

    private static final String COMMENT_TABLE = "post_comment";
    private static final Long NON_EXISTING_POST_ID = 7777777221L;
    private static final Long NON_EXISTING_COMMENT_ID = 7777777228L;

    private CommentDao commentDao;

    @BeforeClass
    public void loadData() {
        executeSqlScripts("post.sql", "post_comment.sql");
    }

    @BeforeMethod
    public void initPostRepository() {
        commentDao = getMapper(CommentDao.class);
    }

    public void insertsComment() {
        int numCommentsBefore = calculateTableRows(COMMENT_TABLE);
        Comment comment = new Comment(1L, null, "nickname", "This is a comment", null);
        assertEquals(1, commentDao.insert(comment));
        int numCommentsAfter = calculateTableRows(COMMENT_TABLE);
        assertEquals(1, numCommentsAfter - numCommentsBefore);
        assertNotNull(comment.getId());
    }

    @Test(expectedExceptions = PersistenceException.class)
    public void blocksInsertForNonExistingPost() {
        Comment comment = new Comment(NON_EXISTING_POST_ID, null, "nickname", "This is a comment", null);
        commentDao.insert(comment);
    }

    @Test(expectedExceptions = PersistenceException.class)
    public void blocksInsertForNonExistingPostComment() {
        Comment comment = new Comment(2L, NON_EXISTING_COMMENT_ID, "nickname", "This is a comment", null);
        commentDao.insert(comment);
    }

    public void findsByPostIdAndReturnsOrdered() {
        List<Comment> comments = commentDao.findCommentByPostId(2L);
        assertTrue(comments.size() > 1);
        LocalDateTime previous = LocalDateTime.MIN;
        for (Comment comment : comments) {
            assertTrue(previous.isBefore(comment.getCreatedOn()));
            previous = comment.getCreatedOn();
        }
    }

    public void countsExistingPostAndComment() {
        assertEquals(1, commentDao.countPostComments(2L, 2L));
    }

    public void countsNotExistingPostAndComment() {
        assertEquals(0, commentDao.countPostComments(NON_EXISTING_POST_ID, NON_EXISTING_COMMENT_ID));
    }

    public void countsExistingPostAndNotComment() {
        assertEquals(0, commentDao.countPostComments(2L, NON_EXISTING_COMMENT_ID));
    }

    public void returnsParentCommentId() {
        List<Comment> comments = commentDao.findCommentByPostId(1L);
        Optional<Comment> childComment = comments.stream()
                .filter(comment -> comment.getParentCommentId() != null)
                .findAny();
        assertTrue(childComment.isPresent());
    }

}
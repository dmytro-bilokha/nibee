package com.dmytrobilokha.nibee.service.comment;

import com.dmytrobilokha.nibee.dao.comment.CommentDao;
import com.dmytrobilokha.nibee.data.Comment;
import com.dmytrobilokha.nibee.service.post.PostService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class CommentServiceImplTest {

    private CommentDao commentDaoMock;
    private PostService postServiceMock;
    private CommentService commentService;
    private NicknameValidator nicknameValidatorMock;

    @Before
    public void init() {
        commentDaoMock = Mockito.mock(CommentDao.class);
        postServiceMock = Mockito.mock(PostService.class);
        when(postServiceMock.doesPostExist(any())).thenReturn(true);
        when(commentDaoMock.countPostComments(any(), any())).thenReturn(1);
        nicknameValidatorMock = Mockito.mock(NicknameValidator.class);
        when(nicknameValidatorMock.checkNickname(anyString())).thenReturn(Collections.emptyList());
        commentService = new CommentServiceImpl(commentDaoMock, postServiceMock, nicknameValidatorMock);
    }

    @Test
    public void checkBlocksCreationForNonExistingPost() {
        when(postServiceMock.doesPostExist(any())).thenReturn(false);
        assertThrowsException(() -> commentService.createAndSave(1L, null, "author", "content")
                , "Exception should be thrown when trying to comment non-existing post");
    }

    @Test
    public void checkBlocksCreationForNonExistingParentComment() {
        when(commentDaoMock.countPostComments(any(), any())).thenReturn(0);
        assertThrowsException(() -> commentService.createAndSave(1L, 42L, "author", "content")
                , "Exception should be thrown when trying to reply to non-existing comment");
    }

    @Test
    public void checkBlocksCreationForEmptyContent() {
        assertThrowsException(() -> commentService.createAndSave(1L, null, "author", "  ")
                , "Exception should be thrown when trying to comment with empty content");
    }

    @Test
    public void checkCreatesComment() throws CommentCreationException {
        Comment comment = commentService.createAndSave(1L, null, "TheAuthor", "Content");
        assertNotNull(comment);
        Mockito.verify(commentDaoMock, times(1)).insert(comment);
    }

    @Test
    public void checkCallsNicknameValidator() throws CommentCreationException {
        commentService.createAndSave(1L, null, "TheAuthor", "Content");
        Mockito.verify(nicknameValidatorMock, times(1)).checkNickname("TheAuthor");
    }

    @Test
    public void checkReactsOnNicknameValidator() {
        when(nicknameValidatorMock.checkNickname(anyString())).thenReturn(Collections.singletonList("Ooops!"));
        assertThrowsException(() -> commentService.createAndSave(1L, null, "author", "blah")
                , "Exception should be thrown when nickname validator reports invalid nickname");
    }

    private void assertThrowsException(CommentCreator creator, String failMessage) {
        try {
            creator.create();
        } catch (CommentCreationException ex) {
            Mockito.verify(commentDaoMock, times(0)).insert(any());
            return;
        }
        fail(failMessage);
    }

    private interface CommentCreator {
        void create() throws CommentCreationException;
    }

}
package com.dmytrobilokha.nibee.service.comment;

import com.dmytrobilokha.nibee.dao.comment.CommentDao;
import com.dmytrobilokha.nibee.data.Comment;
import com.dmytrobilokha.nibee.service.post.PostService;
import org.mockito.Mockito;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

@Test(groups = "service.unit")
public class CommentServiceImplTest {

    private CommentDao commentDaoMock;
    private PostService postServiceMock;
    private NicknameValidator nicknameValidatorMock;
    private CommentService commentService;

    @BeforeClass
    public void initService() {
        commentDaoMock = Mockito.mock(CommentDao.class);
        postServiceMock = Mockito.mock(PostService.class);
        nicknameValidatorMock = Mockito.mock(NicknameValidator.class);
        commentService = new CommentServiceImpl(commentDaoMock, postServiceMock, nicknameValidatorMock);
    }

    @BeforeMethod
    public void setupMocks() {
        when(postServiceMock.doesPostExist(any())).thenReturn(true);
        when(commentDaoMock.countPostComments(any(), any())).thenReturn(1);
        when(nicknameValidatorMock.checkNickname(anyString())).thenReturn(Collections.emptyList());
    }

    @AfterMethod
    public void resetMocks() {
        reset(commentDaoMock, postServiceMock, nicknameValidatorMock);
    }

    public void blocksCreationForNonExistingPost() {
        when(postServiceMock.doesPostExist(any())).thenReturn(false);
        assertThrowsExceptionWithoutInsertion(() -> commentService.createAndSave(1L, null, "author", "content")
                , "Exception should be thrown when trying to comment non-existing post");
    }

    public void blocksCreationForNonExistingParentComment() {
        when(commentDaoMock.countPostComments(any(), any())).thenReturn(0);
        assertThrowsExceptionWithoutInsertion(() -> commentService.createAndSave(1L, 42L, "author", "content")
                , "Exception should be thrown when trying to reply to non-existing comment");
    }

    public void blocksCreationForEmptyContent() {
        assertThrowsExceptionWithoutInsertion(() -> commentService.createAndSave(1L, null, "author", "  ")
                , "Exception should be thrown when trying to comment with empty content");
    }

    public void createsComment() throws CommentCreationException {
        Comment comment = commentService.createAndSave(1L, null, "TheAuthor", "Content");
        assertNotNull(comment);
        verify(commentDaoMock, times(1)).insert(comment);
    }

    public void callsNicknameValidator() throws CommentCreationException {
        commentService.createAndSave(1L, null, "TheAuthor", "Content");
        verify(nicknameValidatorMock, times(1)).checkNickname("TheAuthor");
    }

    @Test(dependsOnMethods = "callsNicknameValidator")
    public void reactsOnNicknameValidator() {
        when(nicknameValidatorMock.checkNickname(anyString())).thenReturn(Collections.singletonList("Ooops!"));
        assertThrowsExceptionWithoutInsertion(() -> commentService.createAndSave(1L, null, "author", "blah")
                , "Exception should be thrown when nickname validator reports invalid nickname");
    }

    private void assertThrowsExceptionWithoutInsertion(CommentCreator creator, String failMessage) {
        try {
            creator.create();
        } catch (CommentCreationException ex) {
            verify(commentDaoMock, times(0)).insert(any());
            return;
        }
        fail(failMessage);
    }

    private interface CommentCreator {
        void create() throws CommentCreationException;
    }

}

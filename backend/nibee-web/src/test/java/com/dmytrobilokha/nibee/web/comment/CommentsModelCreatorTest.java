package com.dmytrobilokha.nibee.web.comment;

import com.dmytrobilokha.nibee.data.Comment;
import com.dmytrobilokha.nibee.service.comment.CommentService;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

@Test(groups = "web.unit")
public class CommentsModelCreatorTest {

    private CommentService mockCommentService;
    private final List<Comment> commentsFromService = new ArrayList<>();
    private HttpServletRequest mockRequest;

    private CommentsModelCreator commentsModelCreator;
    private CommentsModel createdModel;

    @BeforeClass
    public void init() {
        mockCommentService = mock(CommentService.class);
        mockRequest = mock(HttpServletRequest.class);
        doAnswer(ans -> {
            createdModel = ans.getArgument(1);
            return null;
        }).when(mockRequest).setAttribute(eq("commentsModel"), any());
        when(mockCommentService.fetchPostComments(anyLong())).thenReturn(commentsFromService);
        commentsModelCreator = new CommentsModelCreator(mockCommentService);
    }

    @AfterMethod
    public void clean() {
        commentsFromService.clear();
    }

    public void passesPostIdAndComments() {
        Comment singleComment = createComment(5L, null);
        commentsFromService.add(singleComment);
        commentsModelCreator.createAndPutInRequest(1L, mockRequest);
        assertEquals(Long.valueOf(1), createdModel.getPostId());
        assertEquals(1, createdModel.getComments().size());
    }

    public void rollsCommentTree() {
        fillCommentsWithTestDataset();
        commentsModelCreator.createAndPutInRequest(2L, mockRequest);
        List<Long> commentsId = createdModel.getComments()
                .stream()
                .map(CommentModel::getId)
                .collect(Collectors.toList());
        assertEquals(
                Arrays.asList(1L, 2L, 22L, 21L, 211L, 3L, 31L, 311L, 3111L, 3112L)
                , commentsId
        );
    }

    public void calculatesDepth() {
        fillCommentsWithTestDataset();
        commentsModelCreator.createAndPutInRequest(2L, mockRequest);
        List<Integer> depthShouldBe = createdModel.getComments()
                .stream()
                .map(model -> model.getId().toString().length() - 1)
                .collect(Collectors.toList());
        List<Integer> depthReal = createdModel.getComments()
                .stream()
                .map(CommentModel::getDepthLevel)
                .collect(Collectors.toList());
        assertEquals(depthShouldBe, depthReal);
    }

    private void fillCommentsWithTestDataset() {
        commentsFromService.add(createComment(1L, null));
        commentsFromService.add(createComment(2L, null));
        commentsFromService.add(createComment(3L, null));
        commentsFromService.add(createComment(22L, 2L));
        commentsFromService.add(createComment(21L, 2L));
        commentsFromService.add(createComment(31L, 3L));
        commentsFromService.add(createComment(311L, 31L));
        commentsFromService.add(createComment(3111L, 311L));
        commentsFromService.add(createComment(3112L, 311L));
        commentsFromService.add(createComment(211L, 21L));
    }

    private Comment createComment(Long commentId, Long parentCommentId) {
        Comment comment = new Comment(2L, parentCommentId, "author" + commentId
                , "content" + commentId, LocalDateTime.now());
        setCommentId(comment, commentId);
        return comment;
    }

    private void setCommentId(Comment comment, Long id) {
        try {
            Field commentIdField = comment.getClass().getDeclaredField("id");
            commentIdField.setAccessible(true);
            commentIdField.set(comment, id);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            throw new IllegalStateException("Failed to set id=" + id + " for " + comment, ex);
        }
    }

}
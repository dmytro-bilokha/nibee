package com.dmytrobilokha.nibee.service.comment;

import com.dmytrobilokha.nibee.dao.comment.CommentDao;
import com.dmytrobilokha.nibee.data.Comment;
import com.dmytrobilokha.nibee.service.post.PostService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Stateless
public class CommentServiceImpl implements CommentService {

    private static final int CONTENT_MAX_LENGTH = 5000;

    private CommentDao commentDao;
    private PostService postService;
    private NicknameValidator nicknameValidator;

    public CommentServiceImpl() {
        //This constructor is required by the EJB spec
    }

    @Inject
    public CommentServiceImpl(CommentDao commentDao, PostService postService, NicknameValidator nicknameValidator) {
        this.commentDao = commentDao;
        this.postService = postService;
        this.nicknameValidator = nicknameValidator;
    }

    @Override
    public List<Comment> fetchPostComments(Long postId) {
        return commentDao.findCommentByPostId(postId);
    }

    @Override
    public boolean doesPostCommentExist(Long postId, Long commentId) {
        return commentDao.countPostComments(postId, commentId) > 0;
    }

    @Override
    public Comment createAndSave(Long postId, Long parentCommentId, String authorNickname, String content)
            throws CommentCreationException {
        validate(postId, parentCommentId, authorNickname, content);
        Comment comment = new Comment(postId, parentCommentId, authorNickname, content, LocalDateTime.now());
        commentDao.insert(comment);
        return comment;
    }

    private void validate(Long postId, Long parentCommentId, String nickname, String content)
            throws CommentCreationException {
        List<String> validationErrorMessages = new ArrayList<>();
        validationErrorMessages.addAll(nicknameValidator.checkNickname(nickname));
        validationErrorMessages.addAll(checkContent(content));
        validationErrorMessages.addAll(checkParentsId(postId, parentCommentId));
        if (!validationErrorMessages.isEmpty()) {
            throw new CommentCreationException(validationErrorMessages);
        }
    }

    private List<String> checkContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return Collections.singletonList("Content should not be empty");
        }
        if (content.length() > CONTENT_MAX_LENGTH) {
            return Collections.singletonList("Content should not exceed " + CONTENT_MAX_LENGTH + " characters");
        }
        return Collections.emptyList();
    }

    private List<String> checkParentsId(Long postId, Long parentCommentId) {
        if (parentCommentId == null) {
            if (!postService.doesPostExist(postId)) {
                return Collections.singletonList("Unable to create comment for the post with id '" + postId
                        + "', because such post doesn't exist");
            }
        } else if (!doesPostCommentExist(postId, parentCommentId)) {
            return Collections.singletonList("Unable to create replay for the comment with id '"
                    + parentCommentId + "' under the post with id '" + postId
                    + "', because such comment/post doesn't exist");
        }
        return Collections.emptyList();
    }

}

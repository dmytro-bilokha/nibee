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

    private static final int NICKNAME_MAX_LENGTH = 25;
    private static final int CONTENT_MAX_LENGTH = 5000;

    private CommentDao commentDao;
    private PostService postService;

    public CommentServiceImpl() {
        //This constructor is required by the EJB spec
    }

    @Inject
    public CommentServiceImpl(CommentDao commentDao, PostService postService) {
        this.commentDao = commentDao;
        this.postService = postService;
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
        String nickname = authorNickname == null ? null : authorNickname.trim();
        validate(postId, parentCommentId, nickname, content);
        Comment comment = new Comment(postId, parentCommentId, nickname, content, LocalDateTime.now());
        commentDao.insert(comment);
        return comment;
    }

    private void validate(Long postId, Long parentCommentId, String nickname, String content)
            throws CommentCreationException {
        List<String> validationErrorMessages = new ArrayList<>();
        validationErrorMessages.addAll(checkAuthorNickname(nickname));
        validationErrorMessages.addAll(checkContent(content));
        validationErrorMessages.addAll(checkParentsId(postId, parentCommentId));
        if (!validationErrorMessages.isEmpty()) {
            throw new CommentCreationException(validationErrorMessages);
        }
    }

    private List<String> checkAuthorNickname(String authorNickname) {
        if (authorNickname == null || authorNickname.isEmpty()) {
            return Collections.singletonList("Author nickname should not be empty");
        }
        if (authorNickname.length() > NICKNAME_MAX_LENGTH) {
            return Collections.singletonList("Author nickname should not exceed "
                    + NICKNAME_MAX_LENGTH + " characters");
        }
        return Collections.emptyList();
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
        if (parentCommentId != null) {
            if (!doesPostCommentExist(postId, parentCommentId)) {
                return Collections.singletonList("Unable to create replay for the comment with id '"
                + parentCommentId + "' under the post with id '" + postId
                        + "', because such comment/post doesn't exist");
            }
        } else if (!postService.doesPostExist(postId)) {
            return Collections.singletonList("Unable to create comment for the post with id '" + postId
                    + "', because such post doesn't exist");
        }
        return Collections.emptyList();
    }

}

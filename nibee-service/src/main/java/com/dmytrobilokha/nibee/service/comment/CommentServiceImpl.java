package com.dmytrobilokha.nibee.service.comment;

import com.dmytrobilokha.nibee.dao.comment.CommentDao;
import com.dmytrobilokha.nibee.data.Comment;
import com.dmytrobilokha.nibee.service.post.PostService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.time.LocalDateTime;

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
    public Comment createAndSave(Long postId, String authorNickname, String content) throws CommentCreationException {
        String nickname = authorNickname == null ? null : authorNickname.trim();
        validateAuthorNickname(nickname);
        validateContent(content);
        validatePostId(postId);
        Comment comment = new Comment(postId, nickname, content, LocalDateTime.now());
        commentDao.insert(comment);
        return comment;
    }

    private void validateAuthorNickname(String authorNickname) throws CommentCreationException {
        if (authorNickname == null || authorNickname.isEmpty()) {
            throw new CommentCreationException("Author nickname should not be null or empty");
        }
        if (authorNickname.length() > NICKNAME_MAX_LENGTH) {
            throw new CommentCreationException("Author nickname should not exceed "
                    + NICKNAME_MAX_LENGTH + " characters");
        }
    }

    private void validateContent(String content) throws CommentCreationException {
        if (content == null || content.trim().isEmpty()) {
            throw new CommentCreationException("Content should not be null or empty");
        }
        if (content.length() > CONTENT_MAX_LENGTH) {
            throw new CommentCreationException("Content should not exceed "
                    + CONTENT_MAX_LENGTH + " characters");
        }
    }

    private void validatePostId(Long postId) throws CommentCreationException {
        if (!postService.doesPostExist(postId)) {
            throw new CommentCreationException("Unable to create comment for the post with id '" + postId
                    + "', because such post doesn't exist");
        }
    }

}

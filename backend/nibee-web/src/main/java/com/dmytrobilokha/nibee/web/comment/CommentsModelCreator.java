package com.dmytrobilokha.nibee.web.comment;

import com.dmytrobilokha.nibee.data.Comment;
import com.dmytrobilokha.nibee.service.comment.CommentService;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Dependent
public class CommentsModelCreator {

    private final CommentService commentService;

    @Inject
    public CommentsModelCreator(CommentService commentService) {
        this.commentService = commentService;
    }

    public void createAndPutInRequest(Long postId, boolean showNewCommentForm, HttpServletRequest req) {
        List<CommentModel> comments = createCommentModels(commentService.fetchPostComments(postId));
        CommentsModel model = new CommentsModel(postId, showNewCommentForm, comments);
        model.putInRequest(req);
    }

    private List<CommentModel> createCommentModels(List<Comment> comments) {
        Map<Long, List<Comment>> commentsByParentCommentId = groupCommentsByParentCommentId(comments);
        List<CommentModel> models = new ArrayList<>(comments.size());
        List<Comment> rootComments = commentsByParentCommentId.getOrDefault(null, Collections.emptyList());
        for (Comment rootComment : rootComments) {
            appendCommentAndChildren(models, rootComment, commentsByParentCommentId);
        }
        return models;
    }

    private Map<Long, List<Comment>> groupCommentsByParentCommentId(List<Comment> comments) {
        Map<Long, List<Comment>> commentsByParentCommentId = new HashMap<>();
        for (Comment comment : comments) {
            List<Comment> commentGroup = commentsByParentCommentId
                    .computeIfAbsent(comment.getParentCommentId(), x -> new ArrayList<>());
            commentGroup.add(comment);
        }
        return commentsByParentCommentId;
    }

    private void appendCommentAndChildren(
            List<CommentModel> models, Comment parentComment, Map<Long, List<Comment>> commentsByParentCommentId) {
        Deque<Iterator<Comment>> iteratorDeque = new ArrayDeque<>();
        Iterator<Comment> currentIterator = Collections.singletonList(parentComment).iterator();
        int depth = 0;
        while (currentIterator != null) {
            Comment currentComment = currentIterator.next();
            models.add(new CommentModel(currentComment, depth));
            List<Comment> currentCommentChildren = commentsByParentCommentId
                    .getOrDefault(currentComment.getId(), Collections.emptyList());
            if (!currentCommentChildren.isEmpty()) {
                iteratorDeque.offerFirst(currentIterator);
                currentIterator = currentCommentChildren.iterator();
                depth++;
            }
            while (currentIterator != null && !currentIterator.hasNext()) {
                currentIterator = iteratorDeque.pollFirst();
                depth--;
            }
        }
    }

}

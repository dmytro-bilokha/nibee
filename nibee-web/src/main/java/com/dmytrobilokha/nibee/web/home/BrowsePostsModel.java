package com.dmytrobilokha.nibee.web.home;

import com.dmytrobilokha.nibee.data.Post;
import com.dmytrobilokha.nibee.data.Tag;
import com.dmytrobilokha.nibee.web.HeadlinePostModel;
import com.dmytrobilokha.nibee.web.InRequestPuttable;
import com.dmytrobilokha.nibee.web.param.InvalidParamException;
import com.dmytrobilokha.nibee.web.param.ParamParser;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class BrowsePostsModel implements InRequestPuttable {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("uu-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH);

    private final List<HeadlinePostModel> headlines;
    private final NavigationType navigationType;
    private final String backParam;
    private final String forwardParam;
    private final Tag tag;

    BrowsePostsModel(List<Post> posts, NavigationType navigationType) {
        this(posts, navigationType, null);
    }

    BrowsePostsModel(List<Post> posts, NavigationType navigationType, Tag tag) {
        validate(posts, navigationType);
        this.navigationType = navigationType;
        this.headlines = Collections.unmodifiableList(
                posts.stream()
                .map(HeadlinePostModel::new)
                .collect(Collectors.toList())
        );
        this.backParam = navigationType.back ? convertLastTouch(posts.get(0)) : "";
        this.forwardParam = navigationType.forward ? convertLastTouch(posts.get(posts.size() - 1)) : "";
        this.tag = tag;
    }

    static Optional<LocalDateTime> extractBeforeParam(HttpServletRequest request) throws InvalidParamException {
        return ParamParser.parseDateTime(request, "before", DATE_TIME_FORMATTER);
    }

    static Optional<LocalDateTime> extractAfterParam(HttpServletRequest request) throws InvalidParamException {
        return ParamParser.parseDateTime(request, "after", DATE_TIME_FORMATTER);
    }

    static Optional<Long> extractTagIdParam(HttpServletRequest request) throws InvalidParamException {
        return ParamParser.parseLong(request, "tagId");
    }

    private void validate(List<Post> posts, NavigationType navigationType) {
        if (posts.isEmpty()) {
            throw new IllegalStateException("Unable to have navigation possible for empty posts list");
        }
        if (navigationType == NavigationType.BACK_AND_FORWARD && posts.size() < 2) {
            throw new IllegalStateException("Unable to have both back and forward possible for posts list with"
                    + " size less than two: " + posts);
        }
    }

    private String convertLastTouch(Post post) {
        return DATE_TIME_FORMATTER.format(post.getLastTouch());
    }

    public List<HeadlinePostModel> getHeadlines() {
        return headlines;
    }

    public boolean isBackPossible() {
        return navigationType.back;
    }

    public String getBackParam() {
        return backParam;
    }

    public boolean isForwardPossible() {
        return navigationType.forward;
    }

    public String getForwardParam() {
        return forwardParam;
    }

    public boolean isFilteredByTag() {
        return tag != null;
    }

    public Tag getTag() {
        return tag;
    }

    public enum NavigationType {
        NO(false, false), BACK(true, false), FORWARD(false, true), BACK_AND_FORWARD(true, true);

        private final boolean back;
        private final boolean forward;

        NavigationType(boolean back, boolean forward) {
            this.back = back;
            this.forward = forward;
        }
    }
}

package com.dmytrobilokha.nibee.web.home;

import com.dmytrobilokha.nibee.data.Post;
import com.dmytrobilokha.nibee.web.AbstractModel;
import com.dmytrobilokha.nibee.web.HeadlinePostModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class BrowsePostsModel extends AbstractModel {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("uu-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH);

    private final List<HeadlinePostModel> headlines;
    private final boolean backPossible;
    private final String backParam;
    private final boolean forwardPossible;
    private final String forwardParam;

    BrowsePostsModel(List<Post> posts, boolean backPossible, boolean forwardPossible) {
        validate(posts, backPossible, forwardPossible);
        this.backPossible = backPossible;
        this.forwardPossible = forwardPossible;
        this.headlines = Collections.unmodifiableList(
                posts.stream()
                .map(HeadlinePostModel::new)
                .collect(Collectors.toList())
        );
        this.backParam = extractTouchParam(backPossible, posts.get(0));
        this.forwardParam = extractTouchParam(forwardPossible, posts.get(posts.size() - 1));
    }

    static Optional<LocalDateTime> parseDateTimeParam(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(LocalDateTime.parse(dateTimeString, DATE_TIME_FORMATTER));
        } catch (DateTimeParseException ex) {
            return Optional.empty();
        }
    }

    static Optional<Long> parseLongParam(String longValueString) {
        if (longValueString == null || longValueString.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(Long.valueOf(longValueString, 10));
        } catch (NumberFormatException ex) {
            return Optional.empty(); //TODO: throw custom exception here
        }
    }

    private String extractTouchParam(boolean possible, Post post) {
        if (!possible) {
            return "";
        }
        return convertLastTouch(post);
    }

    private void validate(List<Post> posts, boolean backPossible, boolean forwardPossible) {
        if (backPossible && forwardPossible && posts.size() < 2) {
            throw new IllegalStateException("Unable to have both back and forward possible for posts list with"
                    + " size less than two: " + posts);
        }
        if ((backPossible || forwardPossible) && posts.isEmpty()) {
            throw new IllegalStateException("Unable to have " + (backPossible ? "back" : "forward")
                    + " possible for empty posts list");
        }
    }

    private String convertLastTouch(Post post) {
        return DATE_TIME_FORMATTER.format(post.getLastTouch());
    }

    public List<HeadlinePostModel> getHeadlines() {
        return headlines;
    }

    public boolean isBackPossible() {
        return backPossible;
    }

    public String getBackParam() {
        return backParam;
    }

    public boolean isForwardPossible() {
        return forwardPossible;
    }

    public String getForwardParam() {
        return forwardParam;
    }

}

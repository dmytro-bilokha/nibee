package com.dmytrobilokha.nibee.data;

import java.time.LocalDateTime;
import java.util.Objects;

public class Comment {

    private Long id;
    private Long postId;
    private String authorNickname;
    private String content;
    private LocalDateTime createdOn;

    Comment() {
        //This constructor is required by MyBatis
    }

    public Comment(Long postId, String authorNickname, String content) {
        this.postId = postId;
        this.authorNickname = authorNickname;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public Long getPostId() {
        return postId;
    }

    public String getAuthorNickname() {
        return authorNickname;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(id, comment.id) &&
                Objects.equals(authorNickname, comment.authorNickname) &&
                Objects.equals(content, comment.content) &&
                Objects.equals(createdOn, comment.createdOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, authorNickname, createdOn);
    }

}

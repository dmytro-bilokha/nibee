package com.dmytrobilokha.nibee.data;

import java.time.LocalDateTime;
import java.util.Objects;

public class Post {

    private Long id;
    private String name;
    private String title;
    private String path;
    private boolean shareable;
    private boolean commentAllowed;
    private LocalDateTime createdOn;
    private LocalDateTime modifiedOn;

    Post() {
        //Constructor for myBatis
    }

    public Post(String name, String title, String path, boolean shareable, boolean commentAllowed) {
        this.name = name;
        this.title = title;
        this.path = path;
        this.shareable = shareable;
        this.commentAllowed = commentAllowed;
        this.createdOn = LocalDateTime.MAX;
    }

    public Post(String name, String path, LocalDateTime createdOn) {
        this.name = name;
        this.path = path;
        this.createdOn = createdOn;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }

    public boolean isShareable() {
        return shareable;
    }

    public boolean isCommentAllowed() {
        return commentAllowed;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public LocalDateTime getModifiedOn() {
        return modifiedOn;
    }

    public LocalDateTime getLastTouch() {
        return modifiedOn == null ? createdOn : modifiedOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(id, post.id)
                && Objects.equals(name, post.name)
                && Objects.equals(title, post.title)
                && Objects.equals(path, post.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Post{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", title='" + title + '\''
                + ", path='" + path + '\''
                + ", shareable=" + shareable
                + ", commentAllowed=" + commentAllowed
                + ", createdOn=" + createdOn
                + ", modifiedOn=" + modifiedOn
                + '}';
    }

}

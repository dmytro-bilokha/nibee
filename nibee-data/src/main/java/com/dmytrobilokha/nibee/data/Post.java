package com.dmytrobilokha.nibee.data;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Post {

    private Long id;
    private String name;
    private String path;
    private LocalDateTime createdOn;
    private LocalDateTime modifiedOn;
    private List<Tag> tagList;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public LocalDateTime getModifiedOn() {
        return modifiedOn;
    }

    public List<Tag> getTags() {
        if (tagList == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(tagList);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(id, post.id) &&
                Objects.equals(name, post.name) &&
                Objects.equals(path, post.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", createdOn=" + createdOn +
                ", modifiedOn=" + modifiedOn +
                ", tagList=" + tagList +
                '}';
    }

}

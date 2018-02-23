package com.dmytrobilokha.nibee.data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
            return new ArrayList<>();
        }
        return new ArrayList<>(tagList);
    }

}

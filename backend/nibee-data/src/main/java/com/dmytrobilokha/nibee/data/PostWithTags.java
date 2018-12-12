package com.dmytrobilokha.nibee.data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class PostWithTags extends Post {

    private List<Tag> tagList;

    PostWithTags() {
        //This constructor is required for MyBatis
    }

    public PostWithTags(String name, String path, Set<Tag> tagSet, LocalDateTime createdOn) {
        super(name, path, createdOn);
        this.tagList = new ArrayList<>(tagSet);
    }

    public List<Tag> getTags() {
        if (tagList == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(tagList);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        PostWithTags that = (PostWithTags) o;
        return Objects.equals(tagList, that.tagList);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "PostWithTags{"
                + "id=" + getId()
                + ", name='" + getName() + '\''
                + ", title='" + getTitle() + '\''
                + ", path='" + getPath() + '\''
                + ", shareable=" + isShareable()
                + ", commentAllowed=" + isCommentAllowed()
                + ", createdOn=" + getCreatedOn()
                + ", modifiedOn=" + getModifiedOn()
                + ", tagList=" + tagList
                + '}';
    }

}

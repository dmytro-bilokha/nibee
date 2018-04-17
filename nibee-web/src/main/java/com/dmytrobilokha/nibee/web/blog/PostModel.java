package com.dmytrobilokha.nibee.web.blog;

import com.dmytrobilokha.nibee.data.Post;
import com.dmytrobilokha.nibee.data.Tag;
import com.dmytrobilokha.nibee.web.AbstractModel;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Locale;

public class PostModel extends AbstractModel {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("dd MMM uuuu", Locale.ENGLISH);

    private final String entryFileUrl;
    private final String contentBase;
    private final Collection<Tag> tags;
    private final String createdOn;
    private final String modifiedOn;

    PostModel(String entryFileUrl, String contentBase, Post post) {
        this.entryFileUrl = entryFileUrl;
        this.contentBase = contentBase;
        this.tags = post.getTags();
        this.createdOn = DATE_TIME_FORMATTER.format(post.getCreatedOn());
        if (post.getModifiedOn() == null) {
            this.modifiedOn = "";
        } else {
            this.modifiedOn = DATE_TIME_FORMATTER.format(post.getModifiedOn());
        }
    }

    public boolean isPostSharable() {
        return !tags.isEmpty();
    }

    public String getEntryFileUrl() {
        return entryFileUrl;
    }

    public String getContentBase() {
        return contentBase;
    }

    public Collection<Tag> getTags() {
        return tags;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public String getModifiedOn() {
        return modifiedOn;
    }

}

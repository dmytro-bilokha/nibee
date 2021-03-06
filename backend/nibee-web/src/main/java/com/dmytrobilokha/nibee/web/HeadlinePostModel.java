package com.dmytrobilokha.nibee.web;

import com.dmytrobilokha.nibee.data.PostWithTags;
import com.dmytrobilokha.nibee.data.Tag;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Locale;

public class HeadlinePostModel implements InRequestPuttable {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("dd MMM uuuu", Locale.ENGLISH);

    private final String name;
    private final String title;
    private final boolean shareable;
    private final Collection<Tag> tags;
    private final String createdOn;
    private final String modifiedOn;

    public HeadlinePostModel(PostWithTags post) {
        this.name = post.getName();
        this.title = post.getTitle();
        this.shareable = post.isShareable();
        this.tags = post.getTags();
        this.createdOn = DATE_TIME_FORMATTER.format(post.getCreatedOn());
        if (post.getModifiedOn() == null) {
            this.modifiedOn = "";
        } else {
            this.modifiedOn = DATE_TIME_FORMATTER.format(post.getModifiedOn());
        }
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public boolean isShareable() {
        return shareable;
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

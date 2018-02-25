package com.dmytrobilokha.nibee.web.blog;

import com.dmytrobilokha.nibee.data.Tag;
import com.dmytrobilokha.nibee.web.AbstractModel;

import java.util.Collection;

public class PostModel extends AbstractModel {

    private final String entryFileUrl;
    private final Collection<Tag> tags;

    PostModel(String entryFileUrl, Collection<Tag> tags) {
        this.entryFileUrl = entryFileUrl;
        this.tags = tags;
    }

    public String getEntryFileUrl() {
        return entryFileUrl;
    }

    public Collection<Tag> getTags() {
        return tags;
    }

}

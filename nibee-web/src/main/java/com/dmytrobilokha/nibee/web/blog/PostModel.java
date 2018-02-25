package com.dmytrobilokha.nibee.web.blog;

import com.dmytrobilokha.nibee.data.Tag;
import com.dmytrobilokha.nibee.web.AbstractModel;

import java.util.Collection;

public class PostModel extends AbstractModel {

    private final String entryFileUrl;
    private final String contentBase;
    private final Collection<Tag> tags;

    PostModel(String entryFileUrl, String contentBase, Collection<Tag> tags) {
        this.entryFileUrl = entryFileUrl;
        this.contentBase = contentBase;
        this.tags = tags;
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

}

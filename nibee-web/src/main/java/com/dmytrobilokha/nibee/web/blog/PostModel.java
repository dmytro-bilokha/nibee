package com.dmytrobilokha.nibee.web.blog;

import com.dmytrobilokha.nibee.data.Post;
import com.dmytrobilokha.nibee.web.HeadlinePostModel;

public class PostModel extends HeadlinePostModel {

    private final String entryFileUrl;
    private final String contentBase;

    PostModel(String entryFileUrl, String contentBase, Post post) {
        super(post);
        this.entryFileUrl = entryFileUrl;
        this.contentBase = contentBase;
    }

    public boolean isPostSharable() {
        return !getTags().isEmpty();
    }

    public String getEntryFileUrl() {
        return entryFileUrl;
    }

    public String getContentBase() {
        return contentBase;
    }

}

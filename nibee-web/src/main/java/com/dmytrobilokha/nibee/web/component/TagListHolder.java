package com.dmytrobilokha.nibee.web.component;

import com.dmytrobilokha.nibee.data.Tag;
import com.dmytrobilokha.nibee.service.tag.TagService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@RequestScoped
@Named
public class TagListHolder {

    private final TagService tagService;
    private List<Tag> tags;

    @Inject
    public TagListHolder(TagService tagService) {
        this.tagService = tagService;
    }

    public List<Tag> getTags() {
        if (tags == null) {
            tags = tagService.getAll();
        }
        return tags;
    }

}

package com.dmytrobilokha.nibee.web.admin.managepost;

import com.dmytrobilokha.nibee.data.Tag;
import com.dmytrobilokha.nibee.service.tag.TagService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("tags")
public class TagResource {

    private final TagService tagService;

    @Inject
    public TagResource(TagService tagService) {
        this.tagService = tagService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Tag> getAll() {
        return tagService.getAll();
    }

}

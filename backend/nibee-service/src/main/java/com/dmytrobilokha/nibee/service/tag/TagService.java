package com.dmytrobilokha.nibee.service.tag;

import com.dmytrobilokha.nibee.data.Tag;

import java.util.List;
import java.util.Set;

public interface TagService {

    List<Tag> getAll();

    void assignTagsToPost(Long postId, Set<Long> tagIds);
}

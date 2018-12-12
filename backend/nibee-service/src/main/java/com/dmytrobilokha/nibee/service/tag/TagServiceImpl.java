package com.dmytrobilokha.nibee.service.tag;

import com.dmytrobilokha.nibee.dao.tag.TagDao;
import com.dmytrobilokha.nibee.data.Tag;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.Set;

@Stateless
public class TagServiceImpl implements TagService {

    private TagDao tagDao;

    public TagServiceImpl() {
        //Required by EJB spec
    }

    @Inject
    public TagServiceImpl(TagDao tagDao) {
        this.tagDao = tagDao;
    }

    @Override
    public List<Tag> getAll() {
        return tagDao.getAll();
    }

    @Override
    public void assignTagsToPost(Long postId, Set<Long> tagIds) {
        for (Long tagId : tagIds) {
            tagDao.assignTagToPost(postId, tagId);
        }
    }

}

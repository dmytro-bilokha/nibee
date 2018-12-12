package com.dmytrobilokha.nibee.service.post;

import com.dmytrobilokha.nibee.dao.post.PostDao;
import com.dmytrobilokha.nibee.data.Post;
import com.dmytrobilokha.nibee.data.PostWithTags;
import com.dmytrobilokha.nibee.data.Tag;
import com.dmytrobilokha.nibee.service.config.ConfigProperty;
import com.dmytrobilokha.nibee.service.config.ConfigService;
import com.dmytrobilokha.nibee.service.file.FileService;
import com.dmytrobilokha.nibee.service.tag.TagService;

import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Stateless
public class PostServiceImpl implements PostService {

    private static final String UPLOAD_DIR = "_UPLOADS_";

    private PostDao postDao;
    private TagService tagService;
    private FileService fileService;
    private ConfigService configService;

    public PostServiceImpl() {
        //EJB spec required constructor
    }

    @Inject
    public PostServiceImpl(
            PostDao postDao
            , TagService tagService
            , FileService fileService
            , ConfigService configService
    ) {
        this.postDao = postDao;
        this.tagService = tagService;
        this.fileService = fileService;
        this.configService = configService;
    }

    @Override
    public PostWithTags findPostByName(String name) {
        return postDao.findPostByName(name);
    }

    @Override
    public String findPostPathByName(String name) {
        return postDao.findPostPathByName(name);
    }

    @Override
    public List<PostWithTags> findPostAfter(LocalDateTime dateTime, int limit) {
        return postDao.findPostAfter(dateTime, null, limit);
    }

    @Override
    public List<PostWithTags> findPostBefore(LocalDateTime dateTime, int limit) {
        return postDao.findPostBefore(dateTime, null, limit);
    }

    @Override
    public List<PostWithTags> findPostAfterFilteredByTag(LocalDateTime dateTime, Long tagId, int limit) {
        return postDao.findPostAfter(dateTime, tagId, limit);
    }

    @Override
    public List<PostWithTags> findPostBeforeFilteredByTag(LocalDateTime dateTime, Long tagId, int limit) {
        return postDao.findPostBefore(dateTime, tagId, limit);
    }

    @Override
    public boolean doesPostExist(Long postId) {
        return postDao.countPostsById(postId) > 0;
    }

    @Override
    public void createPost(
            InputStream postFileInputStream
            , Post post
            , Set<Long> tagIds
    ) throws IllegalPostDataException {
        validatePostUniqueness(post);
        validateTagsExistence(tagIds);
        postDao.createPost(post);
        tagService.assignTagsToPost(post.getId(), tagIds);
        Path uploadPostDir = unzipPostAssets(post.getPath(), postFileInputStream);
        deployPostAssets(post.getPath(), uploadPostDir);
    }

    private void validatePostUniqueness(Post post) throws IllegalPostDataException {
        List<Post> duplicates = postDao.findPostDuplicates(post);
        if (duplicates.isEmpty()) {
            return;
        }
        String duplicatesData = duplicates.stream()
                .map(p -> '(' + p.getName() + ", " + p.getPath() + ')')
                .collect(Collectors.joining(", "));
        throw new IllegalPostDataException("Found duplicates for post: " + duplicatesData);
    }

    private void validateTagsExistence(Set<Long> tagIds) throws IllegalPostDataException {
        Set<Long> allTagIds = tagService.getAll()
                .stream()
                .map(Tag::getId)
                .collect(Collectors.toSet());
        List<Long> nonExistingTagIds = tagIds.stream()
                .filter(id -> !allTagIds.contains(id))
                .collect(Collectors.toList());
        if (!nonExistingTagIds.isEmpty()) {
            throw new IllegalPostDataException("Assigned non-existing tag id(s): " + nonExistingTagIds);
        }
    }

    private Path unzipPostAssets(String postPath, InputStream postFileInputStream) throws IllegalPostDataException {
        Path uploadPostDir = Paths.get(
                configService.getAsString(ConfigProperty.POSTS_ROOT)
                , UPLOAD_DIR
                , postPath + '_' + UUID.randomUUID());
        try {
            fileService.unzipStreamToDir(postFileInputStream, uploadPostDir);
        } catch (IOException ex) {
            throw new IllegalPostDataException("Failed to unzip post assets file", ex);
            //Throw exception about bad zip format
        }
        return uploadPostDir;
    }

    private void deployPostAssets(String postPath, Path uploadPostDir) {
        Path deployPostDir = Paths.get(configService.getAsString(ConfigProperty.POSTS_ROOT), postPath);
        try {
            fileService.renameAtomically(uploadPostDir, deployPostDir);
        } catch (IOException ex) {
            throw new EJBException("Failed to move post assets from upload "
                    + uploadPostDir + " to deployment " + deployPostDir, ex);
        }
    }

}

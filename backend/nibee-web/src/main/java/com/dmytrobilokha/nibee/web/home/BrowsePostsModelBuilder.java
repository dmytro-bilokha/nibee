package com.dmytrobilokha.nibee.web.home;

import com.dmytrobilokha.nibee.data.PostWithTags;
import com.dmytrobilokha.nibee.data.Tag;
import com.dmytrobilokha.nibee.service.post.PostService;
import com.dmytrobilokha.nibee.web.home.BrowsePostsModel.NavigationType;

import java.time.LocalDateTime;
import java.util.List;

class BrowsePostsModelBuilder {

    private static final LocalDateTime THE_END_OF_TIME = LocalDateTime.of(3000, 1, 1, 0, 1);

    private final PostService postService;
    private final int headlinersPerPage;
    private Long tagId;
    private LocalDateTime before;
    private LocalDateTime after;

    BrowsePostsModelBuilder(PostService postService, int headlinersPerPage) {
        this.postService = postService;
        this.headlinersPerPage = headlinersPerPage;
    }

    BrowsePostsModelBuilder tagId(Long tagId) {
        this.tagId = tagId;
        return this;
    }

    BrowsePostsModelBuilder before(LocalDateTime before) {
        this.before = before;
        return this;
    }

    BrowsePostsModelBuilder after(LocalDateTime after) {
        this.after = after;
        return this;
    }

    BrowsePostsModel build() {
        if (before == null && after == null) {
            before = THE_END_OF_TIME;
        }
        MainModelData modelSeed;
        if (before == null) {
            modelSeed = fetchMainModelDataForAfterCase(after, tagId);
        } else {
            modelSeed = fetchMainModelDataForBeforeCase(before, tagId);
        }
        if (modelSeed.posts.isEmpty()) {
            return null;
        }
        if (tagId == null) {
            return new BrowsePostsModel(modelSeed.posts, modelSeed.navigationType);
        } else {
            return new BrowsePostsModel(modelSeed.posts, modelSeed.navigationType
                    , findTagById(modelSeed.posts.get(0), tagId));
        }
    }

    private MainModelData fetchMainModelDataForBeforeCase(LocalDateTime beforeParam, Long tagIdParam) {
        List<PostWithTags> posts;
        NavigationType navigationType;
        if (tagIdParam == null) {
            posts = postService.findPostBefore(beforeParam, headlinersPerPage + 1);
        } else {
            posts = postService.findPostBeforeFilteredByTag(beforeParam, tagIdParam, headlinersPerPage + 1);
        }
        if (posts.size() > headlinersPerPage) {
            navigationType = THE_END_OF_TIME.equals(beforeParam)
                    ? NavigationType.FORWARD : NavigationType.BACK_AND_FORWARD;
            posts = posts.subList(0, headlinersPerPage);
        } else {
            navigationType = THE_END_OF_TIME.equals(beforeParam) ? NavigationType.NO : NavigationType.BACK;
        }
        return new MainModelData(posts, navigationType);
    }

    private MainModelData fetchMainModelDataForAfterCase(LocalDateTime afterParam, Long tagIdParam) {
        List<PostWithTags> posts;
        NavigationType navigationType;
        if (tagIdParam == null) {
            posts = postService.findPostAfter(afterParam, headlinersPerPage + 1);
        } else {
            posts = postService.findPostAfterFilteredByTag(afterParam, tagIdParam, headlinersPerPage + 1);
        }
        if (posts.size() > headlinersPerPage) {
            navigationType = NavigationType.BACK_AND_FORWARD;
            posts = posts.subList(1, posts.size());
        } else {
            navigationType = NavigationType.FORWARD;
        }
        return new MainModelData(posts, navigationType);
    }

    private Tag findTagById(PostWithTags post, Long id) {
        return post.getTags()
                .stream()
                .filter(t -> id.equals(t.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Tag with id=" + id + " not found for " + post));
    }

    private static class MainModelData {
        private final List<PostWithTags> posts;
        private final NavigationType navigationType;

        private MainModelData(List<PostWithTags> posts, NavigationType navigationType) {
            this.posts = posts;
            this.navigationType = navigationType;
        }
    }
}

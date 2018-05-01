package com.dmytrobilokha.nibee.web.home;

import com.dmytrobilokha.nibee.data.Post;
import com.dmytrobilokha.nibee.data.Tag;
import com.dmytrobilokha.nibee.service.post.PostService;
import com.dmytrobilokha.nibee.web.home.BrowsePostsModel.NavigationType;

import java.time.LocalDateTime;
import java.util.List;

class BrowsePostsModelBuilder {

    private static final LocalDateTime THE_END_OF_TIME = LocalDateTime.of(3000, 1, 1, 0, 1);
    private static final int HEADLINERS_PER_PAGE = 2;

    private final PostService postService;
    private Long tagId;
    private LocalDateTime before;
    private LocalDateTime after;

    BrowsePostsModelBuilder(PostService postService) {
        this.postService = postService;
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
        if (before != null) {
            modelSeed = fetchMainModelDataForBeforeCase(before, tagId);
        } else {
            modelSeed = fetchMainModelDataForAfterCase(after, tagId);
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
        List<Post> posts;
        NavigationType navigationType;
        if (tagIdParam != null) {
            posts = postService.findPostBeforeFilteredByTag(beforeParam, tagIdParam, HEADLINERS_PER_PAGE + 1);
        } else {
            posts = postService.findPostBefore(beforeParam, HEADLINERS_PER_PAGE + 1);
        }
        if (posts.size() > HEADLINERS_PER_PAGE) {
            navigationType = beforeParam == THE_END_OF_TIME ? NavigationType.FORWARD : NavigationType.BACK_AND_FORWARD;
            posts = posts.subList(0, HEADLINERS_PER_PAGE);
        } else {
            navigationType = beforeParam == THE_END_OF_TIME ? NavigationType.NO : NavigationType.BACK;
        }
        return new MainModelData(posts, navigationType);
    }

    private MainModelData fetchMainModelDataForAfterCase(LocalDateTime afterParam, Long tagIdParam) {
        List<Post> posts;
        NavigationType navigationType;
        if (tagIdParam != null) {
            posts = postService.findPostAfterFilteredByTag(afterParam, tagIdParam, HEADLINERS_PER_PAGE + 1);
        } else {
            posts = postService.findPostAfter(afterParam, HEADLINERS_PER_PAGE + 1);
        }
        if (posts.size() > HEADLINERS_PER_PAGE) {
            navigationType = NavigationType.BACK_AND_FORWARD;
            posts = posts.subList(1, posts.size());
        } else {
            navigationType = NavigationType.FORWARD;
        }
        return new MainModelData(posts, navigationType);
    }

    private Tag findTagById(Post post, Long id) {
        return post.getTags()
                .stream()
                .filter(t -> id.equals(t.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Tag with id=" + id + " not found for " + post));
    }

    private static class MainModelData {
        private final List<Post> posts;
        private final NavigationType navigationType;

        private MainModelData(List<Post> posts, NavigationType navigationType) {
            this.posts = posts;
            this.navigationType = navigationType;
        }
    }
}

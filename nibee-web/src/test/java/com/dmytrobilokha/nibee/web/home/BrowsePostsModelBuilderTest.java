package com.dmytrobilokha.nibee.web.home;

import com.dmytrobilokha.nibee.data.Post;
import com.dmytrobilokha.nibee.data.Tag;
import com.dmytrobilokha.nibee.service.post.PostService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

public class BrowsePostsModelBuilderTest {

    private static final int HEADLINERS_PER_PAGE = 2;
    private static final int SEARCH_LIMIT = HEADLINERS_PER_PAGE + 1;

    private PostService mockPostService;
    private BrowsePostsModelBuilder modelBuilder;

    @Before
    public void init() {
        mockPostService = Mockito.mock(PostService.class);
        modelBuilder = new BrowsePostsModelBuilder(mockPostService, 2);
    }

    @Test
    public void checkReturnsNullOnEmptyPostList() {
        Mockito.when(mockPostService.findPostBefore(any(), eq(SEARCH_LIMIT))).thenReturn(Collections.emptyList());
        BrowsePostsModel model = modelBuilder.build();
        assertNull(model);
    }

    @Test
    public void checkSetsModelPropertiesForFirstPageShort() {
        List<Post> postList = Arrays.asList(createPost(1), createPost(2));
        Mockito.when(mockPostService.findPostBefore(any(), eq(SEARCH_LIMIT))).thenReturn(postList);
        BrowsePostsModel model = modelBuilder.build();
        assertNotNull(model);
        assertFalse(model.isBackPossible());
        assertFalse(model.isForwardPossible());
        assertFalse(model.isFilteredByTag());
        assertEquals(2, model.getHeadlines().size());
    }

    @Test
    public void checkSetsModelPropertiesForFirstPageLong() {
        List<Post> postList = Arrays.asList(createPost(1), createPost(2), createPost(3));
        Mockito.when(mockPostService.findPostBefore(any(), eq(SEARCH_LIMIT))).thenReturn(postList);
        BrowsePostsModel model = modelBuilder.build();
        assertNotNull(model);
        assertFalse(model.isBackPossible());
        assertTrue(model.isForwardPossible());
        assertFalse(model.isFilteredByTag());
        assertEquals(2, model.getHeadlines().size());
    }

    @Test
    public void checkSetsTagForFirstPage() {
        List<Post> postList = Arrays.asList(createPostWithTags(1), createPostWithTags(2));
        Mockito.when(mockPostService.findPostBeforeFilteredByTag(any(), eq(1L), eq(SEARCH_LIMIT))).thenReturn(postList);
        BrowsePostsModel model = modelBuilder
                .tagId(1L)
                .build();
        assertNotNull(model);
        assertTrue(model.isFilteredByTag());
        Tag tag = model.getTag();
        assertNotNull(tag);
        assertEquals(Long.valueOf(1L), tag.getId());
    }

    @Test
    public void checkSetsModelPropertiesForAfterShort() {
        List<Post> postList = Arrays.asList(createPost(1), createPost(2));
        Mockito.when(mockPostService.findPostAfter(any(), eq(SEARCH_LIMIT))).thenReturn(postList);
        BrowsePostsModel model = modelBuilder
                .after(LocalDateTime.of(2009, 10, 1, 1, 1))
                .build();
        assertNotNull(model);
        assertFalse(model.isBackPossible());
        assertTrue(model.isForwardPossible());
        assertFalse(model.isFilteredByTag());
        assertEquals(2, model.getHeadlines().size());
    }

    @Test
    public void checkSetsModelPropertiesForAfterPageLong() {
        List<Post> postList = Arrays.asList(createPost(1), createPost(2), createPost(3));
        Mockito.when(mockPostService.findPostAfter(any(), eq(SEARCH_LIMIT))).thenReturn(postList);
        BrowsePostsModel model = modelBuilder
                .after(LocalDateTime.of(2009, 10, 1, 1, 1))
                .build();
        assertNotNull(model);
        assertTrue(model.isBackPossible());
        assertTrue(model.isForwardPossible());
        assertFalse(model.isFilteredByTag());
        assertEquals(2, model.getHeadlines().size());
    }

    @Test
    public void checkSetsTagForAfter() {
        List<Post> postList = Arrays.asList(createPostWithTags(1), createPostWithTags(2));
        Mockito.when(mockPostService.findPostAfterFilteredByTag(any(), eq(1L), eq(SEARCH_LIMIT))).thenReturn(postList);
        BrowsePostsModel model = modelBuilder
                .tagId(1L)
                .after(LocalDateTime.of(2009, 10, 1, 1, 1))
                .build();
        assertNotNull(model);
        assertTrue(model.isFilteredByTag());
        Tag tag = model.getTag();
        assertNotNull(tag);
        assertEquals(Long.valueOf(1L), tag.getId());
    }

    private Post createPost(int seconds) {
        return new Post("Name", "path", Collections.emptySet(), LocalDateTime.of(2018, 4, 17, 6, seconds));
    }

    private Post createPostWithTags(int seconds) {
        List<Tag> tags = Arrays.asList(new Tag(1L, "First"), new Tag(2L, "Second"));
        return new Post("Name", "path", new HashSet<>(tags), LocalDateTime.of(2018, 4, 17, 6, seconds));
    }

}
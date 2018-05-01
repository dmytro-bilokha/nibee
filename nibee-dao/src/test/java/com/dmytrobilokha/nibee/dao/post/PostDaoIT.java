package com.dmytrobilokha.nibee.dao.post;

import com.dmytrobilokha.nibee.dao.AbstractDaoTest;
import com.dmytrobilokha.nibee.data.Post;
import com.dmytrobilokha.nibee.data.Tag;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PostDaoIT extends AbstractDaoTest {

    private PostDao postDao;

    @BeforeClass
    public static void loadData() {
        executeSqlScripts("post.sql");
    }

    @Before
    public void initPostRepository() {
        postDao = getMapper(PostDao.class);
    }

    @Test
    public void checkFindsPostByName() {
        Post post = postDao.findPostByName("post-about-rest");
        assertNotNull(post);
        assertEquals("post-about-rest", post.getName());
    }

    @Test
    public void checkFindsPostWithoutTagByName() {
        Post post = postDao.findPostByName("resources");
        assertNotNull(post);
        assertEquals("resources", post.getName());
    }

    @Test
    public void checkDoesntFindPost() {
        Post post = postDao.findPostByName("SomeNotExistingInDbNameHere");
        assertNull(post);
    }

    @Test
    public void checkFindsPathByPostName() {
        String postPath = postDao.findPostPathByName("post-about-rest");
        assertEquals("2018/01/REST", postPath);
    }

    @Test
    public void checkFindsPostAfter() {
        LocalDateTime dateTime = LocalDateTime.of(2018, 3, 3, 0, 1);
        List<Post> posts = postDao.findPostAfter(dateTime, null, 1000);
        assertFalse(posts.isEmpty());
        List<Post> wrongDatePosts = posts.stream()
                .filter(p -> comparePostDateTime(p, dateTime) < 0)
                .collect(Collectors.toList());
        assertTrue(wrongDatePosts.isEmpty());
    }

    @Test
    public void checkFindsNoPostAfter() {
        LocalDateTime dateTime = LocalDateTime.of(2028, 3, 3, 0, 1);
        List<Post> posts = postDao.findPostAfter(dateTime, null, 1000);
        assertTrue(posts.isEmpty());
    }

    @Test
    public void checkOrdersPostAfter() {
        LocalDateTime dateTime = LocalDateTime.of(2010, 3, 3, 0, 1);
        List<Post> posts = postDao.findPostAfter(dateTime, null, 1000);
        assertPostsOrdered(posts);
    }

    private void assertPostsOrdered(List<Post> posts) {
        List<Post> orderedPosts = new ArrayList<>(posts);
        Collections.sort(orderedPosts, this::comparePostsDateTime);
        for (int i = 0; i < posts.size(); i++) {
            assertTrue(orderedPosts.get(i) == posts.get(i));
        }
    }

    @Test
    public void checkLimitsPostAfter() {
        LocalDateTime dateTime = LocalDateTime.of(2010, 3, 3, 0, 1);
        List<Post> posts = postDao.findPostAfter(dateTime, null, 1000);
        assertTrue(posts.size() > 2);
        int limit = posts.size() - 1;
        List<Post> limitedPosts = postDao.findPostAfter(dateTime, null, limit);
        assertEquals(limit, limitedPosts.size());
    }

    @Test
    public void checkFiltersByTagIdPostAfter() {
        LocalDateTime dateTime = LocalDateTime.of(2010, 3, 3, 0, 1);
        List<Post> posts = postDao.findPostAfter(dateTime, 1L, 1000);
        assertFalse(posts.isEmpty());
        assertAllPostsHaveTagId(posts, 1L);
    }

    private void assertAllPostsHaveTagId(Collection<Post> posts, Long tagId) {
        for(Post post : posts) {
            assertTrue(post.getTags().stream()
                    .anyMatch(tag -> tagId.equals(tag.getId())));
        }
    }

    @Test
    public void checkDoesntLoseTagWhenFiltering() {
        LocalDateTime dateTime = LocalDateTime.of(2010, 3, 3, 0, 1);
        List<Post> postsFiltered = postDao.findPostAfter(dateTime, 1L, 1000);
        List<Post> posts = postDao.findPostAfter(dateTime, null, 1000);
        Map<Long, Integer> postTagsSizeMap = posts.stream()
                .collect(Collectors.toMap(Post::getId, post -> post.getTags().size()));
        for(Post post : postsFiltered) {
            assertEquals(postTagsSizeMap.get(post.getId()).intValue(), post.getTags().size());
        }
    }

    @Test
    public void checkReturnsPostAfterWithTagsOrdered() {
        LocalDateTime dateTime = LocalDateTime.of(2010, 3, 3, 0, 1);
        List<Post> posts = postDao.findPostAfter(dateTime, null, 1000);
        assertTagsOrderedByName(posts);
    }

    private void assertTagsOrderedByName(Collection<Post> posts) {
        for (Post post : posts) {
            List<Tag> postTags = post.getTags();
            List<Tag> tagsOrdered = new ArrayList<>(postTags);
            Collections.sort(tagsOrdered, this::compareTags);
            for (int i = 0 ; i < tagsOrdered.size(); i++) {
                assertTrue(tagsOrdered.get(i) == postTags.get(i));
            }
        }
    }

    private int compareTags(Tag tag1, Tag tag2) {
       return tag1.getName().compareTo(tag2.getName());
    }

    @Test
    public void checkFindsPostBefore() {
        LocalDateTime dateTime = LocalDateTime.of(2018, 4, 14, 0, 1);
        List<Post> posts = postDao.findPostBefore(dateTime, null, 1000);
        assertFalse(posts.isEmpty());
        List<Post> wrongDatePosts = posts.stream()
                .filter(p -> comparePostDateTime(p, dateTime) > 0)
                .collect(Collectors.toList());
        assertTrue(wrongDatePosts.isEmpty());
    }

    @Test
    public void checkFindsNoPostBefore() {
        LocalDateTime dateTime = LocalDateTime.of(2000, 3, 3, 0, 1);
        List<Post> posts = postDao.findPostBefore(dateTime, null, 1000);
        assertTrue(posts.isEmpty());
    }

    @Test
    public void checkOrdersPostBefore() {
        LocalDateTime dateTime = LocalDateTime.of(2060, 3, 3, 0, 1);
        List<Post> posts = postDao.findPostBefore(dateTime, null, 1000);
        assertPostsOrdered(posts);
    }

    @Test
    public void checkLimitsPostBefore() {
        LocalDateTime dateTime = LocalDateTime.of(2060, 3, 3, 0, 1);
        List<Post> posts = postDao.findPostBefore(dateTime, null, 1000);
        assertTrue(posts.size() > 1);
        List<Post> limitedPosts = postDao.findPostBefore(dateTime, null, 1);
        assertEquals(1, limitedPosts.size());
    }

    @Test
    public void checkLimitsPostBefore2() {
        LocalDateTime dateTime = LocalDateTime.of(2060, 3, 3, 0, 1);
        List<Post> posts = postDao.findPostBefore(dateTime, null, 1000);
        assertTrue(posts.size() > 2);
        int limit = posts.size() - 1;
        List<Post> limitedPosts = postDao.findPostBefore(dateTime, null, limit);
        assertEquals(limit, limitedPosts.size());
    }

    @Test
    public void checkReturnsPostBeforeWithTagsOrdered() {
        LocalDateTime dateTime = LocalDateTime.of(2080, 3, 3, 0, 1);
        List<Post> posts = postDao.findPostBefore(dateTime, null, 1000);
        assertTagsOrderedByName(posts);
    }

    private int comparePostDateTime(Post post, LocalDateTime dateTime) {
        LocalDateTime postDateTime = post.getLastTouch();
        return postDateTime.compareTo(dateTime);
    }

    private int comparePostsDateTime(Post post1, Post post2) {
        LocalDateTime post1DateTime = post1.getLastTouch();
        LocalDateTime post2DateTime = post2.getLastTouch();
        return post2DateTime.compareTo(post1DateTime);
    }

}

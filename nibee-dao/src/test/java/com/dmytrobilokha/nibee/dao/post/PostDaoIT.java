package com.dmytrobilokha.nibee.dao.post;

import com.dmytrobilokha.nibee.dao.AbstractDaoTest;
import com.dmytrobilokha.nibee.data.Post;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
        List<Post> posts = postDao.findPostByName("post-about-rest");
        assertEquals(1, posts.size());
        assertEquals("post-about-rest", posts.get(0).getName());
    }

    @Test
    public void checkFindsPostWithoutTagByName() {
        List<Post> posts = postDao.findPostByName("resources");
        assertEquals(1, posts.size());
        assertEquals("resources", posts.get(0).getName());
    }

    @Test
    public void checkDoesntFindPost() {
        List<Post> posts = postDao.findPostByName("SomeNotExistingInDbNameHere");
        assertTrue(posts.isEmpty());
    }

    @Test
    public void checkFindsPostByTagId() {
        final long id = 1L;
        List<Post> posts = postDao.findPostByTagId(id);
        assertEveryPostHasTagWithId(posts, id);
    }

    private void assertEveryPostHasTagWithId(Collection<Post> posts, long id) {
        assertFalse("Posts list should not be empty", posts.isEmpty());
        for (Post post : posts) {
            boolean containsId = post.getTags()
                    .stream()
                    .anyMatch(t -> Long.valueOf(id).equals(t.getId()));
            assertTrue(post + " should has tag with id=" + id, containsId);
        }
    }

    @Test
    public void checkDoesntFindPostByTagId() {
        List<Post> posts = postDao.findPostByTagId(12345678111L);
        assertTrue(posts.isEmpty());
    }

    @Test
    public void checkFindsPathByPostName() {
        List<String> postPaths = postDao.findPostPathByName("post-about-rest");
        assertEquals(1, postPaths.size());
        assertEquals("2018/01/REST", postPaths.get(0));
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
        assertTrue(posts.size() > 1);
        List<Post> limitedPosts = postDao.findPostAfter(dateTime, null, 1);
        assertEquals(1, limitedPosts.size());
    }

    @Test
    public void checkFiltersByTagIdPostAfter() {
        LocalDateTime dateTime = LocalDateTime.of(2010, 3, 3, 0, 1);
        List<Post> posts = postDao.findPostAfter(dateTime, 1L, 1000);
        assertFalse(posts.isEmpty());
        for(Post post : posts) {
            assertTrue(post.getTags().stream()
                    .anyMatch(tag -> tag.getId() == 1L));
        }
    }

    @Test
    public void checkDoesntLoseTagWheFiltering() {
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
        List<Post> orderedPosts = new ArrayList<>(posts);
        Collections.sort(orderedPosts, this::comparePostsDateTime);
        for (int i = 0; i < posts.size(); i++) {
            assertTrue(orderedPosts.get(i) == posts.get(i));
        }
    }

    @Test
    public void checkLimitsPostBefore() {
        LocalDateTime dateTime = LocalDateTime.of(2060, 3, 3, 0, 1);
        List<Post> posts = postDao.findPostBefore(dateTime, null, 1000);
        assertTrue(posts.size() > 1);
        List<Post> limitedPosts = postDao.findPostBefore(dateTime, null, 1);
        assertEquals(1, limitedPosts.size());
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

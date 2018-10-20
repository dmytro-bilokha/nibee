package com.dmytrobilokha.nibee.dao.post;

import com.dmytrobilokha.nibee.dao.AbstractDaoTest;
import com.dmytrobilokha.nibee.data.Post;
import com.dmytrobilokha.nibee.data.Tag;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

public class PostDaoTest extends AbstractDaoTest {

    private PostDao postDao;

    @BeforeClass
    public void loadData() {
        executeSqlScripts("post.sql");
    }

    @BeforeMethod
    public void initPostRepository() {
        postDao = getMapper(PostDao.class);
    }

    public void findsPostByName() {
        Post post = postDao.findPostByName("post-about-rest");
        assertNotNull(post);
        assertEquals("post-about-rest", post.getName());
    }

    public void findsPostWithoutTagByName() {
        Post post = postDao.findPostByName("resources");
        assertNotNull(post);
        assertEquals("resources", post.getName());
    }

    public void doesntFindPost() {
        Post post = postDao.findPostByName("SomeNotExistingInDbNameHere");
        assertNull(post);
    }

    public void findsPathByPostName() {
        String postPath = postDao.findPostPathByName("post-about-rest");
        assertEquals("2018/01/REST", postPath);
    }

    public void findsPostAfter() {
        LocalDateTime dateTime = LocalDateTime.of(2018, 3, 3, 0, 1);
        List<Post> posts = postDao.findPostAfter(dateTime, null, 1000);
        assertFalse(posts.isEmpty());
        List<Post> wrongDatePosts = posts.stream()
                .filter(p -> comparePostDateTime(p, dateTime) < 0)
                .collect(Collectors.toList());
        assertTrue(wrongDatePosts.isEmpty());
    }

    public void findsNoPostAfter() {
        LocalDateTime dateTime = LocalDateTime.of(2028, 3, 3, 0, 1);
        List<Post> posts = postDao.findPostAfter(dateTime, null, 1000);
        assertTrue(posts.isEmpty());
    }

    public void ordersPostAfter() {
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

    public void limitsPostAfter() {
        LocalDateTime dateTime = LocalDateTime.of(2010, 3, 3, 0, 1);
        List<Post> posts = postDao.findPostAfter(dateTime, null, 1000);
        assertTrue(posts.size() > 2);
        int limit = posts.size() - 1;
        List<Post> limitedPosts = postDao.findPostAfter(dateTime, null, limit);
        assertEquals(limit, limitedPosts.size());
    }

    public void filtersByTagIdPostAfter() {
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

    public void doesntLoseTagWhenFiltering() {
        LocalDateTime dateTime = LocalDateTime.of(2010, 3, 3, 0, 1);
        List<Post> postsFiltered = postDao.findPostAfter(dateTime, 1L, 1000);
        List<Post> posts = postDao.findPostAfter(dateTime, null, 1000);
        Map<Long, Integer> postTagsSizeMap = posts.stream()
                .collect(Collectors.toMap(Post::getId, post -> post.getTags().size()));
        for(Post post : postsFiltered) {
            assertEquals(postTagsSizeMap.get(post.getId()).intValue(), post.getTags().size());
        }
    }

    public void returnsPostAfterWithTagsOrdered() {
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

    public void findsPostBefore() {
        LocalDateTime dateTime = LocalDateTime.of(2018, 4, 14, 0, 1);
        List<Post> posts = postDao.findPostBefore(dateTime, null, 1000);
        assertFalse(posts.isEmpty());
        List<Post> wrongDatePosts = posts.stream()
                .filter(p -> comparePostDateTime(p, dateTime) > 0)
                .collect(Collectors.toList());
        assertTrue(wrongDatePosts.isEmpty());
    }

    public void findsNoPostBefore() {
        LocalDateTime dateTime = LocalDateTime.of(2000, 3, 3, 0, 1);
        List<Post> posts = postDao.findPostBefore(dateTime, null, 1000);
        assertTrue(posts.isEmpty());
    }

    public void ordersPostBefore() {
        LocalDateTime dateTime = LocalDateTime.of(2060, 3, 3, 0, 1);
        List<Post> posts = postDao.findPostBefore(dateTime, null, 1000);
        assertPostsOrdered(posts);
    }

    public void limitsPostBefore() {
        LocalDateTime dateTime = LocalDateTime.of(2060, 3, 3, 0, 1);
        List<Post> posts = postDao.findPostBefore(dateTime, null, 1000);
        assertTrue(posts.size() > 1);
        List<Post> limitedPosts = postDao.findPostBefore(dateTime, null, 1);
        assertEquals(1, limitedPosts.size());
    }

    public void limitsPostBefore2() {
        LocalDateTime dateTime = LocalDateTime.of(2060, 3, 3, 0, 1);
        List<Post> posts = postDao.findPostBefore(dateTime, null, 1000);
        assertTrue(posts.size() > 2);
        int limit = posts.size() - 1;
        List<Post> limitedPosts = postDao.findPostBefore(dateTime, null, limit);
        assertEquals(limit, limitedPosts.size());
    }

    public void returnsPostBeforeWithTagsOrdered() {
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

    public void countsExistingPost() {
        int numPosts = postDao.countPostsById(1L);
        assertEquals(1, numPosts);
    }

    public void doesntCountsNonExistingPost() {
        int numPosts = postDao.countPostsById(88889999L);
        assertEquals(0, numPosts);
    }

}

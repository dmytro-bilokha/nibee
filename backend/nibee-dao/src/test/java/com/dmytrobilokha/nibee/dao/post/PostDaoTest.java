package com.dmytrobilokha.nibee.dao.post;

import com.dmytrobilokha.nibee.dao.AbstractDaoTest;
import com.dmytrobilokha.nibee.data.Post;
import com.dmytrobilokha.nibee.data.PostWithTags;
import com.dmytrobilokha.nibee.data.Tag;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertSame;
import static org.testng.AssertJUnit.assertTrue;

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

    @Test
    public void findsPostByName() {
        PostWithTags post = postDao.findPostByName("post-about-rest");
        assertNotNull(post);
        assertEquals("post-about-rest", post.getName());
    }

    @Test(dependsOnMethods = "findsPostByName")
    public void readsPostFlags() {
        PostWithTags post = postDao.findPostByName("post-about-rest");
        assertTrue(post.isShareable());
        assertTrue(post.isCommentAllowed());
    }

    @Test
    public void findsPostWithoutTagByName() {
        PostWithTags post = postDao.findPostByName("resources");
        assertNotNull(post);
        assertEquals("resources", post.getName());
    }

    @Test
    public void doesntFindPost() {
        PostWithTags post = postDao.findPostByName("SomeNotExistingInDbNameHere");
        assertNull(post);
    }

    @Test
    public void findsPathByPostName() {
        String postPath = postDao.findPostPathByName("post-about-rest");
        assertEquals("2018/01/REST", postPath);
    }

    @Test
    public void findsPostAfter() {
        LocalDateTime dateTime = LocalDateTime.of(2018, 3, 3, 0, 1);
        List<PostWithTags> posts = postDao.findPostAfter(dateTime, null, 1000);
        assertFalse(posts.isEmpty());
        List<PostWithTags> wrongDatePosts = posts.stream()
                .filter(p -> comparePostDateTime(p, dateTime) < 0)
                .collect(Collectors.toList());
        assertTrue(wrongDatePosts.isEmpty());
    }

    @Test
    public void findsNoPostAfter() {
        LocalDateTime dateTime = LocalDateTime.of(2028, 3, 3, 0, 1);
        List<PostWithTags> posts = postDao.findPostAfter(dateTime, null, 1000);
        assertTrue(posts.isEmpty());
    }

    @Test
    public void ordersPostAfter() {
        LocalDateTime dateTime = LocalDateTime.of(2010, 3, 3, 0, 1);
        List<PostWithTags> posts = postDao.findPostAfter(dateTime, null, 1000);
        assertPostsOrdered(posts);
    }

    private void assertPostsOrdered(List<PostWithTags> posts) {
        List<PostWithTags> orderedPosts = new ArrayList<>(posts);
        Collections.sort(orderedPosts, this::comparePostsDateTime);
        for (int i = 0; i < posts.size(); i++) {
            assertSame(orderedPosts.get(i), posts.get(i));
        }
    }

    @Test
    public void limitsPostAfter() {
        LocalDateTime dateTime = LocalDateTime.of(2010, 3, 3, 0, 1);
        List<PostWithTags> posts = postDao.findPostAfter(dateTime, null, 1000);
        assertTrue(posts.size() > 2);
        int limit = posts.size() - 1;
        List<PostWithTags> limitedPosts = postDao.findPostAfter(dateTime, null, limit);
        assertEquals(limit, limitedPosts.size());
    }

    @Test
    public void filtersByTagIdPostAfter() {
        LocalDateTime dateTime = LocalDateTime.of(2010, 3, 3, 0, 1);
        List<PostWithTags> posts = postDao.findPostAfter(dateTime, 1L, 1000);
        assertFalse(posts.isEmpty());
        assertAllPostsHaveTagId(posts, 1L);
    }

    private void assertAllPostsHaveTagId(Collection<PostWithTags> posts, Long tagId) {
        for (PostWithTags post : posts) {
            assertTrue(post.getTags().stream()
                    .anyMatch(tag -> tagId.equals(tag.getId())));
        }
    }

    @Test
    public void doesntLoseTagWhenFiltering() {
        LocalDateTime dateTime = LocalDateTime.of(2010, 3, 3, 0, 1);
        List<PostWithTags> postsFiltered = postDao.findPostAfter(dateTime, 1L, 1000);
        List<PostWithTags> posts = postDao.findPostAfter(dateTime, null, 1000);
        Map<Long, Integer> postTagsSizeMap = posts.stream()
                .collect(Collectors.toMap(PostWithTags::getId, post -> post.getTags().size()));
        for (PostWithTags post : postsFiltered) {
            assertEquals(postTagsSizeMap.get(post.getId()).intValue(), post.getTags().size());
        }
    }

    @Test
    public void returnsPostAfterWithTagsOrdered() {
        LocalDateTime dateTime = LocalDateTime.of(2010, 3, 3, 0, 1);
        List<PostWithTags> posts = postDao.findPostAfter(dateTime, null, 1000);
        assertTagsOrderedByName(posts);
    }

    private void assertTagsOrderedByName(Collection<PostWithTags> posts) {
        for (PostWithTags post : posts) {
            List<Tag> postTags = post.getTags();
            List<Tag> tagsOrdered = new ArrayList<>(postTags);
            Collections.sort(tagsOrdered, this::compareTags);
            for (int i = 0; i < tagsOrdered.size(); i++) {
                assertSame(tagsOrdered.get(i), postTags.get(i));
            }
        }
    }

    private int compareTags(Tag tag1, Tag tag2) {
        return tag1.getName().compareTo(tag2.getName());
    }

    @Test
    public void findsPostBefore() {
        LocalDateTime dateTime = LocalDateTime.of(2018, 4, 14, 0, 1);
        List<PostWithTags> posts = postDao.findPostBefore(dateTime, null, 1000);
        assertFalse(posts.isEmpty());
        List<PostWithTags> wrongDatePosts = posts.stream()
                .filter(p -> comparePostDateTime(p, dateTime) > 0)
                .collect(Collectors.toList());
        assertTrue(wrongDatePosts.isEmpty());
    }

    @Test
    public void findsNoPostBefore() {
        LocalDateTime dateTime = LocalDateTime.of(2000, 3, 3, 0, 1);
        List<PostWithTags> posts = postDao.findPostBefore(dateTime, null, 1000);
        assertTrue(posts.isEmpty());
    }

    @Test
    public void ordersPostBefore() {
        LocalDateTime dateTime = LocalDateTime.of(2060, 3, 3, 0, 1);
        List<PostWithTags> posts = postDao.findPostBefore(dateTime, null, 1000);
        assertPostsOrdered(posts);
    }

    @Test
    public void limitsPostBefore() {
        LocalDateTime dateTime = LocalDateTime.of(2060, 3, 3, 0, 1);
        List<PostWithTags> posts = postDao.findPostBefore(dateTime, null, 1000);
        assertTrue(posts.size() > 1);
        List<PostWithTags> limitedPosts = postDao.findPostBefore(dateTime, null, 1);
        assertEquals(1, limitedPosts.size());
    }

    @Test
    public void limitsPostBefore2() {
        LocalDateTime dateTime = LocalDateTime.of(2060, 3, 3, 0, 1);
        List<PostWithTags> posts = postDao.findPostBefore(dateTime, null, 1000);
        assertTrue(posts.size() > 2);
        int limit = posts.size() - 1;
        List<PostWithTags> limitedPosts = postDao.findPostBefore(dateTime, null, limit);
        assertEquals(limit, limitedPosts.size());
    }

    @Test
    public void returnsPostBeforeWithTagsOrdered() {
        LocalDateTime dateTime = LocalDateTime.of(2080, 3, 3, 0, 1);
        List<PostWithTags> posts = postDao.findPostBefore(dateTime, null, 1000);
        assertTagsOrderedByName(posts);
    }

    private int comparePostDateTime(PostWithTags post, LocalDateTime dateTime) {
        LocalDateTime postDateTime = post.getLastTouch();
        return postDateTime.compareTo(dateTime);
    }

    private int comparePostsDateTime(PostWithTags post1, PostWithTags post2) {
        LocalDateTime post1DateTime = post1.getLastTouch();
        LocalDateTime post2DateTime = post2.getLastTouch();
        return post2DateTime.compareTo(post1DateTime);
    }

    @Test
    public void countsExistingPost() {
        int numPosts = postDao.countPostsById(1L);
        assertEquals(1, numPosts);
    }

    @Test
    public void doesntCountsNonExistingPost() {
        int numPosts = postDao.countPostsById(88889999L);
        assertEquals(0, numPosts);
    }

    @Test
    public void findsPostNameDuplicates() {
        Post post = new Post("post-about-rest", "title", "path", true, true);
        List<Post> duplicates = postDao.findPostDuplicates(post);
        assertEquals(1, duplicates.size());
        assertEquals(Long.valueOf(1000L), duplicates.get(0).getId());
    }

    @Test
    public void findsPostPathDuplicates() {
        Post post = new Post("name", "title", "2018/02/SOAP", true, true);
        List<Post> duplicates = postDao.findPostDuplicates(post);
        assertEquals(1, duplicates.size());
        assertEquals(Long.valueOf(2000L), duplicates.get(0).getId());
    }

    @Test
    public void createsPostAndSetsId() {
        Post post = new Post("newName", "newTitle", "newPath", true, true);
        postDao.createPost(post);
        assertNotNull(post.getId());
    }

    @Test(dependsOnMethods = {"findsPostByName", "createsPostAndSetsId"})
    public void createdPostDataFilledInDb() {
        Post postCreated = postDao.findPostByName("newName");
        assertNotNull(postCreated.getId());
        assertEquals("newPath", postCreated.getPath());
        assertTrue(postCreated.isCommentAllowed());
        assertTrue(postCreated.isShareable());
        assertNotNull(postCreated.getCreatedOn());
        assertNull(postCreated.getModifiedOn());
    }

}

package com.dmytrobilokha.nibee.service.post;

import com.dmytrobilokha.nibee.dao.post.PostDao;
import com.dmytrobilokha.nibee.data.Post;
import com.dmytrobilokha.nibee.data.PostWithTags;
import com.dmytrobilokha.nibee.service.config.ConfigProperty;
import com.dmytrobilokha.nibee.service.config.ConfigService;
import com.dmytrobilokha.nibee.service.file.FileService;
import com.dmytrobilokha.nibee.service.tag.TagService;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertSame;

@Test(groups = "service.unit")
public class PostServiceTest {

    private PostService postService;
    private PostDao postDaoMock;
    private TagService tagServiceMock;
    private FileService fileServiceMock;
    private ConfigService configServiceMock;

    @BeforeClass
    public void init() {
        postDaoMock = mock(PostDao.class);
        tagServiceMock = mock(TagService.class);
        fileServiceMock = mock(FileService.class);
        configServiceMock = mock(ConfigService.class);
        postService = new PostServiceImpl(postDaoMock, tagServiceMock, fileServiceMock, configServiceMock);
    }

    @BeforeMethod
    public void setupMockDefaults() {
        when(postDaoMock.findPostByName(anyString())).thenReturn(null);
        when(postDaoMock.findPostDuplicates(any())).thenReturn(Collections.emptyList());
        when(tagServiceMock.getAll()).thenReturn(Collections.emptyList());
        when(configServiceMock.getAsString(ConfigProperty.POSTS_ROOT)).thenReturn("/tmp/postRoot");
    }

    @AfterMethod
    public void resetMocks() {
        reset(postDaoMock);
    }

    @Test
    public void returnsNullWhenPostNotFound() {
        PostWithTags result = postService.findPostByName("name doesnt matter");
        assertNull(result);
    }

    @Test
    public void returnsPostByName() {
        PostWithTags post1 = mock(PostWithTags.class);
        when(postDaoMock.findPostByName(anyString())).thenReturn(post1);
        PostWithTags result = postService.findPostByName("name doesnt matter");
        assertSame(post1, result);
    }

    @Test
    public void createsPost() throws IllegalPostDataException, IOException {
        Post newPost = new Post("Name", "Title", "Path", true, true);
        InputStream mockZipStream = mock(InputStream.class);
        Set<Long> postTags = Collections.emptySet();
        postService.createPost(mockZipStream, newPost, postTags);
        verify(fileServiceMock, times(1)).unzipStreamToDir(same(mockZipStream), any(Path.class));
        verify(postDaoMock, times(1)).createPost(same(newPost));
        verify(tagServiceMock, times(1)).assignTagsToPost(same(null), same(postTags));
        verify(fileServiceMock, times(1)).renameAtomically(any(Path.class), eq(Paths.get("/tmp/postRoot/Path")));
    }

    @Test(dependsOnMethods = "createsPost", expectedExceptions = IllegalPostDataException.class)
    public void noCreationIfPostNonUnique() throws IllegalPostDataException {
        Post newPost = new Post("Name", "Title", "Path", true, true);
        when(postDaoMock.findPostDuplicates(any(Post.class))).thenReturn(Collections.singletonList(newPost));
        postService.createPost(mock(InputStream.class), newPost, Collections.emptySet());
    }

    @Test(dependsOnMethods = "createsPost", expectedExceptions = IllegalPostDataException.class)
    public void noCreationIfTagsUnknown() throws IllegalPostDataException {
        Post newPost = new Post("Name", "Title", "Path", true, true);
        postService.createPost(mock(InputStream.class), newPost, Collections.singleton(42L));
    }

}

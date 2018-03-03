package com.dmytrobilokha.nibee.web.blog;

import com.dmytrobilokha.nibee.service.config.ConfigService;
import com.dmytrobilokha.nibee.service.file.FileService;
import com.dmytrobilokha.nibee.service.post.PostService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class BlogResponseFactoryTest {

    private BlogResponseFactory blogResponseFactory;
    private ConfigService mockConfigService;
    private PostService mockPostService;
    private FileService mockFileService;
    private HttpServletRequest mockRequest;

    private final String servletPath;
    private final Class responseClassExpected;
    private final String postNameExpected;
    private final String postResourceExpected;

    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(new Object[][] {
                {"/postname/../../anothername", NotFoundBlogResponse.class, null, null}
                , {"/postname//anothername/", NotFoundBlogResponse.class, null, null}
                , {"/post\\name/", NotFoundBlogResponse.class, null, null}
                , {"/post/~name/", NotFoundBlogResponse.class, null, null}
                , {"/post+name/", NotFoundBlogResponse.class, null, null}
                , {"/post%name/", NotFoundBlogResponse.class, null, null}
                , {"/postname$/", NotFoundBlogResponse.class, null, null}
                , {"/", WelcomeBlogResponse.class, null, null}
                , {"", WelcomeBlogResponse.class, null, null}
                , {"/postname/", PostEntryBlogResponse.class, "postname", null}
                , {"/postname", PostEntryBlogResponse.class, "postname", null}
                , {"postname", PostEntryBlogResponse.class, "postname", null}
                , {"/postname/postresource", PostResourceBlogResponse.class, "postname", "postresource"}
                , {"postname/postresource", PostResourceBlogResponse.class, "postname", "postresource"}
                , {"/POSTNAMe/postRESource", PostResourceBlogResponse.class, "postname", "postresource"}
                , {"//postname/postresource//", PostResourceBlogResponse.class, "postname", "postresource"}
                , {"/postname/postresource/css/img.jpg", PostResourceBlogResponse.class, "postname", "postresource/css/img.jpg"}
                , {"/postname/postresource/css/img.jpg/", PostResourceBlogResponse.class, "postname", "postresource/css/img.jpg"}
        });
    }

    public BlogResponseFactoryTest(String servletPath, Class responseClassExpected
            , String postNameExpected, String postResourceExpected) {
        this.servletPath = servletPath;
        this.responseClassExpected = responseClassExpected;
        this.postNameExpected = postNameExpected;
        this.postResourceExpected = postResourceExpected;
    }

    @Before
    public void init() {
        mockConfigService = Mockito.mock(ConfigService.class);
        mockPostService = Mockito.mock(PostService.class);
        mockFileService = Mockito.mock(FileService.class);
        mockRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getServletPath()).thenReturn(servletPath);
        blogResponseFactory = new BlogResponseFactory(mockConfigService, mockPostService, mockFileService);
    }

    @Test
    public void testFactoryOutput() {
        BlogResponse response = blogResponseFactory.createResponse(mockRequest);
        assertEquals(responseClassExpected, response.getClass());
        if (responseClassExpected == PostEntryBlogResponse.class) {
            PostEntryBlogResponse postEntryBlogResponse = (PostEntryBlogResponse) response;
            assertEquals(postNameExpected, postEntryBlogResponse.postName);
        }
        if (responseClassExpected == PostResourceBlogResponse.class) {
            PostResourceBlogResponse postResourceBlogResponse = (PostResourceBlogResponse) response;
            assertEquals(postNameExpected, postResourceBlogResponse.postName);
            assertEquals(postResourceExpected, postResourceBlogResponse.postResource);
        }
    }

}

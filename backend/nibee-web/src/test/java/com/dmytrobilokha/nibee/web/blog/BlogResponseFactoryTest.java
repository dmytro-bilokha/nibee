package com.dmytrobilokha.nibee.web.blog;

import com.dmytrobilokha.nibee.service.config.ConfigService;
import com.dmytrobilokha.nibee.service.file.FileService;
import com.dmytrobilokha.nibee.service.post.PostService;
import com.dmytrobilokha.nibee.web.comment.CommentsModelCreator;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

@Test(groups = "web.unit")
public class BlogResponseFactoryTest {

    private BlogResponseFactory blogResponseFactory;
    private ConfigService mockConfigService;
    private PostService mockPostService;
    private FileService mockFileService;
    private CommentsModelCreator mockCommentsModelCreator;
    private HttpServletRequest mockRequest;

    @DataProvider(name = "blogResponseFactoryData")
    public Object[][] getParameters() {
        return new Object[][] {
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
                , {"/POSTNAMe/postRESource", PostResourceBlogResponse.class, "POSTNAMe", "postRESource"}
                , {"//postname/postresource//", PostResourceBlogResponse.class, "postname", "postresource"}
                , {"/postname/postresource/css/img.jpg", PostResourceBlogResponse.class, "postname"
                        , "postresource/css/img.jpg"}
                , {"/postname/postresource/css/img.jpg/", PostResourceBlogResponse.class, "postname"
                        , "postresource/css/img.jpg"}
        };
    }

    @BeforeClass
    public void init() {
        mockCommentsModelCreator = mock(CommentsModelCreator.class);
        mockConfigService = mock(ConfigService.class);
        mockPostService = mock(PostService.class);
        mockFileService = mock(FileService.class);
        mockRequest = mock(HttpServletRequest.class);
        blogResponseFactory = new BlogResponseFactory(mockConfigService, mockPostService
                , mockFileService, mockCommentsModelCreator);
    }

    @Test(dataProvider = "blogResponseFactoryData")
    public void testFactoryOutput(
            String servletPath
            , Class responseClassExpected
            , String postNameExpected
            , String postResourceExpected
    ) {
        when(mockRequest.getServletPath()).thenReturn(servletPath);
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

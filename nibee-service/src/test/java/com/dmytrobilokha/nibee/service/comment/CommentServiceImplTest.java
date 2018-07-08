package com.dmytrobilokha.nibee.service.comment;

import com.dmytrobilokha.nibee.dao.comment.CommentDao;
import com.dmytrobilokha.nibee.service.post.PostService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CommentServiceImplTest {

    private CommentDao commentDaoMock;
    private PostService postServiceMock;

    @Before
    public void init() {
        commentDaoMock = Mockito.mock(CommentDao.class);
        postServiceMock = Mockito.mock(PostService.class);
        when(postServiceMock.doesPostExist(any())).thenReturn(true);
    }

    @Test
    public void createAndSave() {
        //TODO: implement tests to check validation
    }
}
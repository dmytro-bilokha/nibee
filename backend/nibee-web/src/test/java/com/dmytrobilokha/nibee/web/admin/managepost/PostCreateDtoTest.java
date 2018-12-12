package com.dmytrobilokha.nibee.web.admin.managepost;

import com.dmytrobilokha.nibee.web.errorhandling.InvalidClientDataException;
import org.testng.annotations.Test;

@Test(groups = "web.unit")
public class PostCreateDtoTest {

    private static final String VALID_TITLE = "This is the valid blog post title!";
    private static final String VALID_WEB_PATH = "this-is-the-valid-web-path";
    private static final String VALID_FS_PATH = "this_is_theValid.fs0path";
    private static final long[] EMPTY_TAGS = new long[0];

    @Test
    public void allowsValidData() throws InvalidClientDataException {
        PostCreateDto postCreateDto = new PostCreateDto(
                VALID_TITLE, VALID_WEB_PATH, VALID_FS_PATH, true, true, EMPTY_TAGS);
        postCreateDto.validate();
    }

    @Test(expectedExceptions = InvalidClientDataException.class)
    public void blockNonAsciiTitle() throws InvalidClientDataException {
        PostCreateDto postCreateDto = new PostCreateDto(
                "Few non-ascii symbols: йцукен", VALID_WEB_PATH, VALID_FS_PATH, true, true, EMPTY_TAGS);
        postCreateDto.validate();
    }

    @Test(expectedExceptions = InvalidClientDataException.class)
    public void blocksInvalidWebPath() throws InvalidClientDataException {
        PostCreateDto postCreateDto = new PostCreateDto(
                VALID_TITLE, "this-web-path-is-!valid", VALID_FS_PATH, true, true, EMPTY_TAGS);
        postCreateDto.validate();
    }

    @Test(expectedExceptions = InvalidClientDataException.class)
    public void blocksInvalidFsPath() throws InvalidClientDataException {
        PostCreateDto postCreateDto = new PostCreateDto(
                VALID_TITLE, VALID_WEB_PATH, "The+fs=invalid", true, true, EMPTY_TAGS);
        postCreateDto.validate();
    }

    @Test(expectedExceptions = InvalidClientDataException.class)
    public void blocksRepeatingTags() throws InvalidClientDataException {
        PostCreateDto postCreateDto = new PostCreateDto(
                VALID_TITLE, VALID_WEB_PATH, VALID_FS_PATH, true, true, new long[]{1L, 2L, 3L, 4L, 2L});
        postCreateDto.validate();
    }

    @Test(expectedExceptions = InvalidClientDataException.class)
    public void blocksTooManyTags() throws InvalidClientDataException {
        PostCreateDto postCreateDto = new PostCreateDto(
                VALID_TITLE, VALID_WEB_PATH, VALID_FS_PATH, true, true, new long[]{1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L});
        postCreateDto.validate();
    }

}

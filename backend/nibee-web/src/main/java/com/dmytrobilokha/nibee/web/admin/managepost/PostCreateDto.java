package com.dmytrobilokha.nibee.web.admin.managepost;

import com.dmytrobilokha.nibee.data.Post;
import com.dmytrobilokha.nibee.web.errorhandling.InvalidClientDataException;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PostCreateDto {

    private static final Pattern TITLE_PATTERN = Pattern.compile("^\\p{ASCII}{5,}$");
    private static final Pattern WEB_PATH_PATTERN = Pattern.compile("^[a-zA-Z0-9-]{5,}$");
    private static final Pattern FS_PATH_PATTERN = Pattern.compile("^[._a-zA-Z0-9-]{5,}$");
    private static final int MAX_TAGS = 7;

    private final String title;
    private final String webPath;
    private final String fsPath;
    private final boolean shareable;
    private final boolean commentAllowed;
    private final long[] tagIds;

    @JsonbCreator
    public PostCreateDto(
            @JsonbProperty("title") String title
            , @JsonbProperty("webPath") String webPath
            , @JsonbProperty("fsPath") String fsPath
            , @JsonbProperty("shareable") boolean shareable
            , @JsonbProperty("commentAllowed") boolean commentAllowed
            , @JsonbProperty("tagIds") long[] tagIds
    ) {
        this.fsPath = fsPath;
        this.title = title;
        this.webPath = webPath;
        this.shareable = shareable;
        this.commentAllowed = commentAllowed;
        this.tagIds = tagIds;
    }

    void validate() throws InvalidClientDataException {
        List<String> errorMessages = new ArrayList<>();
        Consumer<String> errorMessageAdder = errorMessages::add;
        validateTitle(errorMessageAdder);
        validateWebPath(errorMessageAdder);
        validateFsPath(errorMessageAdder);
        validateTagIds(errorMessageAdder);
        if (errorMessages.isEmpty()) {
            return;
        }
        throw new InvalidClientDataException(errorMessages);
    }

    private void validateTitle(Consumer<String> errorMessageAdder) {
        if (title == null || !TITLE_PATTERN.matcher(title).matches()) {
            errorMessageAdder.accept(
                    "Title should be at least 5 characters long and must contain only ASCII characters,"
                    + " but got: '" + title + '\''
            );
        }
    }

    private void validateWebPath(Consumer<String> errorMessageAdder) {
        if (webPath == null || !WEB_PATH_PATTERN.matcher(webPath).matches()) {
            errorMessageAdder.accept(
                    "Web path should be at least 5 characters long and must contain only numbers,"
                    + " latin letters and minus sign, but got: '" + webPath + '\''
            );
        }
    }

    private void validateFsPath(Consumer<String> errorMessageAdder) {
        if (fsPath == null || !FS_PATH_PATTERN.matcher(fsPath).matches()) {
            errorMessageAdder.accept(
                    "FS path should be at least 5 characters long and must contain only numbers,"
                            + " latin letters, dot, underscore and minus sign, but got: '" + webPath + '\''
            );
        }
    }

    private void validateTagIds(Consumer<String> errorMessageAdder) {
        if (tagIds == null) {
            errorMessageAdder.accept("Tag ids must not be null");
            return;
        }
        long dedupLength = Arrays.stream(tagIds)
                .distinct()
                .count();
        if (dedupLength < tagIds.length) {
            errorMessageAdder.accept("Tag ids must not contain duplicates, but got: '"
                + Arrays.toString(tagIds) + '\'');
        }
        if (tagIds.length > MAX_TAGS) {
            errorMessageAdder.accept("Maximum " + MAX_TAGS + " tags is allowed, but got " + tagIds.length);
        }
    }

    public String getTitle() {
        return title;
    }

    public String getWebPath() {
        return webPath;
    }

    public String getFsPath() {
        return fsPath;
    }

    public boolean isShareable() {
        return shareable;
    }

    public boolean isCommentAllowed() {
        return commentAllowed;
    }

    public long[] getTagIds() {
        return Arrays.copyOf(tagIds, tagIds.length);
    }

    public Post toPost() {
        return new Post(webPath, title, fsPath, shareable, commentAllowed);
    }

    public Set<Long> tagIdSet() {
        return Arrays.stream(tagIds)
                .mapToObj(Long::valueOf)
                .collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return "PostCreateDto{"
                + "title='" + title + '\''
                + ", webPath='" + webPath + '\''
                + ", fsPath='" + fsPath + '\''
                + ", shareable=" + shareable
                + ", commentAllowed=" + commentAllowed
                + ", tagIds=" + Arrays.toString(tagIds)
                + '}';
    }

}

package com.dmytrobilokha.nibee.data;

import com.dmytrobilokha.nibee.data.adapter.LocalDateTimeAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class Comment {

    private Long id;
    private Long postId;
    private Long parentCommentId;
    private String authorNickname;
    private String content;
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime createdOn;

    Comment() {
        //This constructor is required by MyBatis
    }

    public Comment(Long postId, Long parentCommentId, String authorNickname, String content, LocalDateTime createdOn) {
        this.postId = postId;
        this.parentCommentId = parentCommentId;
        this.authorNickname = authorNickname;
        this.content = content;
        this.createdOn = createdOn;
    }

    public Long getId() {
        return id;
    }

    public Long getPostId() {
        return postId;
    }

    public Long getParentCommentId() {
        return parentCommentId;
    }

    public String getAuthorNickname() {
        return authorNickname;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Comment comment = (Comment) o;
        return Objects.equals(id, comment.id)
            && Objects.equals(postId, comment.postId)
            && Objects.equals(parentCommentId, comment.parentCommentId)
            && Objects.equals(authorNickname, comment.authorNickname)
            && Objects.equals(content, comment.content)
            && Objects.equals(createdOn, comment.createdOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, postId, parentCommentId);
    }

    @Override
    public String toString() {
        return "Comment{" + "id=" + id + ", postId=" + postId + ", parentCommentId=" + parentCommentId
            + ", authorNickname='" + authorNickname + '\'' + ", content='" + content
            + '\'' + ", createdOn=" + createdOn + '}';
    }

}

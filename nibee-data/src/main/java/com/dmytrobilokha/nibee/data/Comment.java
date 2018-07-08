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
    private String authorNickname;
    private String content;
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime createdOn;

    Comment() {
        //This constructor is required by MyBatis
    }

    public Comment(Long postId, String authorNickname, String content, LocalDateTime createdOn) {
        this.postId = postId;
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(id, comment.id) &&
                Objects.equals(authorNickname, comment.authorNickname) &&
                Objects.equals(content, comment.content) &&
                Objects.equals(createdOn, comment.createdOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, authorNickname, createdOn);
    }

}

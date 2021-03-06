package com.dmytrobilokha.nibee.dao.tag;

import com.dmytrobilokha.nibee.data.Tag;
import org.apache.ibatis.annotations.Param;
import org.mybatis.cdi.Mapper;

import java.util.List;

@Mapper
public interface TagDao {

    List<Tag> getAll();

    int assignTagToPost(@Param("postId") Long postId, @Param("tagId") Long tagId);

}

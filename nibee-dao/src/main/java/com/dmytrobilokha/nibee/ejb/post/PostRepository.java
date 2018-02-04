package com.dmytrobilokha.nibee.ejb.post;

import org.mybatis.cdi.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PostRepository {

    @Select("SELECT name FROM post")
    List<String> getNames();
}

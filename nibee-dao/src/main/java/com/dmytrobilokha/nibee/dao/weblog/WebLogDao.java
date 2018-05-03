package com.dmytrobilokha.nibee.dao.weblog;

import com.dmytrobilokha.nibee.data.WebLogEntry;
import org.mybatis.cdi.Mapper;

@Mapper
public interface WebLogDao {

    int insertEntry(WebLogEntry entry);

}

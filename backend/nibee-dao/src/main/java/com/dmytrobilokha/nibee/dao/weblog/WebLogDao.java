package com.dmytrobilokha.nibee.dao.weblog;

import com.dmytrobilokha.nibee.data.WebLogRecord;
import org.mybatis.cdi.Mapper;

@Mapper
public interface WebLogDao {

    int insertRecord(WebLogRecord entry);

    int countRecords();

    int deleteOldestRecords(int limit);

}

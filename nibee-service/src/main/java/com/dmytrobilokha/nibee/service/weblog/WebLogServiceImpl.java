package com.dmytrobilokha.nibee.service.weblog;

import com.dmytrobilokha.nibee.dao.weblog.WebLogDao;
import com.dmytrobilokha.nibee.data.WebLogRecord;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class WebLogServiceImpl implements WebLogService {

    private WebLogDao webLogDao;

    public WebLogServiceImpl() {
        //Constructor required by the EJB spec
    }

    @Inject
    public WebLogServiceImpl(WebLogDao webLogDao) {
        this.webLogDao = webLogDao;
    }

    @Override
    public int insertRecord(WebLogRecord entry) {
        return webLogDao.insertRecord(entry);
    }

}

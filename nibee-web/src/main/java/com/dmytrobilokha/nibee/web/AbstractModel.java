package com.dmytrobilokha.nibee.web;

import javax.servlet.ServletRequest;

public abstract class AbstractModel {

    public void putInRequest(ServletRequest req) {
        req.setAttribute(getNameForEl(), this);
    }

    private String getNameForEl() {
        String simpleClassName = getClass().getSimpleName();
        return Character.toLowerCase(simpleClassName.charAt(0)) + simpleClassName.substring(1);
    }

}

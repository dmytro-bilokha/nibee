package com.dmytrobilokha.nibee.web;

import javax.servlet.ServletRequest;

public interface InRequestPuttable {

    default void putInRequest(ServletRequest req) {
        String simpleClassName = getClass().getSimpleName();
        String elName = Character.toLowerCase(simpleClassName.charAt(0)) + simpleClassName.substring(1);
        req.setAttribute(elName, this);
    }

}

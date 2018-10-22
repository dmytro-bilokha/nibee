package com.dmytrobilokha.nibee.web.param;

import com.dmytrobilokha.nibee.web.InRequestPuttable;

public class InvalidParamException extends Exception implements InRequestPuttable {

    InvalidParamException(String paramName, String paramValue) {
        super("Provided '" + paramName + "' parameter value '" + paramValue + "' is invalid.");
    }

    InvalidParamException(String paramName, String paramValue, String additionalMessage) {
        super("Provided '" + paramName + "' parameter value '" + paramValue + "' is invalid. " + additionalMessage);
    }

}

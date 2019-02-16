package com.dmytrobilokha.nibee.web.param;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class ParamParseUtils {

    private ParamParseUtils() {
        //Not going to instantiate the class
    }

    public static Long parseLong(HttpServletRequest request, String paramName) throws InvalidParamException {
        String longValueString = request.getParameter(paramName);
        if (longValueString == null || longValueString.isEmpty()) {
            return null;
        }
        try {
            return Long.valueOf(longValueString, 10);
        } catch (NumberFormatException ex) {
            throw new InvalidParamException(paramName, longValueString, "Should be an integer number.", ex);
        }
    }

    public static LocalDateTime parseDateTime(HttpServletRequest request, String paramName
            , DateTimeFormatter formatter) throws InvalidParamException {
        String dateTimeString = request.getParameter(paramName);
        if (dateTimeString == null || dateTimeString.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeString, formatter);
        } catch (DateTimeParseException ex) {
            throw new InvalidParamException(paramName, dateTimeString
                    , "Should be a date time in the format: " + formatter.toString(), ex);
        }

    }

}


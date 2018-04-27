package com.dmytrobilokha.nibee.web.param;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class ParamParser {

    private ParamParser() {
        //Not going to instantiate the class
    }

    public static Optional<Long> parseLong(HttpServletRequest request, String paramName) throws InvalidParamException {
        String longValueString = request.getParameter(paramName);
        if (longValueString == null || longValueString.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(Long.valueOf(longValueString, 10));
        } catch (NumberFormatException ex) {
            throw new InvalidParamException(paramName, longValueString, "Should be an integer number.");
        }
    }

    public static Optional<LocalDateTime> parseDateTime(HttpServletRequest request, String paramName
            , DateTimeFormatter formatter) throws InvalidParamException {
        String dateTimeString = request.getParameter(paramName);
        if (dateTimeString == null || dateTimeString.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(LocalDateTime.parse(dateTimeString, formatter));
        } catch (DateTimeParseException ex) {
            throw new InvalidParamException(paramName, dateTimeString
                    , "Should be a date time in the format: " + formatter.toString());
        }

    }

}


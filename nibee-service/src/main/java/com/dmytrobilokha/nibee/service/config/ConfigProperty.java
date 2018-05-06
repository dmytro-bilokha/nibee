package com.dmytrobilokha.nibee.service.config;

public enum ConfigProperty {
    POSTS_ROOT("postsRoot")
    , HEADLINERS_PER_PAGE("headlinersPerPage")
    , WEB_LOG_RECORDS_MAX("webLogRecordsMax")
    ;

    private final String propertyName;

    ConfigProperty(String name) {
        this.propertyName = name;
    }

    public String getPropertyName() {
        return propertyName;
    }

}

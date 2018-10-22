package com.dmytrobilokha.nibee.service.config;

public enum ConfigProperty {

    APP_VERSION("applicationVersion", true)
    , BUILD_TIMESTAMP("buildTimestamp", true)
    , POSTS_ROOT("postsRoot", false)
    , HEADLINERS_PER_PAGE("headlinersPerPage", false)
    , WEB_LOG_RECORDS_MAX("webLogRecordsMax", false)
    ;

    private final String propertyName;
    private final boolean buildDefined;

    ConfigProperty(String name, boolean buildDefined) {
        this.propertyName = name;
        this.buildDefined = buildDefined;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public boolean isBuildDefined() {
        return buildDefined;
    }
}

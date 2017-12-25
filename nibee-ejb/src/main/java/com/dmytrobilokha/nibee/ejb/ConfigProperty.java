package com.dmytrobilokha.nibee.ejb;

public enum ConfigProperty {
    POSTS_ROOT("posts.root")
    ;

    private final String propertyName;

    ConfigProperty(String name) {
        this.propertyName = AppConstants.APP_NAME + '/' + name;
    }

    public String getPropertyName() {
        return propertyName;
    }

}

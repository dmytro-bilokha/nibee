package com.dmytrobilokha.nibee.web;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@ApplicationScoped
@Named
public class WebConstants {

    public static final String SITE_NAME = "Dmytro Bilokha's Blog";
    public static final String TAG_ID = "tagId";

    public String getSiteName() {
        return SITE_NAME;
    }

    public String getTagId() {
        return TAG_ID;
    }

}

package com.dmytrobilokha.nibee.web;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@ApplicationScoped
@Named
public class WebConstants {

    public static final String SITE_NAME = "Dmytro Bilokha's Blog";

    public String getSiteName() {
        return SITE_NAME;
    }
}

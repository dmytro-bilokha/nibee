package com.dmytrobilokha.nibee.web.admin.configreport;

import com.dmytrobilokha.nibee.service.config.ConfigProperty;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequestScoped
@Named
public class ConfigReport {

    private final List<ConfigReportElement> elements = new ArrayList<>();

    public void add(ConfigProperty configProperty, String value) {
        elements.add(new ConfigReportElement(configProperty, value));
    }

    public void sort() {
        Collections.sort(elements);
    }

    public List<ConfigReportElement> getElements() {
        return elements;
    }

}

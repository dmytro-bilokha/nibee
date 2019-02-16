package com.dmytrobilokha.nibee.web.admin.configreport;

import com.dmytrobilokha.nibee.service.config.ConfigProperty;

public class ConfigReportElement implements Comparable<ConfigReportElement> {

    private final String enumName;
    private final String propertyName;
    private final String value;

    public ConfigReportElement(ConfigProperty configProperty, String value) {
        this.enumName = configProperty.name();
        this.propertyName = configProperty.getPropertyName();
        this.value = value;
    }

    public String getEnumName() {
        return enumName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getValue() {
        return value;
    }

    @Override
    public int compareTo(ConfigReportElement o) {
        int compResult;
        compResult = propertyName.compareTo(o.propertyName);
        if (compResult != 0) {
            return compResult;
        }
        compResult = enumName.compareTo(o.enumName);
        if (compResult != 0) {
            return compResult;
        }
        return value.compareTo(o.value);
    }

}

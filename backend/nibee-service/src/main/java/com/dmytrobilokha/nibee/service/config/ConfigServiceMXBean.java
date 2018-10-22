package com.dmytrobilokha.nibee.service.config;

import javax.ejb.Local;
import java.util.Map;

@Local
public interface ConfigServiceMXBean {

    void updateConfigProperties();

    Map<ConfigProperty, String> getProperties();
}

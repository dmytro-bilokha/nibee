package com.dmytrobilokha.nibee.service.config;

import javax.ejb.Local;

@Local
public interface ConfigService {

    String getAsString(ConfigProperty configProperty);

    int getAsInt(ConfigProperty configProperty);

}

package com.dmytrobilokha.nibee.web;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashMap;
import java.util.Map;

@ApplicationPath("api")
public class JaxRsConfig extends Application {

    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> map = new HashMap<>();
        //Without this Payara/Glassfish ignores @XmlJavaTypeAdapter during xml or json marshalling
        map.put("jersey.config.server.disableMoxyJson", true);
        return map;
    }

}

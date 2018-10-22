package com.dmytrobilokha.nibee.web.admin.configreport;

import com.dmytrobilokha.nibee.service.config.ConfigProperty;
import com.dmytrobilokha.nibee.service.config.ConfigService;
import com.dmytrobilokha.nibee.web.NavigablePage;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = "/admin/configreport")
public class ConfigReportServlet extends HttpServlet {

    private final ConfigService configService;
    private final ConfigReport configReport;

    @Inject
    public ConfigReportServlet(ConfigService configService, ConfigReport configReport) {
        this.configService = configService;
        this.configReport = configReport;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        buildReport();
        NavigablePage.CONFIG_REPORT.forwardTo(req, resp);
    }

    private void buildReport() {
        for (ConfigProperty configProperty : ConfigProperty.values()) {
            configReport.add(configProperty, configService.getAsString(configProperty));
        }
        configReport.sort();
    }

}

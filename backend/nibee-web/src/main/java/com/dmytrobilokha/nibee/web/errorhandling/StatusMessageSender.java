package com.dmytrobilokha.nibee.web.errorhandling;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@ApplicationScoped
public class StatusMessageSender {

    private Jsonb jsonb;

    StatusMessageSender() {
        //The no-args constructor required by the CDI spec
    }

    @Inject
    public StatusMessageSender(Jsonb jsonb) {
        this.jsonb = jsonb;
    }

    public void sendClientErrorStatus(String message, HttpServletResponse resp) {
        PrintWriter responseWriter = setupResponseToSendClientErrorStatus(resp);
        jsonb.toJson(new StatusMessage(StatusMessage.Code.CLIENT_ERROR, Collections.singletonList(message))
                , responseWriter);
    }

    public void sendClientErrorStatus(InvalidClientDataException ex, HttpServletResponse resp) {
        PrintWriter responseWriter = setupResponseToSendClientErrorStatus(resp);
        jsonb.toJson(ex.getStatusMessage(), responseWriter);
    }

    public void sendServerProblemStatus(String message, HttpServletResponse resp) {
        PrintWriter responseWriter = setupResponseToSendServerProblemStatus(resp);
        jsonb.toJson(new StatusMessage(StatusMessage.Code.SERVER_PROBLEM, Collections.singletonList(message))
                , responseWriter);
    }

    public void sendOkStatus(String message, HttpServletResponse resp) {
        PrintWriter responseWriter = setupResponseToSendStatusMessage(HttpServletResponse.SC_OK, resp);
        jsonb.toJson(new StatusMessage(StatusMessage.Code.OK, Collections.singletonList(message)), responseWriter);
    }

    private PrintWriter setupResponseToSendClientErrorStatus(HttpServletResponse resp) {
        return setupResponseToSendStatusMessage(HttpServletResponse.SC_BAD_REQUEST, resp);
    }

    private PrintWriter setupResponseToSendServerProblemStatus(HttpServletResponse resp) {
        return setupResponseToSendStatusMessage(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
    }

    private PrintWriter setupResponseToSendStatusMessage(int httpStatus, HttpServletResponse resp) {
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType(MediaType.APPLICATION_JSON);
        resp.setStatus(httpStatus);
        try {
            return resp.getWriter();
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to get PrintWriter from HttpServletResponse"
                    + " to send client error status message", ex);
        }
    }

}

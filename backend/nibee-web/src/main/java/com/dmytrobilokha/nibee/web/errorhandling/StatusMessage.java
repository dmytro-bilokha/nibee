package com.dmytrobilokha.nibee.web.errorhandling;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatusMessage implements Serializable {

    private final String code;
    private final List<String> messages;

    @JsonbCreator
    public StatusMessage(
            @JsonbProperty("code") String code
            , @JsonbProperty("messages") List<String> messages
    ) {
        this.code = code;
        this.messages = new ArrayList<>(messages);
    }

    public StatusMessage(Code code, List<String> messages) {
        this.code = code.codeString;
        this.messages = new ArrayList<>(messages);
    }

    public String getCode() {
        return code;
    }

    public List<String> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    @Override
    public String toString() {
        return "StatusMessage{"
                + "code='" + code + '\''
                + ", messages=" + messages
                + '}';
    }

    public enum Code {
        OK("OK")
        , CLIENT_ERROR("CLIENT_ERROR")
        , SERVER_PROBLEM("SERVER_PROBLEM")
        ;

        private final String codeString;

        Code(String codeString) {
            this.codeString = codeString;
        }
    }

}

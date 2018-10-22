package com.dmytrobilokha.nibee.service.comment;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

@ApplicationScoped
public class NicknameValidator {

    private static final int NICKNAME_MAX_LENGTH = 25;
    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");

    public List<String> checkNickname(String nickname) {
        if (nickname == null || nickname.isEmpty()) {
            return Collections.singletonList("Nickname should not be empty");
        }
        List<String> errorMessages = new ArrayList<>();
        if (nickname.length() > NICKNAME_MAX_LENGTH) {
            errorMessages.add("Nickname should not exceed "
                    + NICKNAME_MAX_LENGTH + " characters");
        }
        if (!NICKNAME_PATTERN.matcher(nickname).matches()) {
            errorMessages.add("Nickname should contain only latin letters (a-z, A-Z) and digits (0-9)");
        }
        return errorMessages;
    }

}

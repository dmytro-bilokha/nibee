package com.dmytrobilokha.nibee.service.comment;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NicknameValidatorTest {

    private NicknameValidator nicknameValidator;

    @Before
    public void createValidator() {
        this.nicknameValidator = new NicknameValidator();
    }

    @Test
    public void checkDisablesNull() {
        assertFalse(nicknameValidator.checkNickname(null).isEmpty());
    }

    @Test
    public void checkDisablesEmpty() {
        assertFalse(nicknameValidator.checkNickname("").isEmpty());
    }

    @Test
    public void checkDisablesSpace() {
        assertFalse(nicknameValidator.checkNickname("Big Boss").isEmpty());
    }

    @Test
    public void checkEnables() {
        assertTrue(nicknameValidator.checkNickname("BigBoss42").isEmpty());
    }

    @Test
    public void checkEnables2() {
        assertTrue(nicknameValidator.checkNickname("TheAuthor").isEmpty());
    }

    @Test
    public void checkDisablesTooLongNickname() {
        assertFalse(nicknameValidator.checkNickname("BigBoss42authorNicknameIsTooooooLongToBeAllowed").isEmpty());
    }

    @Test
    public void checkDisablesTooLongNicknameWithNotAllowedSymbols() {
        assertEquals(2, nicknameValidator.checkNickname("BigBoss42authorNickname IsTooooooLongToBeAllowed").size());
    }

}
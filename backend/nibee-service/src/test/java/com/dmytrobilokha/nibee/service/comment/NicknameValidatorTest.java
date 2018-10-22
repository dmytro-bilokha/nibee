package com.dmytrobilokha.nibee.service.comment;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@Test(groups = "service.unit")
public class NicknameValidatorTest {

    private NicknameValidator nicknameValidator;

    @BeforeClass
    public void createValidator() {
        this.nicknameValidator = new NicknameValidator();
    }

    public void disablesNull() {
        assertFalse(nicknameValidator.checkNickname(null).isEmpty());
    }

    public void disablesEmpty() {
        assertFalse(nicknameValidator.checkNickname("").isEmpty());
    }

    public void disablesSpace() {
        assertFalse(nicknameValidator.checkNickname("Big Boss").isEmpty());
    }

    public void enablesValidName() {
        assertTrue(nicknameValidator.checkNickname("BigBoss42").isEmpty());
    }

    public void enablesAnotherValidName() {
        assertTrue(nicknameValidator.checkNickname("TheAuthor").isEmpty());
    }

    public void disablesTooLongNickname() {
        assertFalse(nicknameValidator.checkNickname("BigBoss42authorNicknameIsTooooooLongToBeAllowed").isEmpty());
    }

    public void disablesTooLongNicknameWithNotAllowedSymbols() {
        assertEquals(2, nicknameValidator.checkNickname("BigBoss42authorNickname IsTooooooLongToBeAllowed").size());
    }

}

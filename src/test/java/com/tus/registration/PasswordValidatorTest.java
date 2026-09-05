package com.tus.registration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PasswordValidatorTest {

    @Test
    void matchingPasswordsReturnTrue() {
        assertTrue(PasswordValidator.checkPasswordsMatch("Secret123", "Secret123"));
    }

    @Test
    void differentPasswordsReturnFalse() {
        assertFalse(PasswordValidator.checkPasswordsMatch("Secret123", "Secret124"));
    }

    @Test
    void caseDifferenceReturnsFalse() {
        assertFalse(PasswordValidator.checkPasswordsMatch("secret123", "Secret123"));
    }

    @Test
    void nullConfirmationReturnsFalse() {
        assertFalse(PasswordValidator.checkPasswordsMatch("Secret123", null));
    }
}
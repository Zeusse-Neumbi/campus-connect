package com.example.school.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PasswordUtilTest {

    @Test
    void hashPassword_ShouldReturnHashedString() {
        String password = "mySecretPassword123";
        String hashed = PasswordUtil.hashPassword(password);
        
        assertNotNull(hashed);
        assertNotEquals(password, hashed);
        assertTrue(hashed.startsWith("$2a$")); // BCrypt prefix
    }

    @Test
    void checkPassword_ShouldReturnTrue_WhenMatch() {
        String password = "mySecretPassword123";
        String hashed = PasswordUtil.hashPassword(password);
        
        assertTrue(PasswordUtil.checkPassword(password, hashed));
    }

    @Test
    void checkPassword_ShouldReturnFalse_WhenMismatch() {
        String password = "mySecretPassword123";
        String wrongPassword = "wrongPassword123";
        String hashed = PasswordUtil.hashPassword(password);
        
        assertFalse(PasswordUtil.checkPassword(wrongPassword, hashed));
    }

    @Test
    void checkPassword_ShouldReturnFalse_WhenHashedIsNull() {
        assertFalse(PasswordUtil.checkPassword("password", null));
    }

    @Test
    void checkPassword_ShouldReturnFalse_WhenHashedIsInvalidFormat() {
        // Not a BCrypt string
        assertFalse(PasswordUtil.checkPassword("password", "invalidHashFormat123"));
    }
}

package com.sonardemo.service;

import com.sonardemo.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
    }

    @Test
    void generateSessionToken_returnsNonNullToken() {
        String token = userService.generateSessionToken();
        
        assertNotNull(token);
        assertEquals(32, token.length());
    }

    @Test
    void generateSessionToken_returnsDifferentTokensOnMultipleCalls() {
        String token1 = userService.generateSessionToken();
        String token2 = userService.generateSessionToken();
        
        // Tokens should be different (most of the time)
        assertNotEquals(token1, token2);
    }

    @Test
    void validatePassword_withMatchingPasswords_returnsTrue() {
        boolean result = userService.validatePassword("password123", "password123");
        
        assertTrue(result);
    }

    @Test
    void validatePassword_withNonMatchingPasswords_returnsFalse() {
        boolean result = userService.validatePassword("password123", "wrongpassword");
        
        assertFalse(result);
    }

    @Test
    void getUserDisplayName_withValidUser_returnsFormattedName() {
        User user = new User();
        user.setUsername("john");
        user.setEmail("JOHN@EXAMPLE.COM");
        
        String displayName = userService.getUserDisplayName(user);
        
        assertEquals("JOHN (john@example.com)", displayName);
    }

    @Test
    void createUser_withValidParams_returnsUser() {
        User user = userService.createUser(
            "testuser", "password", "test@example.com",
            "John", "Doe", "1234567890",
            "123 Main St", "City", "Country",
            "USER", true, 25
        );
        
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("USER", user.getRole());
        assertTrue(user.isActive());
    }

    @Test
    void findUserByUsername_withNonExistentUser_returnsNull() {
        // This will fail to connect to DB but should return null due to exception handling
        User user = userService.findUserByUsername("nonexistent");
        
        assertNull(user);
    }

    @Test
    void searchUsers_withInvalidDb_returnsEmptyList() {
        // This will fail to connect to DB but should return empty list due to exception handling
        var users = userService.searchUsers("test", "admin");
        
        assertNotNull(users);
        assertTrue(users.isEmpty());
    }
}


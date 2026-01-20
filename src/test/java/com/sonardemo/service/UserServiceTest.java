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
        
        assertNotEquals(token1, token2);
    }

    @Test
    void authenticate_withNonExistentUser_returnsFalse() {
        boolean result = userService.authenticate("nonexistent", "password");
        
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
    void findByUsername_withNonExistentUser_returnsNull() {
        User user = userService.findByUsername("nonexistent");
        
        assertNull(user);
    }

    @Test
    void findByEmail_withNonExistentEmail_returnsNull() {
        User user = userService.findByEmail("nonexistent@example.com");
        
        assertNull(user);
    }

    @Test
    void searchUsers_withInvalidDb_returnsEmptyList() {
        var users = userService.searchUsers("test", "admin");
        
        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    void findByRole_withInvalidDb_throwsException() {
        assertThrows(RuntimeException.class, () -> {
            userService.findByRole("ADMIN", "username");
        });
    }

    @Test
    void updateUserRole_withInvalidDb_throwsException() {
        assertThrows(RuntimeException.class, () -> {
            userService.updateUserRole("123", "ADMIN");
        });
    }

    @Test
    void deleteUser_withInvalidDb_returnsFalse() {
        boolean result = userService.deleteUser("nonexistent");
        
        assertFalse(result);
    }
}

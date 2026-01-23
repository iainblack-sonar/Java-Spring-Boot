package com.sonardemo.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void defaultConstructor_createsEmptyUser() {
        User user = new User();
        
        assertNull(user.getId());
        assertNull(user.getUsername());
        assertNull(user.getPassword());
        assertNull(user.getEmail());
        assertNull(user.getRole());
        assertFalse(user.isActive());
    }

    @Test
    void parameterizedConstructor_setsAllFields() {
        User user = new User(1L, "john", "pass123", "john@example.com", "ADMIN", true);
        
        assertEquals(1L, user.getId());
        assertEquals("john", user.getUsername());
        assertEquals("pass123", user.getPassword());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("ADMIN", user.getRole());
        assertTrue(user.isActive());
    }

    @Test
    void setters_updateFields() {
        User user = new User();
        
        user.setId(2L);
        user.setUsername("jane");
        user.setPassword("secret");
        user.setEmail("jane@example.com");
        user.setRole("USER");
        user.setActive(true);
        
        assertEquals(2L, user.getId());
        assertEquals("jane", user.getUsername());
        assertEquals("secret", user.getPassword());
        assertEquals("jane@example.com", user.getEmail());
        assertEquals("USER", user.getRole());
        assertTrue(user.isActive());
    }
}




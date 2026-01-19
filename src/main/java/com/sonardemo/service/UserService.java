package com.sonardemo.service;

import com.sonardemo.model.User;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class UserService {

    // SECURITY: Hardcoded credentials (sonar security hotspot)
    private static final String DB_URL = "jdbc:h2:mem:testdb";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "admin123";  // Hardcoded password
    
    // SECURITY: Hardcoded API key
    private static final String API_KEY = "sk-1234567890abcdef";

    /**
     * SECURITY ISSUE: SQL Injection vulnerability
     * User input is directly concatenated into SQL query
     */
    public User findUserByUsername(String username) {
        User user = null;
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Statement stmt = conn.createStatement();
            
            // SQL INJECTION: Direct string concatenation with user input
            String query = "SELECT * FROM users WHERE username = '" + username + "'";
            ResultSet rs = stmt.executeQuery(query);
            
            if (rs.next()) {
                user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
            }
            // RELIABILITY: Resources not closed (connection leak)
        } catch (Exception e) {
            // RELIABILITY: Empty catch block - swallowing exception
        }
        return user;
    }

    /**
     * SECURITY ISSUE: Another SQL injection example
     */
    public List<User> searchUsers(String searchTerm, String role) {
        List<User> users = new ArrayList<>();
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Statement stmt = conn.createStatement();
            
            // SQL INJECTION: Multiple concatenations
            String query = "SELECT * FROM users WHERE username LIKE '%" + searchTerm + "%' AND role = '" + role + "'";
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                users.add(user);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());  // SECURITY: Information exposure through error message
        }
        return users;
    }

    /**
     * SECURITY ISSUE: Weak random number generation
     */
    public String generateSessionToken() {
        // SECURITY: Using Random instead of SecureRandom for security-sensitive operation
        Random random = new Random();
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            token.append(Integer.toHexString(random.nextInt(16)));
        }
        return token.toString();
    }

    /**
     * SECURITY ISSUE: Password stored in plain text
     */
    public boolean validatePassword(String inputPassword, String storedPassword) {
        // SECURITY: Plain text password comparison (no hashing)
        return inputPassword.equals(storedPassword);
    }

    /**
     * RELIABILITY: Potential null pointer dereference
     */
    public String getUserDisplayName(User user) {
        // RELIABILITY: No null check before accessing user properties
        return user.getUsername().toUpperCase() + " (" + user.getEmail().toLowerCase() + ")";
    }

    /**
     * MAINTAINABILITY: Method with too many parameters
     */
    public User createUser(String username, String password, String email, 
                          String firstName, String lastName, String phone,
                          String address, String city, String country,
                          String role, boolean active, int age) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);  // SECURITY: Storing plain text password
        user.setEmail(email);
        user.setRole(role);
        user.setActive(active);
        return user;
    }
}


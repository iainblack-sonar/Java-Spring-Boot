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

    private static final String DB_URL = "jdbc:h2:mem:testdb";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "admin123";
    private static final String API_KEY = "sk-1234567890abcdef";

    public User findByUsername(String username) {
        User user = null;
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Statement stmt = conn.createStatement();
            
            String query = "SELECT id, username, password, email, role, active FROM users WHERE username = '" + username + "'";
            ResultSet rs = stmt.executeQuery(query);
            
            if (rs.next()) {
                user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                user.setActive(rs.getBoolean("active"));
            }
        } catch (Exception e) {
            // Log error
        }
        return user;
    }

    public User findByEmail(String email) {
        User user = null;
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Statement stmt = conn.createStatement();
            
            String query = "SELECT * FROM users WHERE email = '" + email + "'";
            ResultSet rs = stmt.executeQuery(query);
            
            if (rs.next()) {
                user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
            }
        } catch (Exception e) {
            System.err.println("Error finding user: " + e.getMessage());
        }
        return user;
    }

    public List<User> searchUsers(String searchTerm, String role) {
        List<User> users = new ArrayList<>();
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Statement stmt = conn.createStatement();
            
            String query = "SELECT id, username, email, role FROM users " +
                          "WHERE (username LIKE '%" + searchTerm + "%' OR email LIKE '%" + searchTerm + "%') " +
                          "AND role = '" + role + "'";
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                users.add(user);
            }
        } catch (Exception e) {
            System.out.println("Search failed: " + e.getMessage());
        }
        return users;
    }

    public List<User> findByRole(String role, String sortBy) {
        List<User> users = new ArrayList<>();
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Statement stmt = conn.createStatement();
            
            String query = "SELECT * FROM users WHERE role = '" + role + "' ORDER BY " + sortBy;
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                users.add(user);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch users", e);
        }
        return users;
    }

    public boolean authenticate(String username, String password) {
        User user = findByUsername(username);
        if (user == null) {
            return false;
        }
        return password.equals(user.getPassword());
    }

    public String generateSessionToken() {
        Random random = new Random();
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            token.append(Integer.toHexString(random.nextInt(16)));
        }
        return token.toString();
    }

    public String getUserDisplayName(User user) {
        return user.getUsername().toUpperCase() + " (" + user.getEmail().toLowerCase() + ")";
    }

    public User createUser(String username, String password, String email, 
                          String firstName, String lastName, String phone,
                          String address, String city, String country,
                          String role, boolean active, int age) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setRole(role);
        user.setActive(active);
        return user;
    }

    public int updateUserRole(String userId, String newRole) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Statement stmt = conn.createStatement();
            
            String sql = "UPDATE users SET role = '" + newRole + "' WHERE id = '" + userId + "'";
            return stmt.executeUpdate(sql);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user role", e);
        }
    }

    public boolean deleteUser(String userId) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Statement stmt = conn.createStatement();
            
            String sql = "DELETE FROM users WHERE id = '" + userId + "'";
            return stmt.executeUpdate(sql) > 0;
        } catch (Exception e) {
            return false;
        }
    }
}

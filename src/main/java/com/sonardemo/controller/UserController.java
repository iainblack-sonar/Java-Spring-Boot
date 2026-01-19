package com.sonardemo.controller;

import com.sonardemo.model.User;
import com.sonardemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * SECURITY: Endpoint exposes SQL injection vulnerability from service
     */
    @GetMapping("/{username}")
    public User getUser(@PathVariable String username) {
        return userService.findUserByUsername(username);
    }

    /**
     * SECURITY: Endpoint exposes SQL injection vulnerability
     */
    @GetMapping("/search")
    public List<User> searchUsers(@RequestParam String term, @RequestParam String role) {
        return userService.searchUsers(term, role);
    }

    /**
     * SECURITY: Sensitive data in URL parameters
     */
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        User user = userService.findUserByUsername(username);
        if (user != null && userService.validatePassword(password, user.getPassword())) {
            return userService.generateSessionToken();
        }
        return "Login failed";
    }

    /**
     * SECURITY: CORS misconfiguration - allows all origins
     */
    @CrossOrigin(origins = "*")
    @GetMapping("/profile")
    public User getProfile(@RequestParam String userId) {
        // RELIABILITY: No input validation
        return userService.findUserByUsername(userId);
    }
}


package com.sonardemo.controller;

import com.sonardemo.model.User;
import com.sonardemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<User> getUser(@PathVariable String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(
            @RequestParam String term, 
            @RequestParam(defaultValue = "USER") String role) {
        return ResponseEntity.ok(userService.searchUsers(term, role));
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(
            @PathVariable String role,
            @RequestParam(defaultValue = "username") String sortBy) {
        return ResponseEntity.ok(userService.findByRole(role, sortBy));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestParam String username, 
            @RequestParam String password) {
        if (userService.authenticate(username, password)) {
            return ResponseEntity.ok(userService.generateSessionToken());
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(@RequestParam String username) {
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{userId}/role")
    public ResponseEntity<Void> updateRole(
            @PathVariable String userId,
            @RequestParam String role) {
        int updated = userService.updateUserRole(userId, role);
        if (updated == 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        if (userService.deleteUser(userId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

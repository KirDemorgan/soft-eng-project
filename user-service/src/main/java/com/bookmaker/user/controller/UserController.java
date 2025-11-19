package com.bookmaker.user.controller;

import com.bookmaker.user.dto.LoginRequest;
import com.bookmaker.user.dto.RegistrationRequest;
import com.bookmaker.user.dto.UpdateBalanceRequest;
import com.bookmaker.user.model.User;
import com.bookmaker.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "API for user management")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    
    @Operation(summary = "Register a new user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid user data")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest request) {
        try {
            User user = userService.registerUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
            );
            return ResponseEntity.ok(Map.of(
                "message", "User registered successfully",
                "userId", user.getId()
            ));
        } catch (Exception e) {
            log.error("Error registering user", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @Operation(summary = "Authenticate a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Authentication successful"),
        @ApiResponse(responseCode = "400", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Long id = userService.authenticateUser(
                request.getUsername(),
                request.getPassword()
            );
            return ResponseEntity.ok(Map.of("id", id));
        } catch (Exception e) {
            log.error("Error authenticating user", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @Operation(summary = "Get user information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{username}")
    public ResponseEntity<?> getUser(@Parameter(description = "Username") @PathVariable String username) {
        return userService.findByUsername(username)
                .map(user -> ResponseEntity.ok(Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "email", user.getEmail(),
                    "balance", user.getBalance()
                )))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Update user balance")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Balance updated successfully"),
        @ApiResponse(responseCode = "400", description = "Insufficient funds or user not found")
    })
    @PutMapping("/{userId}/balance")
    public ResponseEntity<?> updateBalance(@Parameter(description = "User ID") @PathVariable Long userId,
                                         @RequestBody UpdateBalanceRequest request) {
        try {
            User user = userService.updateBalance(userId, request.getAmount());
            return ResponseEntity.ok(Map.of(
                "message", "Balance updated",
                "newBalance", user.getBalance()
            ));
        } catch (Exception e) {
            log.error("Error updating balance", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
package com.bookmaker.user.decorator;

import com.bookmaker.user.model.User;
import com.bookmaker.user.service.UserService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class LoggingUserServiceDecorator extends UserServiceDecorator {
    private static final Logger log = LoggerFactory.getLogger(LoggingUserServiceDecorator.class);

    public LoggingUserServiceDecorator(UserService userService) {
        super(userService);
    }
    
    @Override
    public User registerUser(String username, String email, String password) {
        log.info("Registering user: {}, email: {}", username, email);
        
        try {
            User user = super.registerUser(username, email, password);
            log.info("User {} registered successfully with ID: {}", username, user.getId());
            return user;
        } catch (Exception e) {
            log.error("Error registering user {}: {}", username, e.getMessage());
            throw e;
        }
    }
    
    @Override
    public Long authenticateUser(String username, String password) {
        log.info("Attempting to authenticate user: {}", username);
        
        try {
            Long id = super.authenticateUser(username, password);
            log.info("User {} authenticated successfully", username);
            return id;
        } catch (Exception e) {
            log.error("Error authenticating user {}: {}", username, e.getMessage());
            throw e;
        }
    }
    
    @Override
    public User updateBalance(Long userId, BigDecimal amount) {
        log.info("Updating balance for user ID {} by amount: {}", userId, amount);
        
        try {
            User user = super.updateBalance(userId, amount);
            log.info("Balance for user ID {} updated. New balance: {}", userId, user.getBalance());
            return user;
        } catch (Exception e) {
            log.error("Error updating balance for user ID {}: {}", userId, e.getMessage());
            throw e;
        }
    }
}
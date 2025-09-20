package com.bookmaker.user.decorator;

import com.bookmaker.user.model.User;
import com.bookmaker.user.service.UserService;

import java.math.BigDecimal;
import java.util.Optional;

// Decorator Pattern - базовый декоратор для сервиса пользователей
public abstract class UserServiceDecorator {
    
    protected UserService userService;
    
    public UserServiceDecorator(UserService userService) {
        this.userService = userService;
    }
    
    public User registerUser(String username, String email, String password) {
        return userService.registerUser(username, email, password);
    }
    
    public String authenticateUser(String username, String password) {
        return userService.authenticateUser(username, password);
    }
    
    public Optional<User> findByUsername(String username) {
        return userService.findByUsername(username);
    }
    
    public User updateBalance(Long userId, BigDecimal amount) {
        return userService.updateBalance(userId, amount);
    }
}
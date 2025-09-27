package com.bookmaker.user.service;

import com.bookmaker.user.model.User;
import com.bookmaker.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public User registerUser(String username, String email, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }
        
        // Использование Builder Pattern
        User user = new User.Builder()
                .username(username)
                .email(email)
                .password((password))
                .balance(BigDecimal.valueOf(1000)) // Стартовый баланс
                .build();
        
        return userRepository.save(user);
    }

    public Long authenticateUser(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Пользователь не найден");
        }

        User user = userOpt.get();

        if (password.equals(user.getPassword())) {
            throw new RuntimeException("Неверный пароль");
        }
        return (user.getId());
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public User updateBalance(Long userId, BigDecimal amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        
        BigDecimal newBalance = user.getBalance().add(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Недостаточно средств");
        }
        
        user.setBalance(newBalance);
        return userRepository.save(user);
    }
}
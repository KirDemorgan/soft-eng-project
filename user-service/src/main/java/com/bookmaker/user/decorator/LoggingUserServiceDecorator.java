package com.bookmaker.user.decorator;

import com.bookmaker.user.model.User;
import com.bookmaker.user.service.UserService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

// Конкретный декоратор для логирования операций пользователей
@Component
public class LoggingUserServiceDecorator extends UserServiceDecorator {
    
    public LoggingUserServiceDecorator(UserService userService) {
        super(userService);
    }
    
    @Override
    public User registerUser(String username, String email, String password) {
        System.out.println(String.format("[%s] Регистрация пользователя: %s, email: %s", 
                LocalDateTime.now(), username, email));
        
        try {
            User user = super.registerUser(username, email, password);
            System.out.println(String.format("[%s] Пользователь %s успешно зарегистрирован с ID: %d", 
                    LocalDateTime.now(), username, user.getId()));
            return user;
        } catch (Exception e) {
            System.err.println(String.format("[%s] Ошибка регистрации пользователя %s: %s", 
                    LocalDateTime.now(), username, e.getMessage()));
            throw e;
        }
    }
    
    @Override
    public String authenticateUser(String username, String password) {
        System.out.println(String.format("[%s] Попытка аутентификации пользователя: %s", 
                LocalDateTime.now(), username));
        
        try {
            String token = super.authenticateUser(username, password);
            System.out.println(String.format("[%s] Пользователь %s успешно аутентифицирован", 
                    LocalDateTime.now(), username));
            return token;
        } catch (Exception e) {
            System.err.println(String.format("[%s] Ошибка аутентификации пользователя %s: %s", 
                    LocalDateTime.now(), username, e.getMessage()));
            throw e;
        }
    }
    
    @Override
    public User updateBalance(Long userId, BigDecimal amount) {
        System.out.println(String.format("[%s] Обновление баланса пользователя ID %d на сумму: %s", 
                LocalDateTime.now(), userId, amount));
        
        try {
            User user = super.updateBalance(userId, amount);
            System.out.println(String.format("[%s] Баланс пользователя ID %d обновлен. Новый баланс: %s", 
                    LocalDateTime.now(), userId, user.getBalance()));
            return user;
        } catch (Exception e) {
            System.err.println(String.format("[%s] Ошибка обновления баланса пользователя ID %d: %s", 
                    LocalDateTime.now(), userId, e.getMessage()));
            throw e;
        }
    }
}
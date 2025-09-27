package com.bookmaker.user.controller;

import com.bookmaker.user.model.User;
import com.bookmaker.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "API для управления пользователями")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Operation(summary = "Регистрация нового пользователя", 
               description = "Создает нового пользователя с начальным балансом 1000")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Пользователь успешно зарегистрирован"),
        @ApiResponse(responseCode = "400", description = "Ошибка валидации данных")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        try {
            User user = userService.registerUser(
                request.get("username"),
                request.get("email"),
                request.get("password")
            );
            return ResponseEntity.ok(Map.of(
                "message", "Пользователь успешно зарегистрирован",
                "userId", user.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @Operation(summary = "Аутентификация пользователя", 
               description = "Проверяет учетные данные и возвращает JWT токен")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешная аутентификация"),
        @ApiResponse(responseCode = "400", description = "Неверные учетные данные")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            Long id = userService.authenticateUser(
                request.get("username"),
                request.get("password")
            );
            return ResponseEntity.ok(Map.of("id", id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @Operation(summary = "Получение информации о пользователе", 
               description = "Возвращает данные пользователя по имени пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Пользователь найден"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/{username}")
    public ResponseEntity<?> getUser(@Parameter(description = "Имя пользователя") @PathVariable String username) {
        return userService.findByUsername(username)
                .map(user -> ResponseEntity.ok(Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "email", user.getEmail(),
                    "balance", user.getBalance()
                )))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Обновление баланса пользователя", 
               description = "Изменяет баланс пользователя на указанную сумму (может быть отрицательной)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Баланс успешно обновлен"),
        @ApiResponse(responseCode = "400", description = "Недостаточно средств или пользователь не найден")
    })
    @PutMapping("/{userId}/balance")
    public ResponseEntity<?> updateBalance(@Parameter(description = "ID пользователя") @PathVariable Long userId, 
                                         @RequestBody Map<String, BigDecimal> request) {
        try {
            User user = userService.updateBalance(userId, request.get("amount"));
            return ResponseEntity.ok(Map.of(
                "message", "Баланс обновлен",
                "newBalance", user.getBalance()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
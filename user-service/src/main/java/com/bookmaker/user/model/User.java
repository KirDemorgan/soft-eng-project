package com.bookmaker.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Schema(description = "Модель пользователя букмекерской конторы")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Уникальный идентификатор пользователя", example = "1")
    private Long id;
    
    @Column(unique = true, nullable = false)
    @Schema(description = "Имя пользователя", example = "testuser", required = true)
    private String username;
    
    @Column(nullable = false)
    @Schema(description = "Пароль пользователя (хешированный)", hidden = true)
    private String password;
    
    @Column(unique = true, nullable = false)
    @Schema(description = "Email пользователя", example = "test@example.com", required = true)
    private String email;
    
    @Column(nullable = false)
    @Schema(description = "Баланс пользователя", example = "1000.00")
    private BigDecimal balance = BigDecimal.ZERO;
    
    @Column(name = "created_at")
    @Schema(description = "Дата и время создания аккаунта", example = "2025-09-20T15:30:00")
    private LocalDateTime createdAt;
    
    @Column(name = "is_active")
    @Schema(description = "Статус активности пользователя", example = "true")
    private Boolean isActive = true;

    // Конструктор по умолчанию для JPA
    public User() {}

    // Приватный конструктор для Builder
    private User(Builder builder) {
        this.username = builder.username;
        this.password = builder.password;
        this.email = builder.email;
        this.balance = builder.balance;
        this.createdAt = builder.createdAt;
        this.isActive = builder.isActive;
    }

    // Builder Pattern
    public static class Builder {
        private String username;
        private String password;
        private String email;
        private BigDecimal balance = BigDecimal.ZERO;
        private LocalDateTime createdAt = LocalDateTime.now();
        private Boolean isActive = true;

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder balance(BigDecimal balance) {
            this.balance = balance;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
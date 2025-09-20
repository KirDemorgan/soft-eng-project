package com.bookmaker.betting.factory;

import com.bookmaker.betting.model.Bet;
import com.bookmaker.betting.model.BetType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class StandardBetFactory implements BetFactory {
    
    @Override
    public Bet createBet(Long userId, Long eventId, BetType type, BigDecimal amount, BigDecimal odds) {
        validateBetParameters(userId, eventId, type, amount, odds);
        
        Bet bet = new Bet(userId, eventId, type, amount, odds);
        
        // Дополнительная логика в зависимости от типа ставки
        switch (type) {
            case WIN_HOME:
            case WIN_AWAY:
            case DRAW:
                // Стандартная ставка на исход
                break;
            case OVER_UNDER:
                // Ставка на тотал
                validateTotalBet(odds);
                break;
            case HANDICAP:
                // Ставка с форой
                validateHandicapBet(odds);
                break;
        }
        
        return bet;
    }
    
    private void validateBetParameters(Long userId, Long eventId, BetType type, 
                                     BigDecimal amount, BigDecimal odds) {
        if (userId == null || eventId == null || type == null) {
            throw new IllegalArgumentException("Обязательные параметры не могут быть null");
        }
        
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма ставки должна быть больше 0");
        }
        
        if (odds == null || odds.compareTo(BigDecimal.ONE) < 0) {
            throw new IllegalArgumentException("Коэффициент должен быть больше или равен 1");
        }
    }
    
    private void validateTotalBet(BigDecimal odds) {
        // Дополнительная валидация для ставок на тотал
        if (odds.compareTo(BigDecimal.valueOf(1.1)) < 0) {
            throw new IllegalArgumentException("Коэффициент для тотала слишком низкий");
        }
    }
    
    private void validateHandicapBet(BigDecimal odds) {
        // Дополнительная валидация для ставок с форой
        if (odds.compareTo(BigDecimal.valueOf(1.05)) < 0) {
            throw new IllegalArgumentException("Коэффициент для форы слишком низкий");
        }
    }
}
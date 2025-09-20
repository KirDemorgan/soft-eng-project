package com.bookmaker.betting.strategy;

import com.bookmaker.betting.model.Bet;
import com.bookmaker.betting.model.BetType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class StandardPayoutStrategy implements PayoutStrategy {
    
    @Override
    public BigDecimal calculatePayout(Bet bet, String eventResult) {
        if (isWinningBet(bet, eventResult)) {
            return bet.getAmount().multiply(bet.getOdds());
        }
        return BigDecimal.ZERO;
    }
    
    private boolean isWinningBet(Bet bet, String eventResult) {
        BetType betType = bet.getType();
        
        switch (betType) {
            case WIN_HOME:
                return "HOME_WIN".equals(eventResult);
            case WIN_AWAY:
                return "AWAY_WIN".equals(eventResult);
            case DRAW:
                return "DRAW".equals(eventResult);
            case OVER_UNDER:
                return checkOverUnder(eventResult);
            case HANDICAP:
                return checkHandicap(eventResult);
            default:
                return false;
        }
    }
    
    private boolean checkOverUnder(String eventResult) {
        // Упрощенная логика для тотала
        // В реальности здесь была бы более сложная логика
        return eventResult.startsWith("OVER") || eventResult.startsWith("UNDER");
    }
    
    private boolean checkHandicap(String eventResult) {
        // Упрощенная логика для форы
        return eventResult.startsWith("HANDICAP");
    }
}
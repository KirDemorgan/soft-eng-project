package com.bookmaker.betting.factory;

import com.bookmaker.betting.model.Bet;
import com.bookmaker.betting.model.BetType;
import java.math.BigDecimal;

// Factory Method Pattern - создание различных типов ставок
public interface BetFactory {
    Bet createBet(Long userId, Long eventId, BetType type, BigDecimal amount, BigDecimal odds);
}
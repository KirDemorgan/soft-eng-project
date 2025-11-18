package com.bookmaker.betting.strategy;

import com.bookmaker.betting.model.Bet;
import java.math.BigDecimal;

public interface PayoutStrategy {
    BigDecimal calculatePayout(Bet bet, String eventResult);
}
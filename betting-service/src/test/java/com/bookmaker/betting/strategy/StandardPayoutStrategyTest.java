package com.bookmaker.betting.strategy;

import com.bookmaker.betting.model.Bet;
import com.bookmaker.betting.model.BetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class StandardPayoutStrategyTest {

    private StandardPayoutStrategy payoutStrategy;

    @BeforeEach
    void setUp() {
        payoutStrategy = new StandardPayoutStrategy();
    }

    @Test
    void calculatePayout_WinHome_HomeWinResult_ShouldReturnPayout() {
        Bet bet = new Bet(1L, 1L, BetType.WIN_HOME, BigDecimal.valueOf(100), BigDecimal.valueOf(2.10));
        String eventResult = "HOME_WIN";

        BigDecimal result = payoutStrategy.calculatePayout(bet, eventResult);

        assertEquals(BigDecimal.valueOf(210.00), result);
    }

    @Test
    void calculatePayout_WinHome_AwayWinResult_ShouldReturnZero() {
        Bet bet = new Bet(1L, 1L, BetType.WIN_HOME, BigDecimal.valueOf(100), BigDecimal.valueOf(2.10));
        String eventResult = "AWAY_WIN";

        BigDecimal result = payoutStrategy.calculatePayout(bet, eventResult);

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void calculatePayout_WinAway_AwayWinResult_ShouldReturnPayout() {
        Bet bet = new Bet(1L, 1L, BetType.WIN_AWAY, BigDecimal.valueOf(50), BigDecimal.valueOf(3.40));
        String eventResult = "AWAY_WIN";

        BigDecimal result = payoutStrategy.calculatePayout(bet, eventResult);

        assertEquals(BigDecimal.valueOf(170.00), result);
    }    @
Test
    void calculatePayout_WinAway_HomeWinResult_ShouldReturnZero() {
        Bet bet = new Bet(1L, 1L, BetType.WIN_AWAY, BigDecimal.valueOf(50), BigDecimal.valueOf(3.40));
        String eventResult = "HOME_WIN";

        BigDecimal result = payoutStrategy.calculatePayout(bet, eventResult);

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void calculatePayout_Draw_DrawResult_ShouldReturnPayout() {
        Bet bet = new Bet(1L, 1L, BetType.DRAW, BigDecimal.valueOf(75), BigDecimal.valueOf(3.20));
        String eventResult = "DRAW";

        BigDecimal result = payoutStrategy.calculatePayout(bet, eventResult);

        assertEquals(BigDecimal.valueOf(240.00), result);
    }

    @Test
    void calculatePayout_Draw_HomeWinResult_ShouldReturnZero() {
        Bet bet = new Bet(1L, 1L, BetType.DRAW, BigDecimal.valueOf(75), BigDecimal.valueOf(3.20));
        String eventResult = "HOME_WIN";

        BigDecimal result = payoutStrategy.calculatePayout(bet, eventResult);

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void calculatePayout_OverUnder_OverResult_ShouldReturnPayout() {
        Bet bet = new Bet(1L, 1L, BetType.OVER_UNDER, BigDecimal.valueOf(100), BigDecimal.valueOf(1.85));
        String eventResult = "OVER_2_5";

        BigDecimal result = payoutStrategy.calculatePayout(bet, eventResult);

        assertEquals(BigInteger.valueOf(185), result.toBigInteger());
    }

    @Test
    void calculatePayout_OverUnder_UnderResult_ShouldReturnPayout() {
        Bet bet = new Bet(1L, 1L, BetType.OVER_UNDER, BigDecimal.valueOf(100), BigDecimal.valueOf(1.95));
        String eventResult = "UNDER_2_5";

        BigDecimal result = payoutStrategy.calculatePayout(bet, eventResult);

        assertEquals(BigInteger.valueOf(195), result.toBigInteger());
    }

    @Test
    void calculatePayout_OverUnder_InvalidResult_ShouldReturnZero() {
        Bet bet = new Bet(1L, 1L, BetType.OVER_UNDER, BigDecimal.valueOf(100), BigDecimal.valueOf(1.85));
        String eventResult = "HOME_WIN";

        BigDecimal result = payoutStrategy.calculatePayout(bet, eventResult);

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void calculatePayout_Handicap_HandicapResult_ShouldReturnPayout() {
        Bet bet = new Bet(1L, 1L, BetType.HANDICAP, BigDecimal.valueOf(200), BigDecimal.valueOf(1.75));
        String eventResult = "HANDICAP_HOME_WIN";

        BigDecimal result = payoutStrategy.calculatePayout(bet, eventResult);

        assertEquals(BigInteger.valueOf(350), result.toBigInteger());
    }

    @Test
    void calculatePayout_Handicap_InvalidResult_ShouldReturnZero() {
        Bet bet = new Bet(1L, 1L, BetType.HANDICAP, BigDecimal.valueOf(200), BigDecimal.valueOf(1.75));
        String eventResult = "DRAW";

        BigDecimal result = payoutStrategy.calculatePayout(bet, eventResult);

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void calculatePayout_ZeroAmount_ShouldReturnZero() {
        Bet bet = new Bet(1L, 1L, BetType.WIN_HOME, BigDecimal.ZERO, BigDecimal.valueOf(2.10));
        String eventResult = "HOME_WIN";

        BigDecimal result = payoutStrategy.calculatePayout(bet, eventResult);

        assertEquals(0, result.toBigInteger().intValue());
    }

    @Test
    void calculatePayout_HighOdds_ShouldCalculateCorrectly() {
        Bet bet = new Bet(1L, 1L, BetType.WIN_AWAY, BigDecimal.valueOf(10), BigDecimal.valueOf(15.50));
        String eventResult = "AWAY_WIN";

        BigDecimal result = payoutStrategy.calculatePayout(bet, eventResult);

        assertEquals(BigDecimal.valueOf(155.00), result);
    }
}
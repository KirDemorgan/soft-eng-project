package com.bookmaker.betting.factory;

import com.bookmaker.betting.model.Bet;
import com.bookmaker.betting.model.BetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class StandardBetFactoryTest {

    private StandardBetFactory betFactory;

    @BeforeEach
    void setUp() {
        betFactory = new StandardBetFactory();
    }

    @Test
    void createBet_WinHome_Success() {
        Long userId = 1L;
        Long eventId = 1L;
        BetType type = BetType.WIN_HOME;
        BigDecimal amount = BigDecimal.valueOf(100);
        BigDecimal odds = BigDecimal.valueOf(2.10);

        Bet result = betFactory.createBet(userId, eventId, type, amount, odds);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(eventId, result.getEventId());
        assertEquals(type, result.getType());
        assertEquals(amount, result.getAmount());
        assertEquals(odds, result.getOdds());
    }

    @Test
    void createBet_WinAway_Success() {
        Long userId = 1L;
        Long eventId = 1L;
        BetType type = BetType.WIN_AWAY;
        BigDecimal amount = BigDecimal.valueOf(50);
        BigDecimal odds = BigDecimal.valueOf(3.40);

        Bet result = betFactory.createBet(userId, eventId, type, amount, odds);

        assertNotNull(result);
        assertEquals(type, result.getType());
        assertEquals(amount, result.getAmount());
        assertEquals(odds, result.getOdds());
    }

    @Test
    void createBet_Draw_Success() {
        Long userId = 1L;
        Long eventId = 1L;
        BetType type = BetType.DRAW;
        BigDecimal amount = BigDecimal.valueOf(75);
        BigDecimal odds = BigDecimal.valueOf(3.20);

        Bet result = betFactory.createBet(userId, eventId, type, amount, odds);

        assertNotNull(result);
        assertEquals(type, result.getType());
        assertEquals(amount, result.getAmount());
        assertEquals(odds, result.getOdds());
    }

    @Test
    void createBet_OverUnder_Success() {
        Long userId = 1L;
        Long eventId = 1L;
        BetType type = BetType.OVER_UNDER;
        BigDecimal amount = BigDecimal.valueOf(100);
        BigDecimal odds = BigDecimal.valueOf(1.85);

        Bet result = betFactory.createBet(userId, eventId, type, amount, odds);

        assertNotNull(result);
        assertEquals(type, result.getType());
        assertEquals(amount, result.getAmount());
        assertEquals(odds, result.getOdds());
    }

    @Test
    void createBet_OverUnder_LowOdds_ShouldThrowException() {
        Long userId = 1L;
        Long eventId = 1L;
        BetType type = BetType.OVER_UNDER;
        BigDecimal amount = BigDecimal.valueOf(100);
        BigDecimal odds = BigDecimal.valueOf(1.05);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                betFactory.createBet(userId, eventId, type, amount, odds));

        assertEquals("Коэффициент для тотала слишком низкий", exception.getMessage());
    }

    @Test
    void createBet_Handicap_Success() {
        Long userId = 1L;
        Long eventId = 1L;
        BetType type = BetType.HANDICAP;
        BigDecimal amount = BigDecimal.valueOf(100);
        BigDecimal odds = BigDecimal.valueOf(1.95);

        Bet result = betFactory.createBet(userId, eventId, type, amount, odds);

        assertNotNull(result);
        assertEquals(type, result.getType());
        assertEquals(amount, result.getAmount());
        assertEquals(odds, result.getOdds());
    }

    @Test
    void createBet_Handicap_LowOdds_ShouldThrowException() {
        Long userId = 1L;
        Long eventId = 1L;
        BetType type = BetType.HANDICAP;
        BigDecimal amount = BigDecimal.valueOf(100);
        BigDecimal odds = BigDecimal.valueOf(1.04);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                betFactory.createBet(userId, eventId, type, amount, odds));

        assertEquals("Коэффициент для форы слишком низкий", exception.getMessage());
    }

    @Test
    void createBet_NullUserId_ShouldThrowException() {
        Long userId = null;
        Long eventId = 1L;
        BetType type = BetType.WIN_HOME;
        BigDecimal amount = BigDecimal.valueOf(100);
        BigDecimal odds = BigDecimal.valueOf(2.10);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                betFactory.createBet(userId, eventId, type, amount, odds));

        assertEquals("Обязательные параметры не могут быть null", exception.getMessage());
    }

    @Test
    void createBet_NullEventId_ShouldThrowException() {
        Long userId = 1L;
        Long eventId = null;
        BetType type = BetType.WIN_HOME;
        BigDecimal amount = BigDecimal.valueOf(100);
        BigDecimal odds = BigDecimal.valueOf(2.10);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                betFactory.createBet(userId, eventId, type, amount, odds));

        assertEquals("Обязательные параметры не могут быть null", exception.getMessage());
    }

    @Test
    void createBet_NullBetType_ShouldThrowException() {
        Long userId = 1L;
        Long eventId = 1L;
        BetType type = null;
        BigDecimal amount = BigDecimal.valueOf(100);
        BigDecimal odds = BigDecimal.valueOf(2.10);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                betFactory.createBet(userId, eventId, type, amount, odds));

        assertEquals("Обязательные параметры не могут быть null", exception.getMessage());
    }

    @Test
    void createBet_ZeroAmount_ShouldThrowException() {
        Long userId = 1L;
        Long eventId = 1L;
        BetType type = BetType.WIN_HOME;
        BigDecimal amount = BigDecimal.ZERO;
        BigDecimal odds = BigDecimal.valueOf(2.10);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                betFactory.createBet(userId, eventId, type, amount, odds));

        assertEquals("Сумма ставки должна быть больше 0", exception.getMessage());
    }

    @Test
    void createBet_NegativeAmount_ShouldThrowException() {
        Long userId = 1L;
        Long eventId = 1L;
        BetType type = BetType.WIN_HOME;
        BigDecimal amount = BigDecimal.valueOf(-100);
        BigDecimal odds = BigDecimal.valueOf(2.10);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                betFactory.createBet(userId, eventId, type, amount, odds));

        assertEquals("Сумма ставки должна быть больше 0", exception.getMessage());
    }

    @Test
    void createBet_LowOdds_ShouldThrowException() {
        Long userId = 1L;
        Long eventId = 1L;
        BetType type = BetType.WIN_HOME;
        BigDecimal amount = BigDecimal.valueOf(100);
        BigDecimal odds = BigDecimal.valueOf(0.95);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                betFactory.createBet(userId, eventId, type, amount, odds));

        assertEquals("Коэффициент должен быть больше или равен 1", exception.getMessage());
    }

    @Test
    void createBet_NullOdds_ShouldThrowException() {
        Long userId = 1L;
        Long eventId = 1L;
        BetType type = BetType.WIN_HOME;
        BigDecimal amount = BigDecimal.valueOf(100);
        BigDecimal odds = null;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                betFactory.createBet(userId, eventId, type, amount, odds));

        assertEquals("Коэффициент должен быть больше или равен 1", exception.getMessage());
    }
}
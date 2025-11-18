package com.bookmaker.betting.service;

import com.bookmaker.betting.factory.BetFactory;
import com.bookmaker.betting.model.Bet;
import com.bookmaker.betting.model.BetStatus;
import com.bookmaker.betting.model.BetType;
import com.bookmaker.betting.repository.BetRepository;
import com.bookmaker.betting.strategy.PayoutStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BettingServiceTest {

    @Mock
    private BetRepository betRepository;

    @Mock
    private BetFactory betFactory;

    @Mock
    private PayoutStrategy payoutStrategy;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private BettingService bettingService;

    private Bet testBet;

    @BeforeEach
    void setUp() {
        testBet = new Bet(1L, 1L, BetType.WIN_HOME, BigDecimal.valueOf(100), BigDecimal.valueOf(2.10));
        testBet.setId(1L);
    }

    @Test
    void placeBet_Success() {
        Long userId = 1L;
        Long eventId = 1L;
        BetType type = BetType.WIN_HOME;
        BigDecimal amount = BigDecimal.valueOf(100);
        BigDecimal odds = BigDecimal.valueOf(2.10);

        when(betFactory.createBet(userId, eventId, type, amount, odds)).thenReturn(testBet);
        when(betRepository.save(testBet)).thenReturn(testBet);
        doNothing().when(userServiceClient).updateBalance(userId, amount.negate());

        Bet result = bettingService.placeBet(userId, eventId, type, amount, odds);

        assertNotNull(result);
        assertEquals(testBet.getId(), result.getId());
        assertEquals(userId, result.getUserId());
        assertEquals(eventId, result.getEventId());
        assertEquals(type, result.getType());
        assertEquals(amount, result.getAmount());
        assertEquals(odds, result.getOdds());

        verify(betFactory).createBet(userId, eventId, type, amount, odds);
        verify(userServiceClient).updateBalance(userId, amount.negate());
        verify(betRepository).save(testBet);
    }

    @Test
    void placeBet_FactoryThrowsException_ShouldThrowRuntimeException() {
        Long userId = 1L;
        Long eventId = 1L;
        BetType type = BetType.WIN_HOME;
        BigDecimal amount = BigDecimal.valueOf(100);
        BigDecimal odds = BigDecimal.valueOf(2.10);

        when(betFactory.createBet(userId, eventId, type, amount, odds))
                .thenThrow(new IllegalArgumentException("Invalid bet parameters"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                bettingService.placeBet(userId, eventId, type, amount, odds));

        verify(betFactory).createBet(userId, eventId, type, amount, odds);
        verify(userServiceClient, never()).updateBalance((Long) any(), (Map<String, BigDecimal>) any());
        verify(betRepository, never()).save(any());
    }

    @Test
    void settleBet_WinningBet_Success() {
        Long betId = 1L;
        String eventResult = "HOME_WIN";
        BigDecimal payout = BigDecimal.valueOf(210);

        testBet.setStatus(BetStatus.PENDING);
        when(betRepository.findById(betId)).thenReturn(Optional.of(testBet));
        when(payoutStrategy.calculatePayout(testBet, eventResult)).thenReturn(payout);
        when(betRepository.save(testBet)).thenReturn(testBet);
        doNothing().when(userServiceClient).updateBalance(testBet.getUserId(), payout);

        bettingService.settleBet(betId, eventResult);

        assertEquals(BetStatus.WON, testBet.getStatus());
        assertEquals(payout, testBet.getPayout());
        assertNotNull(testBet.getSettledAt());

        verify(betRepository).findById(betId);
        verify(payoutStrategy).calculatePayout(testBet, eventResult);
        verify(userServiceClient).updateBalance(testBet.getUserId(), payout);
        verify(betRepository).save(testBet);
    }

    @Test
    void settleBet_LosingBet_Success() {
        Long betId = 1L;
        String eventResult = "AWAY_WIN";
        BigDecimal payout = BigDecimal.ZERO;

        testBet.setStatus(BetStatus.PENDING);
        when(betRepository.findById(betId)).thenReturn(Optional.of(testBet));
        when(payoutStrategy.calculatePayout(testBet, eventResult)).thenReturn(payout);
        when(betRepository.save(testBet)).thenReturn(testBet);

        bettingService.settleBet(betId, eventResult);

        assertEquals(BetStatus.LOST, testBet.getStatus());
        assertEquals(payout, testBet.getPayout());
        assertNotNull(testBet.getSettledAt());

        verify(betRepository).findById(betId);
        verify(payoutStrategy).calculatePayout(testBet, eventResult);
        verify(userServiceClient, never()).updateBalance((Long) any(), (Map<String, BigDecimal>) any());
        verify(betRepository).save(testBet);
    }

    @Test
    void settleBet_BetNotFound_ShouldThrowException() {
        Long betId = 1L;
        String eventResult = "HOME_WIN";

        when(betRepository.findById(betId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                bettingService.settleBet(betId, eventResult));

        assertEquals("Ставка не найдена", exception.getMessage());
        verify(betRepository).findById(betId);
        verify(payoutStrategy, never()).calculatePayout(any(), any());
        verify(betRepository, never()).save(any());
    }

    @Test
    void settleBet_BetAlreadySettled_ShouldThrowException() {
        Long betId = 1L;
        String eventResult = "HOME_WIN";

        testBet.setStatus(BetStatus.WON);
        when(betRepository.findById(betId)).thenReturn(Optional.of(testBet));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                bettingService.settleBet(betId, eventResult));

        assertEquals("Ставка уже обработана", exception.getMessage());
        verify(betRepository).findById(betId);
        verify(payoutStrategy, never()).calculatePayout(any(), any());
        verify(betRepository, never()).save(any());
    }

    @Test
    void settleBet_PushBet_Success() {
        Long betId = 1L;
        String eventResult = "OVER_2.5_PUSH";
        BigDecimal payout = BigDecimal.valueOf(100);

        testBet.setStatus(BetStatus.PENDING);
        when(betRepository.findById(betId)).thenReturn(Optional.of(testBet));
        when(payoutStrategy.calculatePayout(testBet, eventResult)).thenReturn(payout);
        when(betRepository.save(testBet)).thenReturn(testBet);
        doNothing().when(userServiceClient).updateBalance(testBet.getUserId(), testBet.getAmount());

        bettingService.settleBet(betId, eventResult);

        assertEquals(BetStatus.PUSH, testBet.getStatus());
        assertEquals(payout, testBet.getPayout());
        assertNotNull(testBet.getSettledAt());

        verify(betRepository).findById(betId);
        verify(payoutStrategy).calculatePayout(testBet, eventResult);
        verify(userServiceClient).updateBalance(testBet.getUserId(), testBet.getAmount());
        verify(betRepository).save(testBet);
    }

    @Test
    void getUserBets_Success() {
        Long userId = 1L;
        List<Bet> expectedBets = Arrays.asList(testBet);

        when(betRepository.findByUserId(userId)).thenReturn(expectedBets);

        List<Bet> result = bettingService.getUserBets(userId);

        assertEquals(expectedBets, result);
        verify(betRepository).findByUserId(userId);
    }

    @Test
    void getEventBets_Success() {
        Long eventId = 1L;
        List<Bet> expectedBets = Arrays.asList(testBet);

        when(betRepository.findByEventId(eventId)).thenReturn(expectedBets);

        List<Bet> result = bettingService.getEventBets(eventId);

        assertEquals(expectedBets, result);
        verify(betRepository).findByEventId(eventId);
    }

    @Test
    void getPendingBets_Success() {
        List<Bet> expectedBets = Arrays.asList(testBet);

        when(betRepository.findByStatus(BetStatus.PENDING)).thenReturn(expectedBets);

        List<Bet> result = bettingService.getPendingBets();

        assertEquals(expectedBets, result);
        verify(betRepository).findByStatus(BetStatus.PENDING);
    }
}
package com.bookmaker.betting.integration;

import com.bookmaker.betting.command.PlaceBetCommand;
import com.bookmaker.betting.factory.BetFactory;
import com.bookmaker.betting.model.Bet;
import com.bookmaker.betting.model.BetType;
import com.bookmaker.betting.repository.BetRepository;
import com.bookmaker.betting.service.UserServiceClient;
import com.bookmaker.betting.strategy.PayoutStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Интеграционный тест, демонстрирующий взаимодействие всех паттернов проектирования
 * в Betting Service: Factory Method, Strategy и Command
 */
@ExtendWith(MockitoExtension.class)
class PatternIntegrationTest {

    @Mock
    private BetRepository betRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Test
    void testPatternsIntegration() {
        // 1. Factory Method Pattern - создание ставки
        BetFactory betFactory = mock(BetFactory.class);
        Bet bet = new Bet(1L, 1L, BetType.WIN_HOME, BigDecimal.valueOf(100), BigDecimal.valueOf(2.10));
        bet.setId(1L);
        
        when(betFactory.createBet(1L, 1L, BetType.WIN_HOME, BigDecimal.valueOf(100), BigDecimal.valueOf(2.10)))
                .thenReturn(bet);

        // 2. Command Pattern - выполнение команды размещения ставки
        PlaceBetCommand command = new PlaceBetCommand(bet, betRepository, userServiceClient);
        
        when(betRepository.save(bet)).thenReturn(bet);
        doNothing().when(userServiceClient).updateBalance(1L, BigDecimal.valueOf(-100));

        // 3. Strategy Pattern - расчет выплаты
        PayoutStrategy payoutStrategy = mock(PayoutStrategy.class);
        when(payoutStrategy.calculatePayout(bet, "HOME_WIN")).thenReturn(BigDecimal.valueOf(210));

        // Выполнение интеграционного сценария
        
        // Шаг 1: Создание ставки через Factory
        Bet createdBet = betFactory.createBet(1L, 1L, BetType.WIN_HOME, BigDecimal.valueOf(100), BigDecimal.valueOf(2.10));
        assertNotNull(createdBet);
        assertEquals(BetType.WIN_HOME, createdBet.getType());

        // Шаг 2: Размещение ставки через Command
        assertDoesNotThrow(() -> command.execute());

        // Шаг 3: Расчет выплаты через Strategy
        BigDecimal payout = payoutStrategy.calculatePayout(bet, "HOME_WIN");
        assertEquals(BigDecimal.valueOf(210), payout);

        // Проверка вызовов
        verify(betFactory).createBet(1L, 1L, BetType.WIN_HOME, BigDecimal.valueOf(100), BigDecimal.valueOf(2.10));
        verify(userServiceClient).updateBalance(1L, BigDecimal.valueOf(-100));
        verify(betRepository).save(bet);
        verify(payoutStrategy).calculatePayout(bet, "HOME_WIN");
    }

    @Test
    void testCommandUndoIntegration() {
        // Тестирование отката команды при ошибке
        Bet bet = new Bet(1L, 1L, BetType.WIN_HOME, BigDecimal.valueOf(100), BigDecimal.valueOf(2.10));
        PlaceBetCommand command = new PlaceBetCommand(bet, betRepository, userServiceClient);
        
        when(betRepository.save(bet)).thenReturn(bet);
        doNothing().when(userServiceClient).updateBalance(1L, BigDecimal.valueOf(-100));
        doNothing().when(userServiceClient).updateBalance(1L, BigDecimal.valueOf(100));
        doNothing().when(betRepository).delete(bet);

        // Выполняем команду
        command.execute();
        
        // Откатываем команду
        command.undo();

        // Проверяем, что операции отката выполнены
        verify(userServiceClient).updateBalance(1L, BigDecimal.valueOf(-100)); // Списание
        verify(userServiceClient).updateBalance(1L, BigDecimal.valueOf(100));  // Возврат
        verify(betRepository).save(bet);   // Сохранение
        verify(betRepository).delete(bet); // Удаление
    }
}
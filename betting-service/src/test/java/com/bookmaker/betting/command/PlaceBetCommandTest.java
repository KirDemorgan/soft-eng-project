package com.bookmaker.betting.command;

import com.bookmaker.betting.model.Bet;
import com.bookmaker.betting.model.BetType;
import com.bookmaker.betting.repository.BetRepository;
import com.bookmaker.betting.service.UserServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaceBetCommandTest {

    @Mock
    private BetRepository betRepository;

    @Mock
    private UserServiceClient userServiceClient;

    private PlaceBetCommand command;
    private Bet testBet;

    @BeforeEach
    void setUp() {
        testBet = new Bet(1L, 1L, BetType.WIN_HOME, BigDecimal.valueOf(100), BigDecimal.valueOf(2.10));
        testBet.setId(1L);
        command = new PlaceBetCommand(testBet, betRepository, userServiceClient);
    }

    @Test
    void execute_Success() {
        // Given
        when(betRepository.save(testBet)).thenReturn(testBet);
        doNothing().when(userServiceClient).updateBalance(testBet.getUserId(), testBet.getAmount().negate());

        // When
        command.execute();

        // Then
        verify(userServiceClient).updateBalance(testBet.getUserId(), testBet.getAmount().negate());
        verify(betRepository).save(testBet);
    }

    @Test
    void execute_AlreadyExecuted_ShouldThrowException() {
        // Given
        when(betRepository.save(testBet)).thenReturn(testBet);
        doNothing().when(userServiceClient).updateBalance(testBet.getUserId(), testBet.getAmount().negate());
        
        command.execute(); // Первое выполнение

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                command.execute());

        assertEquals("Команда уже выполнена", exception.getMessage());
        
        // Проверяем, что методы вызывались только один раз
        verify(userServiceClient, times(1)).updateBalance(testBet.getUserId(), testBet.getAmount().negate());
        verify(betRepository, times(1)).save(testBet);
    } 
   @Test
    void undo_AfterExecute_Success() {
        // Given
        when(betRepository.save(testBet)).thenReturn(testBet);
        doNothing().when(userServiceClient).updateBalance(testBet.getUserId(), testBet.getAmount().negate());
        doNothing().when(userServiceClient).updateBalance(testBet.getUserId(), testBet.getAmount());
        doNothing().when(betRepository).delete(testBet);
        
        command.execute(); // Сначала выполняем команду

        // When
        command.undo();

        // Then
        verify(userServiceClient).updateBalance(testBet.getUserId(), testBet.getAmount()); // Возврат средств
        verify(betRepository).delete(testBet);
    }

    @Test
    void undo_NotExecuted_ShouldThrowException() {
        // Given - команда не выполнена

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                command.undo());

        assertEquals("Команда не была выполнена", exception.getMessage());
        
        // Проверяем, что методы не вызывались
        verify(userServiceClient, never()).updateBalance((Long) any(), (Map<String, BigDecimal>) any());
        verify(betRepository, never()).delete(any());
    }

    @Test
    void execute_UserServiceThrowsException_ShouldPropagateException() {
        // Given
        RuntimeException userServiceException = new RuntimeException("Недостаточно средств");
        doThrow(userServiceException).when(userServiceClient)
                .updateBalance(testBet.getUserId(), testBet.getAmount().negate());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                command.execute());

        assertEquals("Недостаточно средств", exception.getMessage());
        
        verify(userServiceClient).updateBalance(testBet.getUserId(), testBet.getAmount().negate());
        verify(betRepository, never()).save(testBet); // Ставка не должна сохраниться при ошибке
    }

    @Test
    void execute_RepositoryThrowsException_ShouldPropagateException() {
        // Given
        doNothing().when(userServiceClient).updateBalance(testBet.getUserId(), testBet.getAmount().negate());
        RuntimeException repositoryException = new RuntimeException("Ошибка базы данных");
        when(betRepository.save(testBet)).thenThrow(repositoryException);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                command.execute());

        assertEquals("Ошибка базы данных", exception.getMessage());
        
        verify(userServiceClient).updateBalance(testBet.getUserId(), testBet.getAmount().negate());
        verify(betRepository).save(testBet);
    }
}
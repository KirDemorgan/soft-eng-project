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
        when(betRepository.save(testBet)).thenReturn(testBet);
        doNothing().when(userServiceClient).updateBalance(testBet.getUserId(), testBet.getAmount().negate());

        command.execute();

        verify(userServiceClient).updateBalance(testBet.getUserId(), testBet.getAmount().negate());
        verify(betRepository).save(testBet);
    }

    @Test
    void execute_AlreadyExecuted_ShouldThrowException() {
        when(betRepository.save(testBet)).thenReturn(testBet);
        doNothing().when(userServiceClient).updateBalance(testBet.getUserId(), testBet.getAmount().negate());
        
        command.execute();

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                command.execute());

        assertEquals("Команда уже выполнена", exception.getMessage());
        
        verify(userServiceClient, times(1)).updateBalance(testBet.getUserId(), testBet.getAmount().negate());
        verify(betRepository, times(1)).save(testBet);
    } 
   @Test
    void undo_AfterExecute_Success() {
        when(betRepository.save(testBet)).thenReturn(testBet);
        doNothing().when(userServiceClient).updateBalance(testBet.getUserId(), testBet.getAmount().negate());
        doNothing().when(userServiceClient).updateBalance(testBet.getUserId(), testBet.getAmount());
        doNothing().when(betRepository).delete(testBet);
        
        command.execute();

        command.undo();

        verify(userServiceClient).updateBalance(testBet.getUserId(), testBet.getAmount());
        verify(betRepository).delete(testBet);
    }

    @Test
    void undo_NotExecuted_ShouldThrowException() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                command.undo());

        assertEquals("Команда не была выполнена", exception.getMessage());
        
        verify(userServiceClient, never()).updateBalance((Long) any(), (Map<String, BigDecimal>) any());
        verify(betRepository, never()).delete(any());
    }

    @Test
    void execute_UserServiceThrowsException_ShouldPropagateException() {
        RuntimeException userServiceException = new RuntimeException("Insufficient funds");
        doThrow(userServiceException).when(userServiceClient)
                .updateBalance(testBet.getUserId(), testBet.getAmount().negate());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                command.execute());

        assertEquals("Insufficient funds", exception.getMessage());
        
        verify(userServiceClient).updateBalance(testBet.getUserId(), testBet.getAmount().negate());
        verify(betRepository, never()).save(testBet);
    }

    @Test
    void execute_RepositoryThrowsException_ShouldPropagateException() {
        doNothing().when(userServiceClient).updateBalance(testBet.getUserId(), testBet.getAmount().negate());
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(betRepository.save(testBet)).thenThrow(repositoryException);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                command.execute());

        assertEquals("Database error", exception.getMessage());
        
        verify(userServiceClient).updateBalance(testBet.getUserId(), testBet.getAmount().negate());
        verify(betRepository).save(testBet);
    }
}
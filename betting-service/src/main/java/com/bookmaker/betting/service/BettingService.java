package com.bookmaker.betting.service;

import com.bookmaker.betting.command.PlaceBetCommand;
import com.bookmaker.betting.factory.BetFactory;
import com.bookmaker.betting.model.Bet;
import com.bookmaker.betting.model.BetStatus;
import com.bookmaker.betting.model.BetType;
import com.bookmaker.betting.repository.BetRepository;
import com.bookmaker.betting.strategy.PayoutStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BettingService {
    
    @Autowired
    private BetRepository betRepository;
    
    @Autowired
    private BetFactory betFactory;
    
    @Autowired
    private PayoutStrategy payoutStrategy;
    
    @Autowired
    private UserServiceClient userServiceClient;
    
    public Bet placeBet(Long userId, Long eventId, BetType type, BigDecimal amount, BigDecimal odds) {
        // Используем Factory Method для создания ставки
        Bet bet = betFactory.createBet(userId, eventId, type, amount, odds);
        
        // Используем Command Pattern для размещения ставки
        PlaceBetCommand command = new PlaceBetCommand(bet, betRepository, userServiceClient);
        
        try {
            command.execute();
            return bet;
        } catch (Exception e) {
            // В случае ошибки откатываем операцию
            try {
                command.undo();
            } catch (Exception undoException) {
                // Логируем ошибку отката
            }
            throw new RuntimeException("Не удалось разместить ставку: " + e.getMessage());
        }
    }
    
    public void settleBet(Long betId, String eventResult) {
        Bet bet = betRepository.findById(betId)
                .orElseThrow(() -> new RuntimeException("Ставка не найдена"));
        
        if (bet.getStatus() != BetStatus.PENDING) {
            throw new RuntimeException("Ставка уже обработана");
        }
        
        // Используем Strategy Pattern для расчета выплаты
        BigDecimal payout = payoutStrategy.calculatePayout(bet, eventResult);
        
        bet.setPayout(payout);
        bet.setSettledAt(LocalDateTime.now());
        
        if (payout.compareTo(BigDecimal.ZERO) > 0) {
            bet.setStatus(BetStatus.WON);
            // Начисляем выигрыш на баланс пользователя
            userServiceClient.updateBalance(bet.getUserId(), payout);
        } else {
            bet.setStatus(BetStatus.LOST);
        }
        
        betRepository.save(bet);
    }
    
    public List<Bet> getUserBets(Long userId) {
        return betRepository.findByUserId(userId);
    }
    
    public List<Bet> getEventBets(Long eventId) {
        return betRepository.findByEventId(eventId);
    }
    
    public List<Bet> getPendingBets() {
        return betRepository.findByStatus(BetStatus.PENDING);
    }
}
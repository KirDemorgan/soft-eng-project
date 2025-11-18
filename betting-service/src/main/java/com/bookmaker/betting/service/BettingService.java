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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BettingService {
    private static final Logger log = LoggerFactory.getLogger(BettingService.class);
    
    @Autowired
    private BetRepository betRepository;
    
    @Autowired
    private BetFactory betFactory;
    
    @Autowired
    private PayoutStrategy payoutStrategy;
    
    @Autowired
    private UserServiceClient userServiceClient;
    
    public Bet placeBet(Long userId, Long eventId, BetType type, BigDecimal amount, BigDecimal odds) {
        Bet bet = betFactory.createBet(userId, eventId, type, amount, odds);
        
        PlaceBetCommand command = new PlaceBetCommand(bet, betRepository, userServiceClient);
        
        try {
            command.execute();
            return bet;
        } catch (Exception e) {
            try {
                command.undo();
            } catch (Exception undoException) {
                log.error("Error undoing place bet command", undoException);
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
        
        BigDecimal payout = payoutStrategy.calculatePayout(bet, eventResult);
        
        bet.setPayout(payout);
        bet.setSettledAt(LocalDateTime.now());
        
        if (payout.compareTo(bet.getAmount()) == 0 && !payout.equals(BigDecimal.ZERO)) {
            bet.setStatus(BetStatus.PUSH);
            userServiceClient.updateBalance(bet.getUserId(), bet.getAmount());
        } else if (payout.compareTo(BigDecimal.ZERO) > 0) {
            bet.setStatus(BetStatus.WON);
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
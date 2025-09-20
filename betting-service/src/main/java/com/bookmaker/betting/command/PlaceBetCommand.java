package com.bookmaker.betting.command;

import com.bookmaker.betting.model.Bet;
import com.bookmaker.betting.repository.BetRepository;
import com.bookmaker.betting.service.UserServiceClient;

import java.math.BigDecimal;

public class PlaceBetCommand implements BetCommand {
    
    private final Bet bet;
    private final BetRepository betRepository;
    private final UserServiceClient userServiceClient;
    private boolean executed = false;
    
    public PlaceBetCommand(Bet bet, BetRepository betRepository, UserServiceClient userServiceClient) {
        this.bet = bet;
        this.betRepository = betRepository;
        this.userServiceClient = userServiceClient;
    }
    
    @Override
    public void execute() {
        if (executed) {
            throw new IllegalStateException("Команда уже выполнена");
        }
        
        // Списываем средства с баланса пользователя
        userServiceClient.updateBalance(bet.getUserId(), bet.getAmount().negate());
        
        // Сохраняем ставку
        betRepository.save(bet);
        
        executed = true;
    }
    
    @Override
    public void undo() {
        if (!executed) {
            throw new IllegalStateException("Команда не была выполнена");
        }
        
        // Возвращаем средства на баланс
        userServiceClient.updateBalance(bet.getUserId(), bet.getAmount());
        
        // Удаляем ставку
        betRepository.delete(bet);
        
        executed = false;
    }
}
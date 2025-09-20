package com.bookmaker.betting.repository;

import com.bookmaker.betting.model.Bet;
import com.bookmaker.betting.model.BetStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BetRepository extends JpaRepository<Bet, Long> {
    List<Bet> findByUserId(Long userId);
    List<Bet> findByEventId(Long eventId);
    List<Bet> findByStatus(BetStatus status);
    List<Bet> findByUserIdAndStatus(Long userId, BetStatus status);
}
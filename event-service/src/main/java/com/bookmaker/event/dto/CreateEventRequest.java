package com.bookmaker.event.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CreateEventRequest {
    private String homeTeam;
    private String awayTeam;
    private LocalDateTime startTime;
    private BigDecimal homeWinOdds;
    private BigDecimal awayWinOdds;
    private BigDecimal drawOdds;

    // Getters and setters
    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public BigDecimal getHomeWinOdds() {
        return homeWinOdds;
    }

    public void setHomeWinOdds(BigDecimal homeWinOdds) {
        this.homeWinOdds = homeWinOdds;
    }

    public BigDecimal getAwayWinOdds() {
        return awayWinOdds;
    }

    public void setAwayWinOdds(BigDecimal awayWinOdds) {
        this.awayWinOdds = awayWinOdds;
    }

    public BigDecimal getDrawOdds() {
        return drawOdds;
    }

    public void setDrawOdds(BigDecimal drawOdds) {
        this.drawOdds = drawOdds;
    }
}

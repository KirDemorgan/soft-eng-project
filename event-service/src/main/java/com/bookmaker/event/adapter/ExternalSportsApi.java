package com.bookmaker.event.adapter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// Интерфейс внешнего API спортивных данных
public interface ExternalSportsApi {
    List<ExternalEvent> getUpcomingEvents();
    ExternalOdds getEventOdds(String externalEventId);
    ExternalResult getEventResult(String externalEventId);
}

// DTO для внешнего события
class ExternalEvent {
    private String id;
    private String homeTeam;
    private String awayTeam;
    private LocalDateTime startTime;
    
    // Конструкторы, геттеры и сеттеры
    public ExternalEvent() {}
    
    public ExternalEvent(String id, String homeTeam, String awayTeam, LocalDateTime startTime) {
        this.id = id;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.startTime = startTime;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getHomeTeam() { return homeTeam; }
    public void setHomeTeam(String homeTeam) { this.homeTeam = homeTeam; }
    
    public String getAwayTeam() { return awayTeam; }
    public void setAwayTeam(String awayTeam) { this.awayTeam = awayTeam; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
}

// DTO для коэффициентов
class ExternalOdds {
    private BigDecimal homeWin;
    private BigDecimal awayWin;
    private BigDecimal draw;
    
    public ExternalOdds() {}
    
    public ExternalOdds(BigDecimal homeWin, BigDecimal awayWin, BigDecimal draw) {
        this.homeWin = homeWin;
        this.awayWin = awayWin;
        this.draw = draw;
    }
    
    public BigDecimal getHomeWin() { return homeWin; }
    public void setHomeWin(BigDecimal homeWin) { this.homeWin = homeWin; }
    
    public BigDecimal getAwayWin() { return awayWin; }
    public void setAwayWin(BigDecimal awayWin) { this.awayWin = awayWin; }
    
    public BigDecimal getDraw() { return draw; }
    public void setDraw(BigDecimal draw) { this.draw = draw; }
}

// DTO для результата
class ExternalResult {
    private String result;
    private int homeScore;
    private int awayScore;
    
    public ExternalResult() {}
    
    public ExternalResult(String result, int homeScore, int awayScore) {
        this.result = result;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
    }
    
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    
    public int getHomeScore() { return homeScore; }
    public void setHomeScore(int homeScore) { this.homeScore = homeScore; }
    
    public int getAwayScore() { return awayScore; }
    public void setAwayScore(int awayScore) { this.awayScore = awayScore; }
}
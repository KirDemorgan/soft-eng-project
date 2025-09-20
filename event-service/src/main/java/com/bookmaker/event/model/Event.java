package com.bookmaker.event.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String homeTeam;
    
    @Column(nullable = false)
    private String awayTeam;
    
    @Column(nullable = false)
    private LocalDateTime startTime;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status = EventStatus.ACTIVE;
    
    @Column(precision = 5, scale = 2)
    private BigDecimal homeWinOdds;
    
    @Column(precision = 5, scale = 2)
    private BigDecimal awayWinOdds;
    
    @Column(precision = 5, scale = 2)
    private BigDecimal drawOdds;
    
    @Column
    private String result;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Конструкторы
    public Event() {}

    public Event(String homeTeam, String awayTeam, LocalDateTime startTime) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.startTime = startTime;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getHomeTeam() { return homeTeam; }
    public void setHomeTeam(String homeTeam) { this.homeTeam = homeTeam; }

    public String getAwayTeam() { return awayTeam; }
    public void setAwayTeam(String awayTeam) { this.awayTeam = awayTeam; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public EventStatus getStatus() { return status; }
    public void setStatus(EventStatus status) { this.status = status; }

    public BigDecimal getHomeWinOdds() { return homeWinOdds; }
    public void setHomeWinOdds(BigDecimal homeWinOdds) { this.homeWinOdds = homeWinOdds; }

    public BigDecimal getAwayWinOdds() { return awayWinOdds; }
    public void setAwayWinOdds(BigDecimal awayWinOdds) { this.awayWinOdds = awayWinOdds; }

    public BigDecimal getDrawOdds() { return drawOdds; }
    public void setDrawOdds(BigDecimal drawOdds) { this.drawOdds = drawOdds; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
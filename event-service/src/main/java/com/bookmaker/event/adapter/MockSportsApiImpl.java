package com.bookmaker.event.adapter;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

// Мок-реализация внешнего API для демонстрации
@Component
public class MockSportsApiImpl implements ExternalSportsApi {
    
    @Override
    public List<ExternalEvent> getUpcomingEvents() {
        return Arrays.asList(
            new ExternalEvent("ext_1", "Реал Мадрид", "Барселона", LocalDateTime.now().plusDays(1)),
            new ExternalEvent("ext_2", "Манчестер Юнайтед", "Ливерпуль", LocalDateTime.now().plusDays(2)),
            new ExternalEvent("ext_3", "Бавария", "Боруссия", LocalDateTime.now().plusDays(3))
        );
    }
    
    @Override
    public ExternalOdds getEventOdds(String externalEventId) {
        // Генерируем случайные коэффициенты для демонстрации
        switch (externalEventId) {
            case "ext_1":
                return new ExternalOdds(
                    BigDecimal.valueOf(2.10),
                    BigDecimal.valueOf(3.40),
                    BigDecimal.valueOf(3.20)
                );
            case "ext_2":
                return new ExternalOdds(
                    BigDecimal.valueOf(1.85),
                    BigDecimal.valueOf(3.60),
                    BigDecimal.valueOf(4.20)
                );
            case "ext_3":
                return new ExternalOdds(
                    BigDecimal.valueOf(1.95),
                    BigDecimal.valueOf(3.30),
                    BigDecimal.valueOf(3.80)
                );
            default:
                return new ExternalOdds(
                    BigDecimal.valueOf(2.00),
                    BigDecimal.valueOf(3.00),
                    BigDecimal.valueOf(3.50)
                );
        }
    }
    
    @Override
    public ExternalResult getEventResult(String externalEventId) {
        // Мок-результаты
        return new ExternalResult("HOME_WIN", 2, 1);
    }
}
package com.bookmaker.betting.model;

public enum BetStatus {
    PENDING,    // Ожидает результата
    WON,        // Выиграла
    LOST,       // Проиграла
    CANCELLED   // Отменена
}
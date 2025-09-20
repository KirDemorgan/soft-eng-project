package com.bookmaker.event.model;

public enum EventStatus {
    ACTIVE,     // Активно, принимаются ставки
    LIVE,       // Идет матч
    FINISHED,   // Завершено
    CANCELLED   // Отменено
}
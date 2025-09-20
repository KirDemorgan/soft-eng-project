package com.bookmaker.event.observer;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class OddChangeNotifier {
    
    private final List<OddChangeObserver> observers = new ArrayList<>();
    
    public void addObserver(OddChangeObserver observer) {
        observers.add(observer);
    }
    
    public void removeObserver(OddChangeObserver observer) {
        observers.remove(observer);
    }
    
    public void notifyOddChange(Long eventId, String oddType, BigDecimal oldOdd, BigDecimal newOdd) {
        for (OddChangeObserver observer : observers) {
            try {
                observer.onOddChange(eventId, oddType, oldOdd, newOdd);
            } catch (Exception e) {
                // Логируем ошибку, но продолжаем уведомлять других наблюдателей
                System.err.println("Ошибка при уведомлении наблюдателя: " + e.getMessage());
            }
        }
    }
}
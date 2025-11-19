package com.bookmaker.event.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class OddChangeNotifier {
    private static final Logger log = LoggerFactory.getLogger(OddChangeNotifier.class);
    
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
                log.error("Error notifying observer", e);
            }
        }
    }
}
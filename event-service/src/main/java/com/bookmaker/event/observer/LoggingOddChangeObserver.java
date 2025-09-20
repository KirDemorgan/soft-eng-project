package com.bookmaker.event.observer;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class LoggingOddChangeObserver implements OddChangeObserver {
    
    @Override
    public void onOddChange(Long eventId, String oddType, BigDecimal oldOdd, BigDecimal newOdd) {
        System.out.println(String.format(
            "[%s] Изменение коэффициента для события %d: %s %s -> %s",
            LocalDateTime.now(),
            eventId,
            oddType,
            oldOdd,
            newOdd
        ));
    }
}
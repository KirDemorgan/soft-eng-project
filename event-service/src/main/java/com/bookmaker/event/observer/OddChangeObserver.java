package com.bookmaker.event.observer;

import java.math.BigDecimal;

// Observer Pattern - наблюдатель за изменениями коэффициентов
public interface OddChangeObserver {
    void onOddChange(Long eventId, String oddType, BigDecimal oldOdd, BigDecimal newOdd);
}
package com.bookmaker.event.config;

import com.bookmaker.event.observer.LoggingOddChangeObserver;
import com.bookmaker.event.observer.OddChangeNotifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class ObserverConfig {
    
    @Autowired
    private OddChangeNotifier oddChangeNotifier;
    
    @Autowired
    private LoggingOddChangeObserver loggingObserver;
    
    @PostConstruct
    public void setupObservers() {
        // Регистрируем наблюдателей
        oddChangeNotifier.addObserver(loggingObserver);
    }
}
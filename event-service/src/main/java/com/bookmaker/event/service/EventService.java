package com.bookmaker.event.service;

import com.bookmaker.event.adapter.SportsDataAdapter;
import com.bookmaker.event.model.Event;
import com.bookmaker.event.model.EventStatus;
import com.bookmaker.event.observer.OddChangeNotifier;
import com.bookmaker.event.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private OddChangeNotifier oddChangeNotifier;
    
    @Autowired
    private SportsDataAdapter sportsDataAdapter;
    
    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }
    
    public Optional<Event> findById(Long id) {
        return eventRepository.findById(id);
    }
    
    public List<Event> getActiveEvents() {
        return eventRepository.findByStatus(EventStatus.ACTIVE);
    }
    
    public List<Event> getUpcomingEvents(int hours) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.plusHours(hours);
        return eventRepository.findUpcomingEvents(now, endTime, EventStatus.ACTIVE);
    }
    
    public Event updateEvent(Event event) {
        return eventRepository.save(event);
    }
    
    public void updateEventOdds(Long eventId, BigDecimal homeWinOdds, 
                               BigDecimal awayWinOdds, BigDecimal drawOdds) {
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        
        if (eventOpt.isPresent()) {
            Event event = eventOpt.get();
            
            // Сохраняем старые коэффициенты для уведомлений
            BigDecimal oldHomeOdds = event.getHomeWinOdds();
            BigDecimal oldAwayOdds = event.getAwayWinOdds();
            BigDecimal oldDrawOdds = event.getDrawOdds();
            
            // Обновляем коэффициенты
            event.setHomeWinOdds(homeWinOdds);
            event.setAwayWinOdds(awayWinOdds);
            event.setDrawOdds(drawOdds);
            
            eventRepository.save(event);
            
            // Уведомляем наблюдателей об изменениях
            if (oldHomeOdds != null && !oldHomeOdds.equals(homeWinOdds)) {
                oddChangeNotifier.notifyOddChange(eventId, "HOME_WIN", oldHomeOdds, homeWinOdds);
            }
            if (oldAwayOdds != null && !oldAwayOdds.equals(awayWinOdds)) {
                oddChangeNotifier.notifyOddChange(eventId, "AWAY_WIN", oldAwayOdds, awayWinOdds);
            }
            if (oldDrawOdds != null && !oldDrawOdds.equals(drawOdds)) {
                oddChangeNotifier.notifyOddChange(eventId, "DRAW", oldDrawOdds, drawOdds);
            }
        }
    }
    
    public void finishEvent(Long eventId, String result) {
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        
        if (eventOpt.isPresent()) {
            Event event = eventOpt.get();
            event.setStatus(EventStatus.FINISHED);
            event.setResult(result);
            eventRepository.save(event);
        }
    }
    
    public void importEventsFromExternal() {
        List<Event> externalEvents = sportsDataAdapter.getUpcomingEventsFromExternal();
        
        for (Event event : externalEvents) {
            // Проверяем, не существует ли уже такое событие
            // В реальном приложении здесь была бы более сложная логика дедупликации
            eventRepository.save(event);
        }
    }
}
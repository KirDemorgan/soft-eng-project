package com.bookmaker.event.service;

import com.bookmaker.event.adapter.SportsDataAdapter;
import com.bookmaker.event.model.Event;
import com.bookmaker.event.model.EventStatus;
import com.bookmaker.event.observer.OddChangeNotifier;
import com.bookmaker.event.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService {
    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private OddChangeNotifier oddChangeNotifier;
    
    @Autowired
    private SportsDataAdapter sportsDataAdapter;
    
    public Event createEvent(Event event) {
        log.info("Creating event: {} vs {}", event.getHomeTeam(), event.getAwayTeam());
        return eventRepository.save(event);
    }
    
    public java.util.Optional<Event> findById(Long id) {
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
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        BigDecimal oldHomeOdds = event.getHomeWinOdds();
        BigDecimal oldAwayOdds = event.getAwayWinOdds();
        BigDecimal oldDrawOdds = event.getDrawOdds();

        event.setHomeWinOdds(homeWinOdds);
        event.setAwayWinOdds(awayWinOdds);
        event.setDrawOdds(drawOdds);

        eventRepository.save(event);

        if (oldHomeOdds != null && oldHomeOdds.compareTo(homeWinOdds) != 0) {
            oddChangeNotifier.notifyOddChange(eventId, "HOME_WIN", oldHomeOdds, homeWinOdds);
        }
        if (oldAwayOdds != null && oldAwayOdds.compareTo(awayWinOdds) != 0) {
            oddChangeNotifier.notifyOddChange(eventId, "AWAY_WIN", oldAwayOdds, awayWinOdds);
        }
        if (oldDrawOdds != null && oldDrawOdds.compareTo(drawOdds) != 0) {
            oddChangeNotifier.notifyOddChange(eventId, "DRAW", oldDrawOdds, drawOdds);
        }
    }
    
    public void finishEvent(Long eventId, String result) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
        event.setStatus(EventStatus.FINISHED);
        event.setResult(result);
        eventRepository.save(event);
        log.info("Finished event {}: {}", eventId, result);
    }
    
    public void importEventsFromExternal() {
        log.info("Importing events from external source");
        List<Event> externalEvents = sportsDataAdapter.getUpcomingEventsFromExternal();
        
        for (Event event : externalEvents) {
            eventRepository.save(event);
        }
        log.info("Imported {} events", externalEvents.size());
    }
}
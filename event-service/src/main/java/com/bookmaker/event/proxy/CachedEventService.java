package com.bookmaker.event.proxy;

import com.bookmaker.event.model.Event;
import com.bookmaker.event.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

// Proxy Pattern - кеширование запросов к сервису событий
@Component
public class CachedEventService {
    
    @Autowired
    private EventService eventService;
    
    @Cacheable(value = "events", key = "#eventId")
    public Optional<Event> findById(Long eventId) {
        return eventService.findById(eventId);
    }
    
    @Cacheable(value = "activeEvents")
    public List<Event> getActiveEvents() {
        return eventService.getActiveEvents();
    }
    
    @Cacheable(value = "upcomingEvents", key = "#hours")
    public List<Event> getUpcomingEvents(int hours) {
        return eventService.getUpcomingEvents(hours);
    }
    
    // Методы, которые изменяют данные, не кешируются
    public Event createEvent(Event event) {
        return eventService.createEvent(event);
    }
    
    public Event updateEvent(Event event) {
        return eventService.updateEvent(event);
    }
}
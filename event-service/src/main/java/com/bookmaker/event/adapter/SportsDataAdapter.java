package com.bookmaker.event.adapter;

import com.bookmaker.event.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

// Adapter Pattern - адаптация внешних API спортивных данных
@Component
public class SportsDataAdapter {
    
    @Autowired
    private ExternalSportsApi externalSportsApi;
    
    public List<Event> getUpcomingEventsFromExternal() {
        List<ExternalEvent> externalEvents = externalSportsApi.getUpcomingEvents();
        
        return externalEvents.stream()
                .map(this::convertToInternalEvent)
                .collect(Collectors.toList());
    }
    
    public void updateEventOdds(Event event, String externalEventId) {
        ExternalOdds externalOdds = externalSportsApi.getEventOdds(externalEventId);
        
        event.setHomeWinOdds(externalOdds.getHomeWin());
        event.setAwayWinOdds(externalOdds.getAwayWin());
        event.setDrawOdds(externalOdds.getDraw());
    }
    
    public String getEventResult(String externalEventId) {
        ExternalResult externalResult = externalSportsApi.getEventResult(externalEventId);
        return convertToInternalResult(externalResult);
    }
    
    private Event convertToInternalEvent(ExternalEvent externalEvent) {
        Event event = new Event();
        event.setHomeTeam(externalEvent.getHomeTeam());
        event.setAwayTeam(externalEvent.getAwayTeam());
        event.setStartTime(externalEvent.getStartTime());
        
        // Получаем коэффициенты для события
        ExternalOdds odds = externalSportsApi.getEventOdds(externalEvent.getId());
        event.setHomeWinOdds(odds.getHomeWin());
        event.setAwayWinOdds(odds.getAwayWin());
        event.setDrawOdds(odds.getDraw());
        
        return event;
    }
    
    private String convertToInternalResult(ExternalResult externalResult) {
        // Конвертируем внешний формат результата во внутренний
        switch (externalResult.getResult()) {
            case "HOME_WIN":
                return "HOME_WIN";
            case "AWAY_WIN":
                return "AWAY_WIN";
            case "DRAW":
                return "DRAW";
            default:
                return "UNKNOWN";
        }
    }
}
package com.bookmaker.event.adapter;

import com.bookmaker.event.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class SportsDataAdapter {
    private static final Logger log = LoggerFactory.getLogger(SportsDataAdapter.class);

    @Autowired
    private ExternalSportsApi externalSportsApi;
    
    public List<Event> getUpcomingEventsFromExternal() {
        log.info("Fetching upcoming events from external API");
        List<ExternalEvent> externalEvents = externalSportsApi.getUpcomingEvents();
        
        return externalEvents.stream()
                .map(this::convertToInternalEvent)
                .collect(Collectors.toList());
    }
    
    public void updateEventOdds(Event event, String externalEventId) {
        log.info("Updating odds for event {} from external API", event.getId());
        ExternalOdds externalOdds = externalSportsApi.getEventOdds(externalEventId);
        
        event.setHomeWinOdds(externalOdds.getHomeWin());
        event.setAwayWinOdds(externalOdds.getAwayWin());
        event.setDrawOdds(externalOdds.getDraw());
    }
    
    public String getEventResult(String externalEventId) {
        log.info("Fetching result for external event {}", externalEventId);
        ExternalResult externalResult = externalSportsApi.getEventResult(externalEventId);
        return convertToInternalResult(externalResult);
    }
    
    private Event convertToInternalEvent(ExternalEvent externalEvent) {
        Event event = new Event();
        event.setHomeTeam(externalEvent.getHomeTeam());
        event.setAwayTeam(externalEvent.getAwayTeam());
        event.setStartTime(externalEvent.getStartTime());
        
        ExternalOdds odds = externalSportsApi.getEventOdds(externalEvent.getId());
        event.setHomeWinOdds(odds.getHomeWin());
        event.setAwayWinOdds(odds.getAwayWin());
        event.setDrawOdds(odds.getDraw());
        
        return event;
    }
    
    private String convertToInternalResult(ExternalResult externalResult) {
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
package com.bookmaker.event.controller;

import com.bookmaker.event.dto.CreateEventRequest;
import com.bookmaker.event.dto.FinishEventRequest;
import com.bookmaker.event.dto.UpdateOddsRequest;
import com.bookmaker.event.model.Event;
import com.bookmaker.event.proxy.CachedEventService;
import com.bookmaker.event.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
@Tag(name = "Event Management", description = "API for managing sports events")
public class EventController {
    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    @Autowired
    private EventService eventService;
    
    @Autowired
    private CachedEventService cachedEventService;
    
    @Operation(summary = "Create a sports event")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Event created successfully",
                    content = @Content(schema = @Schema(implementation = Event.class))),
        @ApiResponse(responseCode = "400", description = "Invalid event data")
    })
    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody CreateEventRequest request) {
        Event event = new Event();
        event.setHomeTeam(request.getHomeTeam());
        event.setAwayTeam(request.getAwayTeam());
        event.setStartTime(request.getStartTime());
        event.setHomeWinOdds(request.getHomeWinOdds());
        event.setAwayWinOdds(request.getAwayWinOdds());
        event.setDrawOdds(request.getDrawOdds());
        
        Event savedEvent = eventService.createEvent(event);
        return ResponseEntity.ok(savedEvent);
    }
    
    @Operation(summary = "Get event by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Event found",
                    content = @Content(schema = @Schema(implementation = Event.class))),
        @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEvent(@Parameter(description = "Event ID") @PathVariable Long id) {
        return cachedEventService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Get active events")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of active events",
                    content = @Content(schema = @Schema(implementation = Event.class)))
    })
    @GetMapping("/active")
    public ResponseEntity<List<Event>> getActiveEvents() {
        List<Event> events = cachedEventService.getActiveEvents();
        return ResponseEntity.ok(events);
    }
    
    @Operation(summary = "Get upcoming events")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of upcoming events",
                    content = @Content(schema = @Schema(implementation = Event.class)))
    })
    @GetMapping("/upcoming")
    public ResponseEntity<List<Event>> getUpcomingEvents(@Parameter(description = "Number of hours") @RequestParam(defaultValue = "24") int hours) {
        List<Event> events = cachedEventService.getUpcomingEvents(hours);
        return ResponseEntity.ok(events);
    }
    
    @Operation(summary = "Update event odds")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Odds updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid data or event not found")
    })
    @PutMapping("/{id}/odds")
    public ResponseEntity<?> updateOdds(@Parameter(description = "Event ID") @PathVariable Long id,
                                       @RequestBody UpdateOddsRequest odds) {
        try {
            eventService.updateEventOdds(
                id,
                odds.getHomeWinOdds(),
                odds.getAwayWinOdds(),
                odds.getDrawOdds()
            );
            return ResponseEntity.ok(Map.of("message", "Odds updated"));
        } catch (Exception e) {
            log.error("Error updating odds for event {}", id, e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @Operation(summary = "Finish an event")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Event finished successfully"),
        @ApiResponse(responseCode = "400", description = "Error or event not found")
    })
    @PutMapping("/{id}/finish")
    public ResponseEntity<?> finishEvent(@Parameter(description = "Event ID") @PathVariable Long id,
                                        @RequestBody FinishEventRequest request) {
        try {
            eventService.finishEvent(id, request.getResult());
            return ResponseEntity.ok(Map.of("message", "Event finished"));
        } catch (Exception e) {
            log.error("Error finishing event {}", id, e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @Operation(summary = "Import events from an external API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Events imported successfully"),
        @ApiResponse(responseCode = "400", description = "Error importing events")
    })
    @PostMapping("/import")
    public ResponseEntity<?> importEvents() {
        try {
            eventService.importEventsFromExternal();
            return ResponseEntity.ok(Map.of("message", "Events imported"));
        } catch (Exception e) {
            log.error("Error importing events", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
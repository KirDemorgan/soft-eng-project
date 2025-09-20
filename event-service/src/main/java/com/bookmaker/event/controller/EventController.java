package com.bookmaker.event.controller;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
@Tag(name = "Event Management", description = "API для управления спортивными событиями. Использует паттерны Observer, Adapter, Proxy")
public class EventController {
    
    @Autowired
    private EventService eventService;
    
    @Autowired
    private CachedEventService cachedEventService;
    
    @Operation(summary = "Создание спортивного события", 
               description = "Создает новое спортивное событие с коэффициентами")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Событие успешно создано", 
                    content = @Content(schema = @Schema(implementation = Event.class))),
        @ApiResponse(responseCode = "400", description = "Ошибка валидации данных")
    })
    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody Map<String, Object> request) {
        Event event = new Event();
        event.setHomeTeam(request.get("homeTeam").toString());
        event.setAwayTeam(request.get("awayTeam").toString());
        event.setStartTime(LocalDateTime.parse(request.get("startTime").toString()));
        
        if (request.containsKey("homeWinOdds")) {
            event.setHomeWinOdds(new BigDecimal(request.get("homeWinOdds").toString()));
        }
        if (request.containsKey("awayWinOdds")) {
            event.setAwayWinOdds(new BigDecimal(request.get("awayWinOdds").toString()));
        }
        if (request.containsKey("drawOdds")) {
            event.setDrawOdds(new BigDecimal(request.get("drawOdds").toString()));
        }
        
        Event savedEvent = eventService.createEvent(event);
        return ResponseEntity.ok(savedEvent);
    }
    
    @Operation(summary = "Получение события по ID", 
               description = "Возвращает спортивное событие по ID (используется Proxy паттерн для кеширования)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Событие найдено", 
                    content = @Content(schema = @Schema(implementation = Event.class))),
        @ApiResponse(responseCode = "404", description = "Событие не найдено")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEvent(@Parameter(description = "ID события") @PathVariable Long id) {
        return cachedEventService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Получение активных событий", 
               description = "Возвращает все активные спортивные события (используется кеширование)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список активных событий", 
                    content = @Content(schema = @Schema(implementation = Event.class)))
    })
    @GetMapping("/active")
    public ResponseEntity<List<Event>> getActiveEvents() {
        List<Event> events = cachedEventService.getActiveEvents();
        return ResponseEntity.ok(events);
    }
    
    @Operation(summary = "Получение предстоящих событий", 
               description = "Возвращает события, которые начнутся в ближайшие N часов")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список предстоящих событий", 
                    content = @Content(schema = @Schema(implementation = Event.class)))
    })
    @GetMapping("/upcoming")
    public ResponseEntity<List<Event>> getUpcomingEvents(@Parameter(description = "Количество часов") @RequestParam(defaultValue = "24") int hours) {
        List<Event> events = cachedEventService.getUpcomingEvents(hours);
        return ResponseEntity.ok(events);
    }
    
    @Operation(summary = "Обновление коэффициентов события", 
               description = "Обновляет коэффициенты события и уведомляет наблюдателей (Observer паттерн)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Коэффициенты успешно обновлены"),
        @ApiResponse(responseCode = "400", description = "Ошибка валидации или событие не найдено")
    })
    @PutMapping("/{id}/odds")
    public ResponseEntity<?> updateOdds(@Parameter(description = "ID события") @PathVariable Long id, 
                                       @RequestBody Map<String, BigDecimal> odds) {
        try {
            eventService.updateEventOdds(
                id,
                odds.get("homeWinOdds"),
                odds.get("awayWinOdds"),
                odds.get("drawOdds")
            );
            return ResponseEntity.ok(Map.of("message", "Коэффициенты обновлены"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @Operation(summary = "Завершение события", 
               description = "Устанавливает результат события и меняет статус на FINISHED")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Событие успешно завершено"),
        @ApiResponse(responseCode = "400", description = "Ошибка или событие не найдено")
    })
    @PutMapping("/{id}/finish")
    public ResponseEntity<?> finishEvent(@Parameter(description = "ID события") @PathVariable Long id, 
                                        @RequestBody Map<String, String> request) {
        try {
            String result = request.get("result");
            eventService.finishEvent(id, result);
            return ResponseEntity.ok(Map.of("message", "Событие завершено"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @Operation(summary = "Импорт событий из внешнего API", 
               description = "Импортирует спортивные события из внешнего источника используя Adapter паттерн")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "События успешно импортированы"),
        @ApiResponse(responseCode = "400", description = "Ошибка при импорте событий")
    })
    @PostMapping("/import")
    public ResponseEntity<?> importEvents() {
        try {
            eventService.importEventsFromExternal();
            return ResponseEntity.ok(Map.of("message", "События импортированы"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
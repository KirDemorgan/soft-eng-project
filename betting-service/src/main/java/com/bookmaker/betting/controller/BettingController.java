package com.bookmaker.betting.controller;

import com.bookmaker.betting.model.Bet;
import com.bookmaker.betting.model.BetType;
import com.bookmaker.betting.service.BettingService;
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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bets")
@Tag(name = "Betting Management", description = "API для управления ставками. Использует паттерны Factory Method, Strategy, Command")
public class BettingController {
    
    @Autowired
    private BettingService bettingService;
    
    @Operation(summary = "Размещение ставки", 
               description = "Создает новую ставку используя Factory Method паттерн и выполняет через Command паттерн")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ставка успешно размещена"),
        @ApiResponse(responseCode = "400", description = "Ошибка валидации или недостаточно средств")
    })
    @PostMapping
    public ResponseEntity<?> placeBet(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Long eventId = Long.valueOf(request.get("eventId").toString());
            BetType type = BetType.valueOf(request.get("type").toString());
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            BigDecimal odds = new BigDecimal(request.get("odds").toString());
            
            Bet bet = bettingService.placeBet(userId, eventId, type, amount, odds);
            
            return ResponseEntity.ok(Map.of(
                "message", "Ставка успешно размещена",
                "betId", bet.getId(),
                "amount", bet.getAmount(),
                "odds", bet.getOdds()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @Operation(summary = "Обработка ставки", 
               description = "Рассчитывает результат ставки используя Strategy паттерн для определения выплаты")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ставка успешно обработана"),
        @ApiResponse(responseCode = "400", description = "Ставка не найдена или уже обработана")
    })
    @PutMapping("/{betId}/settle")
    public ResponseEntity<?> settleBet(@Parameter(description = "ID ставки") @PathVariable Long betId, 
                                     @RequestBody Map<String, String> request) {
        try {
            String eventResult = request.get("result");
            bettingService.settleBet(betId, eventResult);
            
            return ResponseEntity.ok(Map.of("message", "Ставка обработана"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @Operation(summary = "Получение ставок пользователя", 
               description = "Возвращает все ставки конкретного пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список ставок пользователя", 
                    content = @Content(schema = @Schema(implementation = Bet.class)))
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Bet>> getUserBets(@Parameter(description = "ID пользователя") @PathVariable Long userId) {
        List<Bet> bets = bettingService.getUserBets(userId);
        return ResponseEntity.ok(bets);
    }
    
    @Operation(summary = "Получение ставок по событию", 
               description = "Возвращает все ставки на конкретное спортивное событие")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список ставок на событие", 
                    content = @Content(schema = @Schema(implementation = Bet.class)))
    })
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<Bet>> getEventBets(@Parameter(description = "ID события") @PathVariable Long eventId) {
        List<Bet> bets = bettingService.getEventBets(eventId);
        return ResponseEntity.ok(bets);
    }
    
    @Operation(summary = "Получение ожидающих ставок", 
               description = "Возвращает все ставки со статусом PENDING (ожидают результата)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список ожидающих ставок", 
                    content = @Content(schema = @Schema(implementation = Bet.class)))
    })
    @GetMapping("/pending")
    public ResponseEntity<List<Bet>> getPendingBets() {
        List<Bet> bets = bettingService.getPendingBets();
        return ResponseEntity.ok(bets);
    }
}
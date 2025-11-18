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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bets")
@Tag(name = "Betting Management", description = "API for managing bets")
public class BettingController {
    private static final Logger log = LoggerFactory.getLogger(BettingController.class);

    @Autowired
    private BettingService bettingService;
    
    @Operation(summary = "Place a bet")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Bet placed successfully"),
        @ApiResponse(responseCode = "400", description = "Validation error or insufficient funds")
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
                "message", "Bet placed successfully",
                "betId", bet.getId(),
                "amount", bet.getAmount(),
                "odds", bet.getOdds()
            ));
        } catch (Exception e) {
            log.error("Error placing bet", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @Operation(summary = "Settle a bet")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Bet settled successfully"),
        @ApiResponse(responseCode = "400", description = "Bet not found or already settled")
    })
    @PutMapping("/{betId}/settle")
    public ResponseEntity<?> settleBet(@Parameter(description = "Bet ID") @PathVariable Long betId,
                                     @RequestBody Map<String, String> request) {
        try {
            String eventResult = request.get("result");
            bettingService.settleBet(betId, eventResult);
            
            return ResponseEntity.ok(Map.of("message", "Bet settled"));
        } catch (Exception e) {
            log.error("Error settling bet", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @Operation(summary = "Get user bets")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of user bets",
                    content = @Content(schema = @Schema(implementation = Bet.class)))
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Bet>> getUserBets(@Parameter(description = "User ID") @PathVariable Long userId) {
        List<Bet> bets = bettingService.getUserBets(userId);
        return ResponseEntity.ok(bets);
    }
    
    @Operation(summary = "Get event bets")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of event bets",
                    content = @Content(schema = @Schema(implementation = Bet.class)))
    })
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<Bet>> getEventBets(@Parameter(description = "Event ID") @PathVariable Long eventId) {
        List<Bet> bets = bettingService.getEventBets(eventId);
        return ResponseEntity.ok(bets);
    }
    
    @Operation(summary = "Get pending bets")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of pending bets",
                    content = @Content(schema = @Schema(implementation = Bet.class)))
    })
    @GetMapping("/pending")
    public ResponseEntity<List<Bet>> getPendingBets() {
        List<Bet> bets = bettingService.getPendingBets();
        return ResponseEntity.ok(bets);
    }
}
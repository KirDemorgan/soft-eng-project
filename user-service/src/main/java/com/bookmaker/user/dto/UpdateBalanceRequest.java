package com.bookmaker.user.dto;

import java.math.BigDecimal;

public class UpdateBalanceRequest {
    private BigDecimal amount;

    // Getters and setters
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}

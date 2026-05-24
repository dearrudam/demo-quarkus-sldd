package org.soujava.demo;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

public record MultiplyRequest(
    @NotNull BigDecimal multiplier,
    @NotNull BigDecimal multiplicand
) {
}

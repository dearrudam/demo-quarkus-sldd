package org.soujava.demo;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

public record DivideRequest(
    @NotNull BigDecimal dividend,
    @NotNull BigDecimal divisor,
    RoundingContext roundingContext
) {
}

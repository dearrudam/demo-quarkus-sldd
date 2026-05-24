package org.soujava.demo;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

public record SubtractRequest(
    @NotNull BigDecimal firstOperand,
    @NotNull BigDecimal secondOperand
) {
}

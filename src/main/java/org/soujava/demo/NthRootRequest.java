package org.soujava.demo;

import java.math.BigDecimal;
import java.math.BigInteger;

import jakarta.validation.constraints.NotNull;

public record NthRootRequest(
        @NotNull BigDecimal radicand,
        @NotNull BigInteger index,
        RoundingContext roundingContext) {
}

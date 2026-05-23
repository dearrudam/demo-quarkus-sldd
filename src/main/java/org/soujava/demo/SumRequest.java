package org.soujava.demo;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

public class SumRequest {

    @NotNull
    public BigDecimal firstOperand;

    @NotNull
    public BigDecimal secondOperand;
}

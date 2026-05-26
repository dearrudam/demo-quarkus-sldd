package org.soujava.demo;

import java.math.BigDecimal;
import java.math.RoundingMode;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CalcService {

    public BigDecimal sum(BigDecimal firstOperand, BigDecimal secondOperand) {
        return firstOperand.add(secondOperand);
    }

    public BigDecimal subtract(BigDecimal firstOperand, BigDecimal secondOperand) {
        return firstOperand.subtract(secondOperand);
    }

    public BigDecimal multiply(BigDecimal multiplier, BigDecimal multiplicand) {
        return multiplier.multiply(multiplicand);
    }

    public BigDecimal divide(BigDecimal dividend, BigDecimal divisor, int scale, RoundingMode roundingMode) {
        return dividend.divide(divisor, scale, roundingMode);
    }
}

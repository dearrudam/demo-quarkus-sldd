package org.soujava.demo;

import java.math.BigDecimal;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CalcService {

    public BigDecimal sum(BigDecimal firstOperand, BigDecimal secondOperand) {
        return firstOperand.add(secondOperand);
    }
}

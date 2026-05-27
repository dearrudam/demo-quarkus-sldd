package org.soujava.demo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
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

    public BigDecimal power(BigDecimal base, BigDecimal exponent) {
        return base.pow(exponent.intValue());
    }


    public BigDecimal nthRoot(BigDecimal radicand, BigInteger index, int scale, RoundingMode roundingMode) {
        var indexInt = index.intValueExact();
        if (BigDecimal.ZERO.compareTo(radicand) == 0) {
            return BigDecimal.ZERO.setScale(scale, roundingMode);
        }

        var negative = radicand.signum() < 0;
        var absoluteRadicand = radicand.abs();
        var mathContext = new MathContext(scale + 10, roundingMode);
        var tolerance = BigDecimal.ONE.scaleByPowerOfTen(-(scale + 2));
        var n = BigDecimal.valueOf(indexInt);
        var x = absoluteRadicand.divide(n, mathContext);
        if (x.compareTo(BigDecimal.ZERO) == 0) {
            x = BigDecimal.ONE;
        }

        for (int i = 0; i < 100; i++) {
            var xPow = x.pow(indexInt - 1, mathContext);
            var numerator = x.multiply(BigDecimal.valueOf(indexInt - 1), mathContext)
                    .add(absoluteRadicand.divide(xPow, mathContext), mathContext);
            var next = numerator.divide(n, mathContext);
            var delta = next.subtract(x, mathContext).abs();
            x = next;
            if (delta.compareTo(tolerance) <= 0) {
                break;
            }
        }

        var result = x.setScale(scale, roundingMode);
        return negative ? result.negate() : result;
    }
}

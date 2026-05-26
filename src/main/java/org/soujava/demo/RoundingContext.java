package org.soujava.demo;

import java.math.RoundingMode;

public record RoundingContext(Integer scale, RoundingMode roundingMode) {
}

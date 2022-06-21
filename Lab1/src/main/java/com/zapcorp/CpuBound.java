package com.zapcorp;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CpuBound implements Runnable {

    private static final BigDecimal ZERO = BigDecimal.valueOf(0);
    private static final BigDecimal ONE = BigDecimal.valueOf(1);
    private static final BigDecimal FOUR = BigDecimal.valueOf(4);
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;

    private final int digits;


    public CpuBound(int digits) {
        this.digits = digits;
    }

    @Override
    public void run() {
        final var res = count_Pi_digits(digits);
        final var abs = res.abs();
    }

    private static BigDecimal count_Pi_digits(int digits) {
        final var scale = digits + 5;
        final var arcTan5 = arcTan_function(5, scale);
        final var arcTan239 = arcTan_function(239, scale);
        final var pi = arcTan5.multiply(FOUR).subtract(arcTan239).multiply(FOUR);
        return pi.setScale(digits, RoundingMode.HALF_UP);
    }


    private static BigDecimal arcTan_function(int inverseX, int scale) {
        final var invX = BigDecimal.valueOf(inverseX);
        final var invX2 = BigDecimal.valueOf((long) inverseX * inverseX);

        var number = ONE.divide(invX, scale, ROUNDING_MODE);
        var result = number;

        BigDecimal term;
        var i = 1;
        do {
            number = number.divide(invX2, scale, ROUNDING_MODE);
            final var denominator = 2 * i + 1;
            term = number.divide(BigDecimal.valueOf(denominator), scale, ROUNDING_MODE);
            if ((i % 2) != 0) {
                result = result.subtract(term);
            } else {
                result = result.add(term);
            }
            i++;
        } while (term.compareTo(ZERO) != 0);

        return result;
    }
}

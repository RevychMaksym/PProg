package com.zapcorp.transformator.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MathUtil {

    public long factorial(int num) {
        var fact = 1L;
        for (var i = 1; i <= num; i++) {
            fact = fact * i;
        }
        return fact;
    }

    public double cosh(double a) {
        return (Math.exp(a) + Math.exp(-a)) / 2;
    }

    public double sinh(double a) {
        return (Math.exp(a) - Math.exp(-a)) / 2;
    }
}

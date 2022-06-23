package com.zapcorp.transformator.util;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@UtilityClass
public class RandUtil {

    private final Random RANDOM = new Random();


    public List<Double> generateList(int size, double origin, double bound) {
        return RANDOM.doubles(size, origin, bound)
                .boxed()
                .collect(Collectors.toList());
    }
}

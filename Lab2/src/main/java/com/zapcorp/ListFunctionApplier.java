package com.zapcorp;

import lombok.Value;

import java.util.List;
import java.util.function.Function;


@Value
public class ListFunctionApplier implements Runnable {

    List<Double> list;
    Function<Double, Double> function;
    int threads;
    int thread;


    @Override
    public void run() {
        for (int i = thread; i < list.size(); i += threads) {
            list.set(i, function.apply(list.get(i)));
        }
    }
}

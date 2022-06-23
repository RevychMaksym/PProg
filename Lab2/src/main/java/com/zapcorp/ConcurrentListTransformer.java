package com.zapcorp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

@Slf4j
@RequiredArgsConstructor
public class ConcurrentListTransformer {

    private static final int TWO = 2;
    private static final int WARMUP_RUNS = 10;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private final int powerOfTwoThreads;
    private final List<Double> list;
    private final Comparator<Double> comparator;
    private final Function<Double, Double> transformationFunction;
    private final int measurementIterations;
    private final String resultFilePath;


    @SneakyThrows
    public void runTransformations() {
        final var result = IntStream.range(0, powerOfTwoThreads)
                .map(power -> (int) Math.pow(TWO, power))
                .mapToObj(this::executeOp)
                .collect(Collectors.toList());

        objectMapper.writeValue(new File(Objects.requireNonNull(
                Thread.currentThread().getContextClassLoader().getResource(resultFilePath)).getPath()), result);
    }

    @SneakyThrows
    private ExecutionResult executeOp(int threads) {
        for (int i = 0; i < WARMUP_RUNS; i++) {
            final var thread =
                    new Thread(new ListFunctionApplier(new ArrayList<>(list), transformationFunction, 1, 0));
            thread.start();
            thread.join();
        }

        final var avgExecTimeNanos = LongStream.range(0L, measurementIterations)
                .mapToDouble(i -> measureExecTime(threads))
                .average();

        return new ExecutionResult(threads, avgExecTimeNanos.orElse(0.0) / 1E6);
    }

    @SneakyThrows
    private double measureExecTime(int threads) {
        final var transformationList = new ArrayList<>(list);
        final var threadList = new ArrayList<Thread>();
        for (int i = 0; i < threads; i++) {
            threadList.add(
                    new Thread(new ListFunctionApplier(transformationList, transformationFunction, threads, i)));
        }
        final var start = System.nanoTime();

        threadList.forEach(Thread::start);
        for (Thread thread : threadList) {
            thread.join();
        }
        final var min = transformationList.stream()
                .min(comparator)
                .orElse(Double.POSITIVE_INFINITY);
        final var minIndex = transformationList.indexOf(min);
        log.info("Min: {}; Index: {}", min, minIndex);

        final var finish = System.nanoTime();

        return finish - start;
    }


    @Value
    private static class ExecutionResult {

        int threads;
        double execTimeMillis;
    }
}

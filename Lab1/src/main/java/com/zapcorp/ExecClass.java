package com.zapcorp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;


@RequiredArgsConstructor
public class ExecClass {

    private static final int TWO = 2;
    private static final int iterations = 50;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private final int powerOfTwoThreads;
    private final Runnable task;
    private final int measurementIterations;
    private final String resultFilePath;


    @SneakyThrows
    public void run_task() {
        final var result = IntStream.range(0, powerOfTwoThreads)
                .map(power -> (int) Math.pow(TWO, power))
                .mapToObj(this::execute_task)
                .collect(Collectors.toList());

        objectMapper.writeValue(new File(Objects.requireNonNull(
                Thread.currentThread().getContextClassLoader().getResource(resultFilePath)).getPath()), result);
    }

    @SneakyThrows
    private ExecutionResult execute_task(int threads) {
        for (int counter = 0; counter < iterations; counter++) {
            final var thread = new Thread(task);
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
        final var threadList = new ArrayList<Thread>();
        for (int counter = 0; counter < threads; counter++) {
            threadList.add(new Thread(task));
        }

        final var start = System.nanoTime();
        threadList.forEach(Thread::start);
        for (Thread thread : threadList) {
            thread.join();
        }
        final var finish = System.nanoTime();
        return (finish - start) / (float) threads;
    }


    @Value
    private static class ExecutionResult {

        int threads;
        double execTimeMillis;
    }
}

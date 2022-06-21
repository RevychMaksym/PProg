package com.zapcorp;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;


@Slf4j
public class Main {

    private static final int THREADS_NUM_POWER = 8;
    private static final int MEASUREMENT_COUNT = 8;

    private static final int CPU_BOUND_OP_PI_DIGITS = 4096;

    private static final String io_bound_input_file = "io-bound/data_net";
    private static final String io_bound_output_file = "./io-bound/data_net-out";

    private static final String cpu_result_file = "./res/cpu-bound.json";
    private static final String io_bound_result_file = "./res/io-bound.json";
    private static final String memory_bound_result_file = "./res/memory-bound.json";

    private static final int MEMORY_BOUND_OP_ALLOC_BYTES = 32_000_000; // 32 Mb

    public static void main(String... args) throws IOException {
        empty_results();

        try {
            log.info("CPU-bound calculation: ");
            final var start = System.currentTimeMillis();
            new ExecClass(
                    THREADS_NUM_POWER,
                    new CpuBound(CPU_BOUND_OP_PI_DIGITS),
                    MEASUREMENT_COUNT,
                    cpu_result_file
            ).run_task();
            log.info("Done ({} seconds).",
                    (System.currentTimeMillis() - start) / 1000);
        } catch (Throwable e) {
            log.error("Error during cpu-bound execution", e);
        }

        try {
            log.info("Memory-bound calculation: ");
            final var start = System.currentTimeMillis();
            new ExecClass(
                    THREADS_NUM_POWER,
                    new MemoryBound(MEMORY_BOUND_OP_ALLOC_BYTES),
                    MEASUREMENT_COUNT,
                    memory_bound_result_file
            ).run_task();
            log.info("Done ({} seconds)",
                    (System.currentTimeMillis() - start) / 1000);
        } catch (Throwable e) {
            log.error("Error during memory-bound execution", e);
        }

        try {
            log.info("IO-bound calculation: ");
            final var start = System.currentTimeMillis();
            new ExecClass(
                    THREADS_NUM_POWER,
                    new IoBound(io_bound_input_file, io_bound_output_file),
                    MEASUREMENT_COUNT,
                    io_bound_result_file
            ).run_task();
            log.info("Done ({} seconds)",
                    (System.currentTimeMillis() - start) / 1000);
        } catch (Throwable e) {
            log.error("Error during io-bound execution", e);
        }
    }

    private static void empty_results() throws IOException {
        final var loader = Thread.currentThread().getContextClassLoader();
        try {
            Files.newBufferedWriter(
                    Paths.get(Paths.get(loader.getResource(cpu_result_file).toURI()).toString()),
                    StandardOpenOption.TRUNCATE_EXISTING).close();
            Files.newBufferedWriter(
                    Paths.get(Paths.get(loader.getResource(io_bound_result_file).toURI()).toString()),
                    StandardOpenOption.TRUNCATE_EXISTING).close();
            Files.newBufferedWriter(
                    Paths.get(Paths.get(loader.getResource(memory_bound_result_file).toURI()).toString()),
                    StandardOpenOption.TRUNCATE_EXISTING).close();
        } catch (URISyntaxException e) {
            log.error("URI syntax error");
        }
    }

}

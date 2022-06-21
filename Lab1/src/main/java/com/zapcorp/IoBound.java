package com.zapcorp;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Random;


@RequiredArgsConstructor
public class IoBound implements Runnable {

    private final Random random = new Random(1000L);
    private final String input_file;
    private final String output_file;

    @Override
    @SneakyThrows
    public void run() {
        final var loader = Thread.currentThread().getContextClassLoader();
        final var outputFilePathRand = output_file + random.nextInt();

        Files.copy(Paths.get(Paths.get(loader.getResource(input_file).toURI()).toString()),
                Paths.get(Paths.get(loader.getResource(".").toURI())  + outputFilePathRand),
                StandardCopyOption.REPLACE_EXISTING);
        Files.deleteIfExists(Paths.get(Paths.get(loader.getResource(outputFilePathRand).toURI()).toString()));
    }
}

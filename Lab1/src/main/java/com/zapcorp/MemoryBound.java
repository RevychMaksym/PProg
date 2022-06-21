package com.zapcorp;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;


@RequiredArgsConstructor
public class MemoryBound implements Runnable {

    private final int bytes;

    @Override
    public void run() {
        final var array = new int[bytes / Integer.BYTES];
        Arrays.fill(array, 1);
        final var el = array[20];
    }
}

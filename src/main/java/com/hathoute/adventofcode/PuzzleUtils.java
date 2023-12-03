package com.hathoute.adventofcode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public final class PuzzleUtils {

    public static List<String> readLinesFromFile(String path) {
        try (var input = PuzzleUtils.class.getResourceAsStream(path);
             var reader = new InputStreamReader(input);
             var bufferedReader = new BufferedReader(reader)) {
            return bufferedReader.lines().toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isNumber(char c) {
        return c >= '0' && c <= '9';
    }
}

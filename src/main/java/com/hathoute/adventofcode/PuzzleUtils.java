package com.hathoute.adventofcode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;

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

    public static String substringAfter(String str, char character) {
        return str.substring(str.indexOf(character) + 1);
    }

    public static Stream<Integer> parseNumbers(String numbers, String separator) {
        return Arrays.stream(numbers.split(separator))
                .map(String::trim)
                .filter(not(String::isBlank))
                .map(Integer::valueOf);
    }
}

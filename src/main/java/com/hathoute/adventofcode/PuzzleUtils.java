package com.hathoute.adventofcode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
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
        return parse(numbers, separator, Integer::parseInt);
    }

    public static Stream<Long> parseLongs(String numbers, String separator) {
        return parse(numbers, separator, Long::parseLong);
    }

    public static <T> Stream<T> parse(String str, String separator, Function<String, T> converter) {
        return Arrays.stream(str.split(separator))
                .map(String::trim)
                .filter(not(String::isBlank))
                .map(converter);
    }

    public record Tuple2<T1, T2>(T1 left, T2 right) {
        public static <V1, V2> Tuple2<V1, V2> of(V1 v1, V2 v2) {
            return new Tuple2<>(v1, v2);
        }
    }

    public record Tuple3<T1, T2, T3>(T1 left, T2 mid, T3 right) {
        public static <V1, V2, V3> Tuple3<V1, V2, V3> of(V1 v1, V2 v2, V3 v3) {
            return new Tuple3<>(v1, v2, v3);
        }
    }
}

package com.hathoute.adventofcode;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.summingInt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.math3.primes.Primes;

public final class PuzzleUtils {

  public static List<String> readLinesFromFile(final String path) {
    try (final var input = PuzzleUtils.class.getResourceAsStream(
        path); final var reader = new InputStreamReader(
        input); final var bufferedReader = new BufferedReader(reader)) {
      return bufferedReader.lines().toList();
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static boolean isNumber(final char c) {
    return c >= '0' && c <= '9';
  }

  public static String substringAfter(final String str, final char character) {
    return str.substring(str.indexOf(character) + 1);
  }

  public static Stream<Integer> parseNumbers(final String numbers, final String separator) {
    return parse(numbers, separator, Integer::parseInt);
  }

  public static Stream<Long> parseLongs(final String numbers, final String separator) {
    return parse(numbers, separator, Long::parseLong);
  }

  public static <T> Stream<T> parse(final String str, final String separator,
      final Function<String, T> converter) {
    return Arrays.stream(str.split(separator))
                 .map(String::trim)
                 .filter(not(String::isBlank))
                 .map(converter);
  }

  public static Map<Integer, Integer> primeFactors(final int n) {
    // Using summingInt() instead of counting() to produce integers since 'n' is int.
    return Primes.primeFactors(n)
                 .stream()
                 .collect(Collectors.groupingBy(Function.identity(), summingInt(t -> 1)));
  }

  public static <T> T findLast(final List<T> list) {
    if (list.isEmpty()) {
      throw new IllegalArgumentException("Provided list cannot be empty");
    }

    return list.get(list.size() - 1);
  }

  public record Tuple2<T1, T2>(T1 left, T2 right) {
    public static <V1, V2> Tuple2<V1, V2> of(final V1 v1, final V2 v2) {
      return new Tuple2<>(v1, v2);
    }
  }

  public record Tuple3<T1, T2, T3>(T1 left, T2 mid, T3 right) {
    public static <V1, V2, V3> Tuple3<V1, V2, V3> of(final V1 v1, final V2 v2, final V3 v3) {
      return new Tuple3<>(v1, v2, v3);
    }
  }
}

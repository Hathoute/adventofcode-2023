package com.hathoute.adventofcode.day5;

import static com.hathoute.adventofcode.PuzzleUtils.parseLongs;

import com.hathoute.adventofcode.AdventOfCodePuzzle;
import com.hathoute.adventofcode.PuzzleUtils;
import com.hathoute.adventofcode.PuzzleUtils.Tuple2;
import com.hathoute.adventofcode.PuzzleUtils.Tuple3;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SeedFertilizerSolution {
  public static void main(final String[] args) {
    final var input = PuzzleUtils.readLinesFromFile("/day5/SeedFertilizerInput.txt");
    final var resolver = new SeedFertilizer("seed");

    final var result = resolver.solve(input);
    System.out.printf("Solution is: %s%n", result);
  }

  static class SeedFertilizer implements AdventOfCodePuzzle {
    private static final Pattern FROM_TO_PATTERN = Pattern.compile("(\\w+)-to-(\\w+) map:");

    private final String initializerName;

    SeedFertilizer(final String initializerName) {
      this.initializerName = initializerName;
    }

    @Override
    public String solve(final List<String> input) {
      final var initializer = parseInitializer(input.get(0));
      final var rangeMaps = parseMaps(input);

      final var minValue = initializer.stream()
                                      .mapToLong(initVal -> compute(rangeMaps, initializerName, initVal))
                                      .min()
                                      .orElseThrow();

      return String.valueOf(minValue);
    }

    private long compute(final Map<String, Tuple2<String, List<RangeMapEntry>>> rangeMap, final String init,
        final long value) {
      var currentKey = init;
      long currentVal = value;
      while (rangeMap.containsKey(currentKey)) {
        final var ranges = rangeMap.get(currentKey);
        final var val = currentVal;
        currentKey = ranges.left();
        currentVal = ranges.right()
                           .stream()
                           .filter(r -> r.isInRange(val))
                           .findFirst()
                           .map(r -> r.getValue(val))
                           .orElse(currentVal);
      }

      return currentVal;
    }

    private Map<String, Tuple2<String, List<RangeMapEntry>>> parseMaps(final List<String> lines) {
      final var concatenated = lines.stream().skip(2).map(String::trim).collect(Collectors.joining("\n"));

      final var splitMaps = concatenated.split("\n\n");
      return Arrays.stream(splitMaps)
                   .map(s -> s.split("\n"))
                   .map(List::of)
                   .map(SeedFertilizer::parseMap)
                   .collect(Collectors.toMap(Tuple3::left, t3 -> Tuple2.of(t3.mid(), t3.right())));
    }

    private static List<Long> parseInitializer(final String firstLine) {
      final var separatorIndex = firstLine.indexOf(':');
      return parseLongs(firstLine.substring(separatorIndex + 1), " ").toList();
    }

    private static Tuple3<String, String, List<RangeMapEntry>> parseMap(final List<String> subList) {
      final var matcher = FROM_TO_PATTERN.matcher(subList.get(0));
      if (!matcher.find()) {
        throw new IllegalArgumentException("Malformed map header");
      }

      final var fromName = matcher.group(1);
      final var toName = matcher.group(2);

      final var ranges = subList.stream()
                                .skip(1)
                                .map(s -> parseLongs(s, " "))
                                .map(Stream::toList)
                                .map(l -> new RangeMapEntry(l.get(1), l.get(0), l.get(2)))
                                .toList();

      return Tuple3.of(fromName, toName, ranges);
    }

    record RangeMapEntry(long keyStart, long valueStart, long length) {
      boolean isInRange(final long key) {
        return key >= keyStart && (key - keyStart) <= length;
      }

      long getValue(final long key) {
        return valueStart + (key - keyStart);
      }
    }
  }
}

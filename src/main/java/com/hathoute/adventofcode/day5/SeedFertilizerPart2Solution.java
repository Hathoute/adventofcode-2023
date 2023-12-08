package com.hathoute.adventofcode.day5;

import static com.hathoute.adventofcode.PuzzleUtils.parseLongs;
import static java.lang.Math.max;
import static java.lang.Math.min;

import com.hathoute.adventofcode.AdventOfCodePuzzle;
import com.hathoute.adventofcode.PuzzleUtils;
import com.hathoute.adventofcode.PuzzleUtils.Tuple2;
import com.hathoute.adventofcode.PuzzleUtils.Tuple3;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SeedFertilizerPart2Solution {
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
                                      .map(initRange -> compute(rangeMaps, initializerName, initRange))
                                      .flatMap(List::stream)
                                      .mapToLong(RangeEntry::startInclusive)
                                      .min()
                                      .orElseThrow();

      return String.valueOf(minValue);
    }

    private List<RangeEntry> compute(final Map<String, Tuple2<String, Map<RangeEntry, Long>>> rangeMap,
        final String init, final RangeEntry entry) {
      var currentKey = init;
      var currentVal = List.of(entry);
      while (rangeMap.containsKey(currentKey)) {
        final var ranges = rangeMap.get(currentKey);
        currentKey = ranges.left();
        currentVal = computeImage(ranges.right(), currentVal);
        // Could consider merging the result if we run into too many ranges overlapping.
      }

      return currentVal;
    }

    private Map<String, Tuple2<String, Map<RangeEntry, Long>>> parseMaps(final List<String> lines) {
      final var concatenated = lines.stream().skip(2).map(String::trim).collect(Collectors.joining("\n"));

      final var splitMaps = concatenated.split("\n\n");
      return Arrays.stream(splitMaps)
                   .map(s -> s.split("\n"))
                   .map(List::of)
                   .map(SeedFertilizer::parseMap)
                   .collect(Collectors.toMap(Tuple3::left, t3 -> Tuple2.of(t3.mid(), t3.right())));
    }

    private static List<RangeEntry> computeImage(final Map<RangeEntry, Long> map,
        final List<RangeEntry> param) {
      var remainings = Collections.unmodifiableList(param);
      final var image = new ArrayList<RangeEntry>();
      for (final var entry : map.entrySet()) {
        final var computed = remainings.stream()
                                       .map(p -> computeImage(entry.getKey(), entry.getValue(), p))
                                       .toList();

        remainings = computed.stream().map(Tuple2::left).flatMap(List::stream).toList();

        computed.stream().map(Tuple2::right).flatMap(Optional::stream).forEach(image::add);
      }

      image.addAll(remainings);
      return image;
    }

    /**
     * Applies param to the (key, value) pair as explained by the AdventOfCode problem.
     *
     * @return A tuple of a list containing remaining ranges (key \ param) and an optional which
     *     contains the mapped range if it exists.
     */
    private static Tuple2<List<RangeEntry>, Optional<RangeEntry>> computeImage(final RangeEntry key,
        final long value, final RangeEntry param) {
      final var intersectionOpt = key.intersection(param);
      if (intersectionOpt.isEmpty()) {
        return Tuple2.of(List.of(param), Optional.empty());
      }

      final var remainings = new LinkedList<RangeEntry>();
      if (param.startInclusive < key.startInclusive) {
        remainings.addLast(new RangeEntry(param.startInclusive, key.startInclusive));
      }
      if (param.endExclusive > key.endExclusive) {
        remainings.addLast(new RangeEntry(key.endExclusive, param.endExclusive));
      }

      final var intersection = intersectionOpt.get();
      final var startOffset = intersection.startInclusive - key.startInclusive;
      final var image = new RangeEntry(value + startOffset, value + startOffset + intersection.length());

      return Tuple2.of(remainings, Optional.of(image));
    }

    private static List<RangeEntry> parseInitializer(final String firstLine) {
      final var separatorIndex = firstLine.indexOf(':');
      final var nums = parseLongs(firstLine.substring(separatorIndex + 1), " ").toList();
      return IntStream.range(0, nums.size() / 2)
                      .mapToObj(i -> Tuple2.of(nums.get(2 * i), nums.get(2 * i + 1)))
                      .map(t2 -> new RangeEntry(t2.left(), t2.left() + t2.right()))
                      .toList();
    }

    private static Tuple3<String, String, Map<RangeEntry, Long>> parseMap(final List<String> subList) {
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
                                .collect(
                              Collectors.toMap(l -> new RangeEntry(l.get(1), l.get(1) + l.get(2)),
                                  l -> l.get(0)));

      return Tuple3.of(fromName, toName, ranges);
    }

    record RangeEntry(long startInclusive, long endExclusive) {
      boolean intersects(final RangeEntry other) {
        return this.endExclusive > other.startInclusive && other.endExclusive > this.startInclusive;
      }

      Optional<RangeEntry> intersection(final RangeEntry other) {
        if (!intersects(other)) {
          return Optional.empty();
        }

        return Optional.of(new RangeEntry(max(this.startInclusive, other.startInclusive),
            min(this.endExclusive, other.endExclusive)));
      }

      long length() {
        return endExclusive - startInclusive;
      }
    }
  }
}

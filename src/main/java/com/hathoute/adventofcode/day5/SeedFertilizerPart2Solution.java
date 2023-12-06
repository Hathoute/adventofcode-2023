package com.hathoute.adventofcode.day5;

import com.hathoute.adventofcode.AdventOfCodePuzzle;
import com.hathoute.adventofcode.PuzzleUtils;
import com.hathoute.adventofcode.PuzzleUtils.Tuple2;
import com.hathoute.adventofcode.PuzzleUtils.Tuple3;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.hathoute.adventofcode.PuzzleUtils.parseLongs;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class SeedFertilizerPart2Solution {
    public static void main(String[] args) {
        var input = PuzzleUtils.readLinesFromFile("/day5/SeedFertilizerInput.txt");
        var resolver = new SeedFertilizer("seed");

        var result = resolver.solve(input);
        System.out.printf("Solution is: %s%n", result);
    }

    static class SeedFertilizer implements AdventOfCodePuzzle {
        private static final Pattern FROM_TO_PATTERN = Pattern.compile("(\\w+)-to-(\\w+) map:");

        private final String initializerName;

        SeedFertilizer(String initializerName) {
            this.initializerName = initializerName;
        }

        @Override
        public String solve(List<String> input) {
            var initializer = parseInitializer(input.get(0));
            var rangeMaps = parseMaps(input);

            var minValue = initializer.stream()
                    .map(initRange -> compute(rangeMaps, initializerName, initRange))
                    .flatMap(List::stream)
                    .mapToLong(RangeEntry::startInclusive)
                    .min()
                    .orElseThrow();

            return String.valueOf(minValue);
        }

        private List<RangeEntry> compute(Map<String, Tuple2<String, Map<RangeEntry, Long>>> rangeMap, String init, RangeEntry entry) {
            var currentKey = init;
            var currentVal = List.of(entry);
            while (rangeMap.containsKey(currentKey)) {
                var ranges = rangeMap.get(currentKey);
                currentKey = ranges.left();
                currentVal = computeImage(ranges.right(), currentVal);
                // Could consider merging the result if we run into too many ranges overlapping.
            }

            return currentVal;
        }

        private Map<String, Tuple2<String, Map<RangeEntry, Long>>> parseMaps(List<String> lines) {
            var concatenated = lines.stream()
                    .skip(2)
                    .map(String::trim)
                    .collect(Collectors.joining("\n"));

            var splitMaps = concatenated.split("\n\n");
            return Arrays.stream(splitMaps)
                    .map(s -> s.split("\n"))
                    .map(List::of)
                    .map(SeedFertilizer::parseMap)
                    .collect(Collectors.toMap(Tuple3::left, t3 -> Tuple2.of(t3.mid(), t3.right())));
        }

        private static List<RangeEntry> computeImage(Map<RangeEntry, Long> map, List<RangeEntry> param) {
            var remainings = Collections.unmodifiableList(param);
            var image = new ArrayList<RangeEntry>();
            for (var entry : map.entrySet()) {
                var computed = remainings.stream()
                        .map(p -> computeImage(entry.getKey(), entry.getValue(), p))
                        .toList();

                remainings = computed.stream().map(Tuple2::left)
                        .flatMap(List::stream)
                        .toList();

                computed.stream().map(Tuple2::right)
                        .flatMap(Optional::stream)
                        .forEach(image::add);
            }

            image.addAll(remainings);
            return image;
        }

        /**
         * Applies param to the (key, value) pair as explained by the AdventOfCode problem.
         *
         * @return A tuple of a list containing remaining ranges (key \ param) and an optional which contains
         * the mapped range if it exists.
         */
        private static Tuple2<List<RangeEntry>, Optional<RangeEntry>> computeImage(RangeEntry key, long value, RangeEntry param) {
            var intersectionOpt = key.intersection(param);
            if (intersectionOpt.isEmpty()) {
                return Tuple2.of(List.of(param), Optional.empty());
            }

            var remainings = new LinkedList<RangeEntry>();
            if (param.startInclusive < key.startInclusive) {
                remainings.addLast(new RangeEntry(param.startInclusive, key.startInclusive));
            }
            if (param.endExclusive > key.endExclusive) {
                remainings.addLast(new RangeEntry(key.endExclusive, param.endExclusive));
            }

            var intersection = intersectionOpt.get();
            var startOffset = intersection.startInclusive - key.startInclusive;
            var image = new RangeEntry(value + startOffset, value + startOffset + intersection.length());

            return Tuple2.of(remainings, Optional.of(image));
        }

        private static List<RangeEntry> parseInitializer(String firstLine) {
            var separatorIndex = firstLine.indexOf(':');
            var nums = parseLongs(firstLine.substring(separatorIndex + 1), " ").toList();
            return IntStream.range(0, nums.size() / 2)
                    .mapToObj(i -> Tuple2.of(nums.get(2 * i), nums.get(2 * i + 1)))
                    .map(t2 -> new RangeEntry(t2.left(), t2.left() + t2.right()))
                    .toList();
        }

        private static Tuple3<String, String, Map<RangeEntry, Long>> parseMap(List<String> subList) {
            var matcher = FROM_TO_PATTERN.matcher(subList.get(0));
            if (!matcher.find()) {
                throw new IllegalArgumentException("Malformed map header");
            }

            var fromName = matcher.group(1);
            var toName = matcher.group(2);

            var ranges = subList.stream().skip(1)
                    .map(s -> parseLongs(s, " "))
                    .map(Stream::toList)
                    .collect(Collectors.toMap(l -> new RangeEntry(l.get(1), l.get(1) + l.get(2)), l -> l.get(0)));

            return Tuple3.of(fromName, toName, ranges);
        }

        record RangeEntry(long startInclusive, long endExclusive) {
            boolean intersects(RangeEntry other) {
                return this.endExclusive > other.startInclusive && other.endExclusive > this.startInclusive;
            }

            Optional<RangeEntry> intersection(RangeEntry other) {
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

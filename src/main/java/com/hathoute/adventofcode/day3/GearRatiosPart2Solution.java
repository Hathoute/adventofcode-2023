package com.hathoute.adventofcode.day3;

import com.hathoute.adventofcode.AdventOfCodePuzzle;
import com.hathoute.adventofcode.PuzzleUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class GearRatiosPart2Solution {
    public static void main(String[] args) {
        var solver = new GearRatios();
        var input = PuzzleUtils.readLinesFromFile("/day3/GearRatiosInput.txt");

        var result = solver.solve(input);

        System.out.printf("Result is: %s%n", result);
    }

    static class GearRatios implements AdventOfCodePuzzle {

        @Override
        public String solve(List<String> input) {
            var adjacentSum = IntStream.range(0, input.size())
                    .mapToObj(e -> processLine(input, e))
                    .flatMapToInt(e -> e.stream().mapToInt(Integer::intValue))
                    .sum();

            return String.valueOf(adjacentSum);
        }

        private List<Integer> processLine(List<String> input, int line) {
            var integerList = new ArrayList<Integer>();

            var lineStr = input.get(line);
            for (int i = 0; i < lineStr.length(); i++) {
                if (lineStr.charAt(i) != '*') {
                    continue;
                }

                var envelope = buildEnvelope(input, line, i);
                if (envelope.size() != 2) {
                    continue;
                }

                integerList.add(envelope.get(0) * envelope.get(1));
            }

            return integerList;
        }

        private static List<Integer> buildEnvelope(List<String> matrix, int line, int index) {
            var numberEnvelope = new ArrayList<Integer>();
            var lineStr = matrix.get(line);

            // If the character just above (or below) is part of a number, then there is no need to check the ones at the
            //  diagonal since they should be part of this same number.
            // We leave handling edge cases to the extractNumber method, so we don't end up with spaghetti.
            if (line > 0) {
                var currentLine = matrix.get(line - 1);
                extractNumber(currentLine, index)
                        .map(n -> Stream.of(Optional.of(n)))
                        .orElseGet(() -> Stream.of(extractNumber(currentLine, index - 1),
                                extractNumber(currentLine, index + 1)))
                        .flatMap(Optional::stream)
                        .forEach(numberEnvelope::add);
            }
            if (line < matrix.size() - 1) {
                var currentLine = matrix.get(line + 1);
                extractNumber(currentLine, index)
                        .map(n -> Stream.of(Optional.of(n)))
                        .orElseGet(() -> Stream.of(extractNumber(currentLine, index - 1),
                                extractNumber(currentLine, index + 1)))
                        .flatMap(Optional::stream)
                        .forEach(numberEnvelope::add);
            }

            Stream.of(extractNumber(lineStr, index + 1), extractNumber(lineStr, index - 1))
                    .flatMap(Optional::stream)
                    .forEach(numberEnvelope::add);

            return numberEnvelope;
        }

        private static Optional<Integer> extractNumber(String str, int index) {
            if (index < 0 || index >= str.length()) {
                return Optional.empty();
            }

            if (!PuzzleUtils.isNumber(str.charAt(index))) {
                return Optional.empty();
            }

            var start = index;
            var end = index + 1;
            while (start > 0 && PuzzleUtils.isNumber(str.charAt(start - 1))) {
                --start;
            }

            while (end < str.length() && PuzzleUtils.isNumber(str.charAt(end))) {
                ++end;
            }

            return Optional.of(Integer.valueOf(str.substring(start, end)));
        }
    }
}

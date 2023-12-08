package com.hathoute.adventofcode.day3;

import com.hathoute.adventofcode.AdventOfCodePuzzle;
import com.hathoute.adventofcode.PuzzleUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class GearRatiosPart2Solution {
  public static void main(final String[] args) {
    final var solver = new GearRatios();
    final var input = PuzzleUtils.readLinesFromFile("/day3/GearRatiosInput.txt");

    final var result = solver.solve(input);

    System.out.printf("Result is: %s%n", result);
  }

  static class GearRatios implements AdventOfCodePuzzle {

    @Override
    public String solve(final List<String> input) {
      final var adjacentSum = IntStream.range(0, input.size())
                                       .mapToObj(e -> processLine(input, e))
                                       .flatMapToInt(e -> e.stream().mapToInt(Integer::intValue))
                                       .sum();

      return String.valueOf(adjacentSum);
    }

    private List<Integer> processLine(final List<String> input, final int line) {
      final var integerList = new ArrayList<Integer>();

      final var lineStr = input.get(line);
      for (int i = 0; i < lineStr.length(); i++) {
        if (lineStr.charAt(i) != '*') {
          continue;
        }

        final var envelope = buildEnvelope(input, line, i);
        if (envelope.size() != 2) {
          continue;
        }

        integerList.add(envelope.get(0) * envelope.get(1));
      }

      return integerList;
    }

    private static List<Integer> buildEnvelope(final List<String> matrix, final int line, final int index) {
      final var numberEnvelope = new ArrayList<Integer>();
      final var lineStr = matrix.get(line);

      // If the character just above (or below) is part of a number, then there is no need to check the ones at the
      //  diagonal since they should be part of this same number.
      // We leave handling edge cases to the extractNumber method, so we don't end up with spaghetti.
      if (line > 0) {
        final var currentLine = matrix.get(line - 1);
        extractNumber(currentLine, index).map(n -> Stream.of(Optional.of(n)))
                                         .orElseGet(
                                             () -> Stream.of(extractNumber(currentLine, index - 1),
                                                 extractNumber(currentLine, index + 1)))
                                         .flatMap(Optional::stream)
                                         .forEach(numberEnvelope::add);
      }
      if (line < matrix.size() - 1) {
        final var currentLine = matrix.get(line + 1);
        extractNumber(currentLine, index).map(n -> Stream.of(Optional.of(n)))
                                         .orElseGet(
                                             () -> Stream.of(extractNumber(currentLine, index - 1),
                                                 extractNumber(currentLine, index + 1)))
                                         .flatMap(Optional::stream)
                                         .forEach(numberEnvelope::add);
      }

      Stream.of(extractNumber(lineStr, index + 1), extractNumber(lineStr, index - 1))
            .flatMap(Optional::stream)
            .forEach(numberEnvelope::add);

      return numberEnvelope;
    }

    private static Optional<Integer> extractNumber(final String str, final int index) {
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

package com.hathoute.adventofcode.day3;

import com.hathoute.adventofcode.AdventOfCodePuzzle;
import com.hathoute.adventofcode.PuzzleUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class GearRatiosSolution {
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

      var start = 0;
      var index = 0;
      final var lineStr = input.get(line);
      while (index <= lineStr.length()) {
        if (index < lineStr.length() && PuzzleUtils.isNumber(lineStr.charAt(index))) {
          index++;
          continue;
        }

        if (start < index) {
          final var envelope = buildEnvelope(input, line, start, index);
          final var hasSymbol = envelope.chars()
                                        .anyMatch(e -> !PuzzleUtils.isNumber((char) e) && e != '.');
          if (hasSymbol) {
            integerList.add(Integer.valueOf(lineStr.substring(start, index)));
          }
        }

        ++index;
        start = index;
      }

      return integerList;
    }

    private static String buildEnvelope(final List<String> matrix, final int line, final int start, final int end) {
      final var builder = new StringBuilder();
      final var absStart = Math.max(0, start - 1);
      final var absEnd = Math.min(matrix.get(0).length(), end + 1);

      if (line > 0) {
        builder.append(matrix.get(line - 1), absStart, absEnd);
      }
      if (line < matrix.size() - 1) {
        builder.append(matrix.get(line + 1), absStart, absEnd);
      }

      if (start != absStart) {
        builder.append(matrix.get(line).charAt(absStart));
      }
      if (end != absEnd) {
        builder.append(matrix.get(line).charAt(end));
      }

      return builder.toString();
    }
  }
}

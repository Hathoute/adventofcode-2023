package com.hathoute.adventofcode.day1;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.function.Predicate.not;

import com.hathoute.adventofcode.AdventOfCodePuzzle;
import com.hathoute.adventofcode.PuzzleUtils;
import java.util.List;

public class CalibrationDocumentSolution {

  public static void main(final String[] args) {
    final var solver = new CalibrationDocument();
    final var lines = PuzzleUtils.readLinesFromFile("/day1/CalibrationDocumentInput.txt");
    final var result = solver.solve(lines);
    System.out.printf("Solution is: %s%n", result);
  }

  static class CalibrationDocument implements AdventOfCodePuzzle {

    @Override
    public String solve(final List<String> inputLines) {
      final var calibrationSum = inputLines.stream()
                                           .filter(not(String::isBlank))
                                           .mapToInt(this::processLine)
                                           .sum();

      return String.valueOf(calibrationSum);
    }

    private int processLine(final String line) {
      Character firstVal = null;
      Character secondVal = null;
      final var lineSize = line.length();
      for (var i = 0; i < lineSize; i++) {
        if (isNull(firstVal) && PuzzleUtils.isNumber(line.charAt(i))) {
          firstVal = line.charAt(i);
        }

        if (isNull(secondVal) && PuzzleUtils.isNumber(line.charAt(lineSize - 1 - i))) {
          secondVal = line.charAt(lineSize - 1 - i);
        }

        if (nonNull(firstVal) && nonNull(secondVal)) {
          break;
        }
      }

      return Integer.parseInt(String.valueOf(firstVal) + secondVal);
    }

  }
}
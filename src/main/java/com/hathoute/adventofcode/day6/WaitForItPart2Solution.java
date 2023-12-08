package com.hathoute.adventofcode.day6;

import static java.util.function.UnaryOperator.identity;

import com.hathoute.adventofcode.AdventOfCodePuzzle;
import com.hathoute.adventofcode.PuzzleUtils;
import java.util.List;
import java.util.stream.Collectors;

public class WaitForItPart2Solution {

  public static void main(final String[] args) {
    final var solver = new WaitForIt();
    final var lines = PuzzleUtils.readLinesFromFile("/day6/WaitForItInput.txt");
    final var result = solver.solve(lines);
    System.out.printf("Solution is: %s%n", result);
  }

  static class WaitForIt implements AdventOfCodePuzzle {

    private static final double DEFAULT_ACCELERATION = 1.0d;

    @Override
    public String solve(final List<String> inputLines) {
      final var entry = parseLines(inputLines.get(0), inputLines.get(1));
      final var waysCount = processRace(entry);

      return String.valueOf(waysCount);
    }

    private RaceEntry parseLines(final String times, final String distances) {
      final var timeStr = PuzzleUtils.parse(times.substring(5), "\\s+", identity())
                                     .collect(Collectors.joining());
      final var distanceStr = PuzzleUtils.parse(distances.substring(9), "\\s+", identity())
                                         .collect(Collectors.joining());

      return new RaceEntry(Long.parseLong(timeStr), Long.parseLong(distanceStr));
    }

    private long processRace(final RaceEntry entry) {
      final var target = entry.distance + 1;
      final var sqrtDelta = Math.sqrt(
          entry.recordTime * entry.recordTime - 4 * target / DEFAULT_ACCELERATION);
      final var lower = Math.max(1, Math.ceil((entry.recordTime - sqrtDelta) / 2));
      final var higher = Math.max(1, Math.floor((entry.recordTime + sqrtDelta) / 2));

      return (long) (higher - lower + 1);
    }

    record RaceEntry(long recordTime, long distance) {
    }

  }
}
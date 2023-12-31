package com.hathoute.adventofcode.day6;

import com.hathoute.adventofcode.AdventOfCodePuzzle;
import com.hathoute.adventofcode.PuzzleUtils;
import java.util.List;
import java.util.stream.IntStream;

public class WaitForItSolution {

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
      final var entries = parseLines(inputLines.get(0), inputLines.get(1));
      final var waysCount = entries.stream().mapToLong(this::processRace).reduce(1, Math::multiplyExact);

      return String.valueOf(waysCount);
    }

    private List<RaceEntry> parseLines(final String times, final String distances) {
      final var timeNums = PuzzleUtils.parseLongs(times.substring(5), "\\s+").toList();
      final var distanceNums = PuzzleUtils.parseLongs(distances.substring(9), "\\s+").toList();

      return IntStream.range(0, timeNums.size())
                      .mapToObj(i -> new RaceEntry(timeNums.get(i), distanceNums.get(i)))
                      .toList();
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
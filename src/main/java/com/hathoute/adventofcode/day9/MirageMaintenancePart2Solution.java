package com.hathoute.adventofcode.day9;

import com.google.common.collect.Lists;
import com.hathoute.adventofcode.AdventOfCodePuzzle;
import com.hathoute.adventofcode.PuzzleUtils;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class MirageMaintenancePart2Solution {

  public static void main(final String[] args) {
    final var solver = new MirageMaintenancePart2();
    final var lines = PuzzleUtils.readLinesFromFile("/day9/MirageMaintenance.txt");
    final var result = solver.solve(lines);
    System.out.printf("Solution is: %s%n", result);
  }

  static class MirageMaintenancePart2 implements AdventOfCodePuzzle {

    // Only change between this and MirageMaintenance.class is

    @Override
    public String solve(final List<String> input) {
      final var valuesSum = input.stream()
                                 .map(s -> PuzzleUtils.parseLongs(s, " ").toList())
                                 .map(Lists::reverse)
                                 .mapToLong(this::nextValue)
                                 .sum();

      return String.valueOf(valuesSum);
    }

    private long nextValue(final List<Long> values) {
      return derivatives(values).stream().mapToLong(PuzzleUtils::findLast).sum();
    }

    private List<List<Long>> derivatives(final List<Long> values) {
      final var result = new LinkedList<List<Long>>();
      var currentDiff = values;

      while (currentDiff.stream().anyMatch(n -> n != 0)) {
        result.addLast(currentDiff);
        currentDiff = derivative(currentDiff);
      }

      return result;
    }

    private static List<Long> derivative(final List<Long> values) {
      if (values.size() < 2) {
        throw new IllegalArgumentException("values");
      }

      final var previous = new AtomicLong(values.get(0));
      return values.stream().skip(1).map(n -> n - previous.getAndSet(n)).toList();
    }
  }
}
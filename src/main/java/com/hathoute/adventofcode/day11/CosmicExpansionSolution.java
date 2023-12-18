package com.hathoute.adventofcode.day11;

import static java.util.stream.Collectors.toSet;

import com.google.common.collect.Streams;
import com.hathoute.adventofcode.AdventOfCodePuzzle;
import com.hathoute.adventofcode.PuzzleUtils;
import com.hathoute.adventofcode.PuzzleUtils.Tuple2;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import org.apache.commons.math3.util.Combinations;

public class CosmicExpansionSolution {

  public static void main(final String[] args) {
    final var solver = new CosmicExpansion();
    final var lines = PuzzleUtils.readLinesFromFile("/day11/CosmicExpansion.txt");
    final var result = solver.solve(lines);
    System.out.printf("Solution is: %s%n", result);
  }

  static class CosmicExpansion implements AdventOfCodePuzzle {

    private static final char EMPTY_SPACE = '.';

    @Override
    public String solve(final List<String> input) {
      final var emptyRows = emptyRows(input);
      final var glaaxies = extracted(input, emptyRows);
      final var permutations = combinationsOfTwo(glaaxies);

      final var distanceSum = permutations.stream()
                                          .mapToLong(t2 -> distance(t2.left(), t2.right()))
                                          .sum();

      return String.valueOf(distanceSum);
    }

    private List<Tuple2<Integer, Integer>> extracted(final List<String> input,
        final Set<Integer> emptyRows) {
      final var galaxies = new LinkedList<Tuple2<Integer, Integer>>();

      var currentColumnOffset = 0;
      for (var column = 0; column < input.get(0).length(); column++) {
        final var columnGalaxies = parseColumn(input, emptyRows, column, currentColumnOffset);
        if (columnGalaxies.isEmpty()) {
          currentColumnOffset++;
        }

        galaxies.addAll(columnGalaxies);
      }

      return galaxies;
    }

    private List<Tuple2<Integer, Integer>> parseColumn(final List<String> input,
        final Set<Integer> emptyRows, final int column, final int colOffset) {
      final var result = new LinkedList<Tuple2<Integer, Integer>>();
      var currentRowOffset = 0;
      for (var row = 0; row < input.size(); row++) {
        if (emptyRows.contains(row)) {
          currentRowOffset++;
          continue;
        }

        if (input.get(row).charAt(column) != EMPTY_SPACE) {
          result.addLast(Tuple2.of(row + currentRowOffset, column + colOffset));
        }
      }

      return result;
    }

    private Set<Integer> emptyRows(final List<String> input) {
      return IntStream.range(0, input.size())
                      .filter(i -> input.get(i).chars().allMatch(c -> c == EMPTY_SPACE))
                      .boxed()
                      .collect(toSet());

    }

    private static long distance(final Tuple2<Integer, Integer> e1,
        final Tuple2<Integer, Integer> e2) {
      return Math.abs(e1.left() - e2.left()) + Math.abs(e1.right() - e2.right());
    }

    private static <T> List<Tuple2<T, T>> combinationsOfTwo(final List<T> elements) {
      final var combinations = new Combinations(elements.size(), 2);
      return Streams.stream(combinations)
                    .map(arr -> Tuple2.of(elements.get(arr[0]), elements.get(arr[1])))
                    .toList();
    }
  }
}
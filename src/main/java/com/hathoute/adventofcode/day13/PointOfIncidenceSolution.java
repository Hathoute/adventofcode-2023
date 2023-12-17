package com.hathoute.adventofcode.day13;

import com.hathoute.adventofcode.AdventOfCodePuzzle;
import com.hathoute.adventofcode.PuzzleUtils;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PointOfIncidenceSolution {

  public static void main(final String[] args) {
    final var solver = new PointOfIncidence();
    final var lines = PuzzleUtils.readLinesFromFile("/day13/PointOfIncidenceInput.txt");
    final var result = solver.solve(lines);
    System.out.printf("Solution is: %s%n", result);
  }

  static class PointOfIncidence implements AdventOfCodePuzzle {
    private static final int HORIZONTAL_MULTIPLIER = 100;

    @Override
    public String solve(final List<String> input) {
      final var patterns = extractPatterns(input);
      final var horizontalSum = patterns.stream()
                                        .map(this::processPattern)
                                        .flatMap(Optional::stream)
                                        .map(i -> i * HORIZONTAL_MULTIPLIER)
                                        .reduce(0, Integer::sum);

      final var verticalSum = patterns.stream()
                                      .map(this::flipPattern)
                                      .map(this::processPattern)
                                      .flatMap(Optional::stream)
                                      .reduce(0, Integer::sum);

      return String.valueOf(horizontalSum + verticalSum);
    }

    private List<List<String>> extractPatterns(final List<String> input) {
      final var patterns = new LinkedList<List<String>>();
      var start = 0;

      for (var i = 0; i <= input.size(); i++) {
        if (i == input.size() || input.get(i).isBlank()) {
          patterns.add(input.subList(start, i));
          start = i + 1;
        }
      }

      return patterns;
    }

    private List<String> flipPattern(final List<String> pattern) {
      return IntStream.range(0, pattern.get(0).length())
                      .mapToObj(c -> pattern.stream()
                                            .map(s -> s.charAt(c))
                                            .map(Object::toString)
                                            .collect(Collectors.joining()))
                      .toList();
    }

    private Optional<Integer> processPattern(final List<String> pattern) {
      final var potentialReflections = new ArrayList<Integer>();

      for (var currentRow = 1; currentRow < pattern.size(); currentRow++) {
        final var currentRowPattern = pattern.get(currentRow);
        potentialReflections.add(currentRow);

        // Handle existing potential reflections
        var reflectionIndex = 0;
        while (reflectionIndex < potentialReflections.size()) {
          final var reflectionRow = potentialReflections.get(reflectionIndex);
          final var reflectedRow = 2 * reflectionRow - currentRow - 1;
          if (reflectedRow < 0) {
            return Optional.of(reflectionRow);
          }

          final var reflectedRowPattern = pattern.get(reflectedRow);

          if (!reflectedRowPattern.equals(currentRowPattern)) {
            potentialReflections.remove(reflectionIndex);
          } else {
            reflectionIndex++;
          }
        }
      }

      return potentialReflections.stream().findFirst();
    }
  }
}
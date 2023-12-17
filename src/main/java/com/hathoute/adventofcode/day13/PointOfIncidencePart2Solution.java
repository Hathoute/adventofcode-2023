package com.hathoute.adventofcode.day13;

import com.hathoute.adventofcode.AdventOfCodePuzzle;
import com.hathoute.adventofcode.PuzzleUtils;
import com.hathoute.adventofcode.PuzzleUtils.Tuple2;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.text.similarity.LevenshteinDistance;

public class PointOfIncidencePart2Solution {

  public static void main(final String[] args) {
    final var solver = new PointOfIncidencePart2();
    final var lines = PuzzleUtils.readLinesFromFile("/day13/PointOfIncidenceInput.txt");
    final var result = solver.solve(lines);
    System.out.printf("Solution is: %s%n", result);
  }

  static class PointOfIncidencePart2 implements AdventOfCodePuzzle {
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
      final var potentialReflections = new ArrayList<Tuple2<Integer, Integer>>();

      for (var currentRow = 1; currentRow < pattern.size(); currentRow++) {
        final var currentRowPattern = pattern.get(currentRow);
        potentialReflections.add(Tuple2.of(currentRow, 0));

        // Handle existing potential reflections
        var reflectionIndex = 0;
        while (reflectionIndex < potentialReflections.size()) {
          final var reflectionRowData = potentialReflections.get(reflectionIndex);
          final var reflectedRow = 2 * reflectionRowData.left() - currentRow - 1;

          if (reflectedRow < 0) {
            if (reflectionRowData.right() == 0) {
              potentialReflections.remove(reflectionIndex);
              continue;
            }

            return Optional.of(reflectionRowData.left());
          }

          final var reflectedRowPattern = pattern.get(reflectedRow);
          final var totalDistance =
              reflectionRowData.right() + LevenshteinDistance.getDefaultInstance()
                                                             .apply(currentRowPattern,
                                                                 reflectedRowPattern);

          potentialReflections.remove(reflectionIndex);
          if (totalDistance < 2) {
            potentialReflections.add(reflectionIndex,
                Tuple2.of(reflectionRowData.left(), totalDistance));
            reflectionIndex++;
          }
        }
      }

      return potentialReflections.stream()
                                 .filter(t2 -> t2.right() == 1)
                                 .map(Tuple2::left)
                                 .findFirst();
    }
  }
}
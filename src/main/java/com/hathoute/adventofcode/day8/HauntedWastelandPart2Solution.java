package com.hathoute.adventofcode.day8;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.maxBy;

import com.hathoute.adventofcode.AdventOfCodePuzzle;
import com.hathoute.adventofcode.PuzzleUtils;
import com.hathoute.adventofcode.PuzzleUtils.Tuple2;
import com.hathoute.adventofcode.PuzzleUtils.Tuple3;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HauntedWastelandPart2Solution {

  public static void main(final String[] args) {
    final var solver = new HauntedWastelandPart2();
    final var lines = PuzzleUtils.readLinesFromFile("/day8/HauntedWasteland.txt");
    final var result = solver.solve(lines);
    System.out.printf("Solution is: %s%n", result);
  }

  static class HauntedWastelandPart2 implements AdventOfCodePuzzle {

    private static final Pattern LINE_PATTERN = Pattern.compile(
        "(\\w{3}) = \\((\\w{3}), (\\w{3})\\)");
    private static final char START_CHAR = 'A';
    private static final char FINISH_CHAR = 'Z';

    @Override
    public String solve(final List<String> input) {
      final var sequence = input.get(0);
      final var elements = input.stream()
                                .skip(2)
                                .map(this::parseLine)
                                .collect(Collectors.toMap(Tuple3::left,
                                    t3 -> new Tuple2<>(t3.mid(), t3.right())));

      final var startPositions = elements.keySet()
                                         .stream()
                                         .filter(s -> s.charAt(2) == START_CHAR)
                                         .toList();

      final var startMemories = startPositions.stream()
                                              .map(pos -> Tuple2.of(pos,
                                                  closestFinish(elements, pos, sequence)))
                                              .collect(
                                                  Collectors.toMap(Tuple2::left, Tuple2::right));

      // Doing the same operation as above for finishPositions, it is seen that the steps from
      // XXXA -> XXXZ are equal to XXXZ -> XXXZ (where XXX are the same, not a placeholder),
      // therefore making this problem much, much easier.

      // One can also deduce, from running this once, that these steps are prime numbers for which
      // finding the least common multiplier is as simple as multiplying the numbers.
      // But to make this a little bit interesting, I've chosen to omit this characteristic.

      final var resultFactorized = startMemories.values()
                                                .stream()
                                                .map(Tuple2::right)
                                                .map(PuzzleUtils::primeFactors)
                                                .flatMap(e -> e.entrySet().stream())
                                                .collect(Collectors.groupingBy(Entry::getKey,
                                                    maxBy(comparingInt(Entry::getValue))));

      final var result = resultFactorized.values()
                                         .stream()
                                         .filter(Optional::isPresent)
                                         .map(Optional::get)
                                         .mapToLong(e -> (long) Math.pow(e.getKey(), e.getValue()))
                                         .reduce(Math::multiplyExact)
                                         .orElse(1L);

      return String.valueOf(result * sequence.length());
    }

    private Tuple2<String, Integer> closestFinish(
        final Map<String, Tuple2<String, String>> elements, final String position,
        final String sequence) {
      var currentPosition = position;
      var runs = 0;

      do {
        currentPosition = runSequence(elements, currentPosition, sequence);
        runs++;
      } while (currentPosition.charAt(2) != FINISH_CHAR);

      return Tuple2.of(currentPosition, runs);
    }

    private String runSequence(final Map<String, Tuple2<String, String>> elements,
        final String position, final String sequence) {
      var currentPosition = position;
      for (final var instruction : sequence.toCharArray()) {
        currentPosition = runInstruction(elements.get(currentPosition), instruction);
      }
      return currentPosition;
    }

    private String runInstruction(final Tuple2<String, String> choice, final char instruction) {
      return instruction == 'R' ? choice.right() : choice.left();
    }

    private Tuple3<String, String, String> parseLine(final String line) {
      final var matcher = LINE_PATTERN.matcher(line);
      if (!matcher.find()) {
        throw new IllegalArgumentException("line");
      }

      return Tuple3.of(matcher.group(1), matcher.group(2), matcher.group(3));
    }
  }
}
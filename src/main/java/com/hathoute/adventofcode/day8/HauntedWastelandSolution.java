package com.hathoute.adventofcode.day8;

import com.hathoute.adventofcode.AdventOfCodePuzzle;
import com.hathoute.adventofcode.PuzzleUtils;
import com.hathoute.adventofcode.PuzzleUtils.Tuple2;
import com.hathoute.adventofcode.PuzzleUtils.Tuple3;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HauntedWastelandSolution {

  public static void main(final String[] args) {
    final var solver = new HauntedWasteland();
    final var lines = PuzzleUtils.readLinesFromFile("/day8/HauntedWasteland.txt");
    final var result = solver.solve(lines);
    System.out.printf("Solution is: %s%n", result);
  }

  static class HauntedWasteland implements AdventOfCodePuzzle {

    private static final Pattern LINE_PATTERN = Pattern.compile(
        "(\\w{3}) = \\((\\w{3}), (\\w{3})\\)");
    private static final String START = "AAA";
    private static final String FINISH = "ZZZ";

    @Override
    public String solve(final List<String> input) {
      final var sequence = input.get(0);
      final var elements = input.stream()
                                .skip(2)
                                .map(this::parseLine)
                                .collect(Collectors.toMap(Tuple3::left,
                                    t3 -> new Tuple2<>(t3.mid(), t3.right())));

      var currentPosition = START;
      var steps = 0;
      while (!currentPosition.equals(FINISH)) {
        currentPosition = runSequence(elements, currentPosition, sequence);
        steps += sequence.length();
      }

      return String.valueOf(steps);
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

      return new Tuple3<>(matcher.group(1), matcher.group(2), matcher.group(3));
    }
  }
}
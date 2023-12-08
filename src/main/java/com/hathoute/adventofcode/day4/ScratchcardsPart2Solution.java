package com.hathoute.adventofcode.day4;

import static com.hathoute.adventofcode.PuzzleUtils.parseNumbers;
import static com.hathoute.adventofcode.PuzzleUtils.substringAfter;

import com.hathoute.adventofcode.AdventOfCodePuzzle;
import com.hathoute.adventofcode.PuzzleUtils;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ScratchcardsPart2Solution {
  public static void main(final String[] args) {
    final var input = PuzzleUtils.readLinesFromFile("/day4/ScratchcardsInput.txt");
    final var resolver = new ScratchcardsPart2();

    final var result = resolver.solve(input);
    System.out.printf("Solution is: %s%n", result);
  }

  static class ScratchcardsPart2 implements AdventOfCodePuzzle {

    /**
     * instances[i] = 1 + sum(1, i-1, j -> (matches[i-j] > j) * instances[i-j])
     */

    @Override
    public String solve(final List<String> input) {
      final var matches = input.stream()
                               .map(ScratchcardsPart2::parseGame)
                               .map(this::getGameScore)
                               .toList();

      final var instanceCache = new int[matches.size()];
      final var totalInstances = IntStream.range(0, matches.size())
                                          .sequential()
                                          .map(
                                        idx -> computeAndCacheInstance(idx, instanceCache, matches))
                                          .sum();

      return String.valueOf(totalInstances);
    }

    private int computeAndCacheInstance(final int index, final int[] instances, final List<Integer> matches) {
      instances[index] = 1 + IntStream.range(1, index + 1)
                                      .filter(j -> matches.get(index - j) >= j)
                                      .map(j -> instances[index - j])
                                      .sum();

      return instances[index];
    }

    private int getGameScore(final Game game) {
      return (int) game.playerNumbers.stream().filter(game.winningNumbers::contains).count();
    }

    private static Game parseGame(final String line) {
      final var numbers = substringAfter(line, ':').split("\\|");
      return new Game(parseNumbers(numbers[0], " ").collect(Collectors.toSet()),
          parseNumbers(numbers[1], " ").collect(Collectors.toSet()));
    }

    record Game(Set<Integer> winningNumbers, Set<Integer> playerNumbers) {
    }
  }
}

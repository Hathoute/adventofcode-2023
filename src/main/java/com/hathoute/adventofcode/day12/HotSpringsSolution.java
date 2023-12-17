package com.hathoute.adventofcode.day12;

import static org.apache.commons.math3.util.CombinatoricsUtils.binomialCoefficient;

import com.hathoute.adventofcode.AdventOfCodePuzzle;
import com.hathoute.adventofcode.PuzzleUtils;
import com.hathoute.adventofcode.PuzzleUtils.Either;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import lombok.Builder;

public class HotSpringsSolution {

  public static void main(final String[] args) {
    final var solver = new HotSprings();
    final var lines = PuzzleUtils.readLinesFromFile("/day12/HotSpringsInput.txt");
    final var result = solver.solve(lines);
    System.out.printf("Solution is: %s%n", result);
  }

  static class HotSprings implements AdventOfCodePuzzle {

    private static final char BROKEN_PIPE = '#';
    private static final char OPERATIONAL_PIPE = '.';
    private static final char UNKNOWN_PIPE = '?';

    @Override
    public String solve(final List<String> input) {
      final var total = input.stream().map(this::parseLine).mapToLong(this::processLine).sum();

      return String.valueOf(total);
    }

    private GroupState parseLine(final String line) {
      final var splitted = line.split(" ");
      return GroupState.builder()
                       .text(splitted[0])
                       .numbers(PuzzleUtils.parseNumbers(splitted[1], ",").toList())
                       .currentTextIndex(0)
                       .currentNumberIndex(0)
                       .possibleArrangements(1)
                       .build();
    }

    private long processLine(final GroupState state) {
      final var remainings = new LinkedList<GroupState>();
      remainings.addLast(state);

      final var possibilities = new AtomicLong(0);

      while (!remainings.isEmpty()) {
        processState(remainings.pollFirst()).forEither(
            l -> possibilities.set(possibilities.get() + l), remainings::addAll);
      }

      return possibilities.get();
    }

    private Either<Long, List<GroupState>> processState(final GroupState state) {
      final var currentIndex = skipToNext(state.text, state.currentTextIndex, OPERATIONAL_PIPE);

      if (currentIndex >= state.text.length()) {
        return state.currentNumberIndex == state.numbers.size() ? Either.ofLeft(
            state.possibleArrangements) : Either.ofRight(List.of());
      }

      if (state.text.charAt(currentIndex) == BROKEN_PIPE) {
        return Either.ofRight(processBroken(state, currentIndex));
      }

      final var groupEnd = skipToNext(state.text, currentIndex, UNKNOWN_PIPE);
      if (groupEnd < state.text.length() && state.text.charAt(groupEnd) == BROKEN_PIPE) {
        return Either.ofRight(processGroupEndingWithBrokenPipe(state, currentIndex, groupEnd));
      }

      return Either.ofRight(processGroupBetweenOperationals(state, currentIndex, groupEnd));
    }

    private List<GroupState> processGroupEndingWithBrokenPipe(final GroupState state,
        final int groupStart, final int groupEnd) {
      if (state.currentNumberIndex >= state.numbers.size()) {
        return List.of();
      }

      final var results = new LinkedList<GroupState>();

      // Brute force all the way to the end of the group
      for (var subGroupStart = groupStart; subGroupStart < groupEnd; subGroupStart++) {
        results.addAll(processBroken(state, subGroupStart));
      }

      // Add the possibility that all pipes are operational
      results.add(state.toBuilder().currentTextIndex(groupEnd).build());
      return results;
    }

    private List<GroupState> processGroupBetweenOperationals(final GroupState state,
        final int groupStart, final int groupEnd) {
      final var results = new LinkedList<GroupState>();
      final var groupingSize = groupEnd - groupStart;

      var currentSize = 0;
      var currentNumberIndex = state.currentNumberIndex;
      while (currentSize <= groupingSize) {
        final var spacesLeft = groupingSize - currentSize;
        final var groupsToPut = currentNumberIndex - state.currentNumberIndex;
        results.add(state.toBuilder()
                         .currentTextIndex(groupEnd + 1)
                         .currentNumberIndex(currentNumberIndex)
                         .possibleArrangements(state.possibleArrangements * binomialCoefficient(
                             spacesLeft + groupsToPut, spacesLeft))
                         .build());

        if (currentNumberIndex >= state.numbers.size()) {
          break;
        }

        currentSize += state.numbers.get(currentNumberIndex) + ((groupsToPut == 0) ? 0 : 1);
        currentNumberIndex++;
      }

      return results;
    }

    private List<GroupState> processBroken(final GroupState state, final int startIndex) {
      if (state.currentNumberIndex >= state.numbers.size()) {
        return List.of();
      }

      final var brokenConstraint = state.numbers.get(state.currentNumberIndex);
      var offset = 1;
      while (offset < brokenConstraint) {
        if (startIndex + offset == state.text.length()
            || state.text.charAt(startIndex + offset) == OPERATIONAL_PIPE) {
          return List.of();
        }

        offset++;
      }

      if (startIndex + offset < state.text.length()
          && state.text.charAt(startIndex + offset) == BROKEN_PIPE) {
        return List.of();
      }

      // Next text index is the startIndex (where we found the first #) plus the offset
      //  (with which we will verify our constraint), plus one because the next pipe should
      //  be an operational one.
      return List.of(state.toBuilder()
                          .currentTextIndex(startIndex + offset + 1)
                          .currentNumberIndex(state.currentNumberIndex + 1)
                          .build());
    }

    private int skipToNext(final String text, final int currentTextIndex, final char skipChar) {
      var currentIndex = currentTextIndex;
      while (currentIndex < text.length() && text.charAt(currentIndex) == skipChar) {
        currentIndex++;
      }

      return currentIndex;
    }

    @Builder(toBuilder = true)
    record GroupState(String text, int currentTextIndex, List<Integer> numbers,
                      int currentNumberIndex, long possibleArrangements) {
    }
  }
}
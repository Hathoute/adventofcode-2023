package com.hathoute.adventofcode.day12;

import static org.apache.commons.math3.util.CombinatoricsUtils.binomialCoefficient;

import com.hathoute.adventofcode.AdventOfCodePuzzle;
import com.hathoute.adventofcode.PuzzleUtils;
import com.hathoute.adventofcode.PuzzleUtils.Either;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import lombok.Builder;

public class HotSpringsPart2Solution {

  public static void main(final String[] args) {
    final var solver = new HotSpringsPart2();
    final var lines = PuzzleUtils.readLinesFromFile("/day12/HotSpringsInput.txt");
    final var result = solver.solve(lines);
    System.out.printf("Solution is: %s%n", result);
  }

  static class HotSpringsPart2 implements AdventOfCodePuzzle {

    private static final char BROKEN_PIPE = '#';
    private static final char OPERATIONAL_PIPE = '.';
    private static final char UNKNOWN_PIPE = '?';

    private static final int REPEAT_COUNT = 5;

    @Override
    public String solve(final List<String> input) {
      final var total = input.stream().map(this::parseLine).mapToLong(this::processLine).sum();

      return String.valueOf(total);
    }

    private GroupState parseLine(final String line) {
      final var splitted = line.split(" ");
      final var numbers = PuzzleUtils.parseNumbers(splitted[1], ",").toList();
      final var repeatedText = String.join(String.valueOf(UNKNOWN_PIPE),
          Collections.nCopies(REPEAT_COUNT, splitted[0]));
      final var repeatedNumbers = Collections.nCopies(REPEAT_COUNT, numbers)
                                             .stream()
                                             .flatMap(List::stream)
                                             .toList();
      return GroupState.builder()
                       .text(repeatedText)
                       .numbers(repeatedNumbers)
                       .currentTextIndex(0)
                       .currentNumberIndex(0)
                       .possibleArrangements(1)
                       .build();
    }

    private long processLine(final GroupState state) {
      Map<State, Long> remainings = new HashMap<>();
      remainings.put(state.state(), 1L);

      final var possibilities = new AtomicLong(0);

      while (!remainings.isEmpty()) {
        final var nextWave = remainings.entrySet()
                                       .stream()
                                       .map(e -> state.toBuilder()
                                                      .currentTextIndex(e.getKey().currentTextIndex)
                                                      .currentNumberIndex(
                                                          e.getKey().currentNumberIndex)
                                                      .possibleArrangements(e.getValue())
                                                      .build())
                                       .map(this::processState)
                                       .toList();

        final var possibilitySum = nextWave.stream()
                                           .filter(Either::isLeft)
                                           .mapToLong(Either::left)
                                           .sum();
        possibilities.set(possibilities.get() + possibilitySum);

        remainings = nextWave.stream()
                             .filter(Either::isRight)
                             .map(Either::right)
                             .flatMap(List::stream)
                             .collect(Collectors.toMap(GroupState::state,
                                 GroupState::possibleArrangements, Long::sum));
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
      private State state() {
        return new State(currentTextIndex, currentNumberIndex);
      }
    }

    record State(int currentTextIndex, int currentNumberIndex) {
    }
  }
}
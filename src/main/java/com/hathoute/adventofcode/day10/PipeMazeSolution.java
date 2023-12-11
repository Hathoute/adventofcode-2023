package com.hathoute.adventofcode.day10;

import static java.util.List.of;
import static java.util.function.Predicate.not;

import com.hathoute.adventofcode.AdventOfCodePuzzle;
import com.hathoute.adventofcode.PuzzleUtils;
import com.hathoute.adventofcode.PuzzleUtils.Tuple2;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class PipeMazeSolution {

  public static void main(final String[] args) {
    final var solver = new PipeMaze();
    final var lines = PuzzleUtils.readLinesFromFile("/day10/PipeMaze.txt");
    final var result = solver.solve(lines);
    System.out.printf("Solution is: %s%n", result);
  }

  static class PipeMaze implements AdventOfCodePuzzle {

    private static final char START_TILE = 'S';
    private static final char EMPTY_TILE = '.';

    private static final Map<Character, List<Orientation>> PIPE_CONNECTIONS = Map.of('|',
        of(Orientation.NORTH, Orientation.SOUTH), '-', of(Orientation.EAST, Orientation.WEST), 'L',
        of(Orientation.NORTH, Orientation.EAST), 'J', of(Orientation.NORTH, Orientation.WEST), '7',
        of(Orientation.SOUTH, Orientation.WEST), 'F', of(Orientation.SOUTH, Orientation.EAST));

    private static final Map<Orientation, Tuple2<Integer, Integer>> MOVES = Map.of(
        Orientation.NORTH, Tuple2.of(-1, 0), Orientation.SOUTH, Tuple2.of(1, 0), Orientation.EAST,
        Tuple2.of(0, 1), Orientation.WEST, Tuple2.of(0, -1));

    @Override
    public String solve(final List<String> input) {
      final var start = findStart(input);
      final var pipeLength = Arrays.stream(Orientation.values())
                                   .filter(o -> isValidMove(input, start, o))
                                   .map(o -> findLength(input, start, applyMove(start, o), o))
                                   .findFirst()
                                   .orElseThrow();

      // Guaranteed that pipeLength will be a multiple of 2
      final var maxDistance = pipeLength / 2;
      return String.valueOf(maxDistance);
    }

    private Tuple2<Integer, Integer> findStart(final List<String> tiles) {
      return IntStream.range(0, tiles.size())
                      .mapToObj(i -> Tuple2.of(i, tiles.get(i).indexOf(START_TILE)))
                      .filter(t -> t.right() != -1)
                      .findFirst()
                      .orElseThrow();
    }

    private boolean isValidMove(final List<String> tiles, final Tuple2<Integer, Integer> position,
        final Orientation move) {
      final var nextPosition = applyMove(position, move);
      try {
        final var nextChar = tiles.get(nextPosition.left()).charAt(nextPosition.right());
        return nextChar != EMPTY_TILE && PIPE_CONNECTIONS.get(nextChar)
                                                         .stream()
                                                         .anyMatch(move.opposite()::equals);
      } catch (final IndexOutOfBoundsException e) {
        // We went out of the tiles, obviously not a valid move;
        return false;
      }
    }

    private long findLength(final List<String> tiles, final Tuple2<Integer, Integer> startPosition,
        final Tuple2<Integer, Integer> currentPosition, final Orientation lastMove) {
      var length = 1L;
      var nextPosition = currentPosition;
      var nextMove = lastMove;
      do {
        final var pipeType = tiles.get(nextPosition.left()).charAt(nextPosition.right());
        // Do not go back.
        nextMove = PIPE_CONNECTIONS.get(pipeType)
                                   .stream()
                                   .filter(not(nextMove.opposite()::equals))
                                   .findFirst()
                                   .orElseThrow();

        nextPosition = applyMove(nextPosition, nextMove);
        ++length;
      } while (!startPosition.equals(nextPosition));

      return length;
    }

    private static Tuple2<Integer, Integer> applyMove(final Tuple2<Integer, Integer> position,
        final Orientation move) {
      final var offset = MOVES.get(move);
      return Tuple2.of(position.left() + offset.left(), position.right() + offset.right());
    }

    enum Orientation {
      NORTH, SOUTH, EAST, WEST;

      Orientation opposite() {
        return switch (this) {
          case NORTH -> SOUTH;
          case SOUTH -> NORTH;
          case EAST -> WEST;
          case WEST -> EAST;
        };
      }
    }
  }
}
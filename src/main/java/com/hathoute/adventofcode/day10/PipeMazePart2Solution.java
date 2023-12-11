package com.hathoute.adventofcode.day10;

import static java.util.List.of;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.groupingBy;

import com.hathoute.adventofcode.AdventOfCodePuzzle;
import com.hathoute.adventofcode.PuzzleUtils;
import com.hathoute.adventofcode.PuzzleUtils.Tuple2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PipeMazePart2Solution {

  public static void main(final String[] args) {
    final var solver = new PipeMazePart2();
    final var lines = PuzzleUtils.readLinesFromFile("/day10/PipeMaze.txt");
    final var result = solver.solve(lines);
    System.out.printf("Solution is: %s%n", result);
  }

  static class PipeMazePart2 implements AdventOfCodePuzzle {

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
      final var startTile = inferStartTile(input, start);
      final var tiles = patchInput(input, start, startTile);
      final var startMove = PIPE_CONNECTIONS.get(startTile).get(0);
      final var pipeTiles = findLoop(tiles, start, applyMove(start, startMove), startMove);
      final var tilesInside = pipeTiles.stream()
                                       .collect(groupingBy(Tuple2::left))
                                       .values()
                                       .stream()
                                       .mapToLong(t2 -> tilesInsideLine(tiles, t2))
                                       .sum();

      return String.valueOf(tilesInside);
    }

    private List<String> patchInput(final List<String> input, final Tuple2<Integer, Integer> start,
        final char startTile) {
      final var patchedList = new ArrayList<String>(input.size());
      for (var i = 0; i < input.size(); i++) {
        final var line = input.get(i);
        if (start.left() == i) {
          patchedList.add(
              line.substring(0, start.right()) + startTile + line.substring(start.right() + 1));
        } else {
          patchedList.add(line);
        }
      }

      return patchedList;
    }

    private char inferStartTile(final List<String> input, final Tuple2<Integer, Integer> start) {
      final var possibleMoves = Arrays.stream(Orientation.values())
                                      .filter(o -> isValidMove(input, start, o))
                                      .collect(Collectors.toSet());

      return PIPE_CONNECTIONS.entrySet()
                             .stream()
                             .filter(e -> possibleMoves.containsAll(e.getValue()))
                             .map(Entry::getKey)
                             .findFirst()
                             .orElseThrow();

    }

    private long tilesInsideLine(final List<String> input,
        final List<Tuple2<Integer, Integer>> lineTiles) {
      final var sortedLineTiles = lineTiles.stream()
                                           .map(t2 -> Tuple2.of(
                                               input.get(t2.left()).charAt(t2.right()), t2.right()))
                                           .filter(t2 -> t2.left() != '-')
                                           .sorted(Comparator.comparingInt(Tuple2::right))
                                           .toList();

      var isInside = true;
      var tilesInside = 0;
      for (var i = 0; i < sortedLineTiles.size() - 1; i++) {
        final var currentTile = sortedLineTiles.get(i);
        final var nextTile = sortedLineTiles.get(i + 1);

        final var currentTileConnections = PIPE_CONNECTIONS.get(currentTile.left());
        final var nextTileConnections = PIPE_CONNECTIONS.get(nextTile.left());

        // We defined above the connections as (VerticalConnection, HorizontalConnection) for
        // curved pipes, and since we filtered horizontal pipes, let's use this property here.
        if (currentTileConnections.get(1) == Orientation.EAST
            && nextTileConnections.get(1) == Orientation.WEST) {
          isInside = isInside != (currentTileConnections.get(0) == nextTileConnections.get(0));
          continue;
        }

        if (isInside) {
          tilesInside += nextTile.right() - currentTile.right() - 1;
        }
        isInside = !isInside;
      }

      return tilesInside;
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

    private List<Tuple2<Integer, Integer>> findLoop(final List<String> tiles,
        final Tuple2<Integer, Integer> startPosition,
        final Tuple2<Integer, Integer> currentPosition, final Orientation lastMove) {

      final var loopTiles = new LinkedList<Tuple2<Integer, Integer>>();
      loopTiles.push(currentPosition);

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
        loopTiles.push(nextPosition);
      } while (!startPosition.equals(nextPosition));

      return loopTiles;
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
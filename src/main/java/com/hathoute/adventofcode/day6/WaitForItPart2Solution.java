package com.hathoute.adventofcode.day6;

import com.hathoute.adventofcode.AdventOfCodePuzzle;
import com.hathoute.adventofcode.PuzzleUtils;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.function.UnaryOperator.identity;

public class WaitForItPart2Solution {

    public static void main(String[] args) {
        var solver = new WaitForIt();
        var lines = PuzzleUtils.readLinesFromFile("/day6/WaitForItInput.txt");
        var result = solver.solve(lines);
        System.out.printf("Solution is: %s%n", result);
    }

    static class WaitForIt implements AdventOfCodePuzzle {

        private static final double DEFAULT_ACCELERATION = 1.0d;

        @Override
        public String solve(List<String> inputLines) {
            var entry = parseLines(inputLines.get(0), inputLines.get(1));
            var waysCount = processRace(entry);

            return String.valueOf(waysCount);
        }

        private RaceEntry parseLines(String times, String distances) {
            var timeStr = PuzzleUtils.parse(times.substring(5), "\\s+", identity())
                    .collect(Collectors.joining());
            var distanceStr = PuzzleUtils.parse(distances.substring(9), "\\s+", identity())
                    .collect(Collectors.joining());

            return new RaceEntry(Long.parseLong(timeStr), Long.parseLong(distanceStr));
        }

        private long processRace(RaceEntry entry) {
            var target = entry.distance + 1;
            var sqrtDelta = Math.sqrt(entry.recordTime * entry.recordTime - 4 * target / DEFAULT_ACCELERATION);
            var lower = Math.max(1, Math.ceil((entry.recordTime - sqrtDelta) / 2));
            var higher = Math.max(1, Math.floor((entry.recordTime + sqrtDelta) / 2));

            return (long) (higher - lower + 1);
        }

        record RaceEntry(long recordTime, long distance) {
        }

    }
}
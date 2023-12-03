package com.hathoute.adventofcode.day2;

import com.hathoute.adventofcode.AdventOfCodePuzzle;
import com.hathoute.adventofcode.PuzzleUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CubeGamePossibilityPart2Solution {

    public static void main(String[] args) {
        var inputLines = PuzzleUtils.readLinesFromFile("/day2/CubeGamePossibilityInput.txt");

        var cubeGame = new CubeGamePossibility();
        var solution = cubeGame.solve(inputLines);

        System.out.printf("Solution is %s%n", solution);
    }

    static class CubeGamePossibility implements AdventOfCodePuzzle {

        @Override
        public String solve(List<String> input) {
            var powerSum = input.stream()
                    .map(CubeGamePossibility::parseGame)
                    .mapToInt(this::powerOf)
                    .sum();

            return String.valueOf(powerSum);
        }

        private Integer powerOf(CubeGame cubeGame) {
            return cubeGame.rounds.stream()
                    .flatMap(m -> m.entrySet().stream())
                    .collect(Collectors.groupingBy(Map.Entry::getKey,
                            Collectors.maxBy(Comparator.comparingInt(Map.Entry::getValue))))
                    .values().stream()
                    .mapToInt(v -> v.get().getValue())
                    .reduce(1, Math::multiplyExact);       // Optional should be non-empty, no need to check

        }

        private static CubeGame parseGame(String line) {
            var indexOf = line.indexOf(':');
            var gameId = Integer.parseInt(line.substring(5, indexOf)); // 5 = "Game ".length();
            var rounds = Arrays.stream(line.substring(indexOf + 2).split(";"))
                    .map(CubeGamePossibility::parseRound)
                    .toList();

            return new CubeGame(gameId, rounds);
        }

        private static Map<String, Integer> parseRound(String round) {
            return Arrays.stream(round.split(","))
                    .map(String::trim)
                    .map(entry -> entry.split(" "))
                    .collect(Collectors.toMap(spl -> spl[1].toLowerCase(), spl -> Integer.parseInt(spl[0])));
        }

        record CubeGame(int id, List<Map<String, Integer>> rounds) {
        }
    }

}

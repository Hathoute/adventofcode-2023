package com.hathoute.adventofcode.day2;

import com.hathoute.adventofcode.AdventOfCodePuzzle;
import com.hathoute.adventofcode.PuzzleUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CubeGamePossibilitySolution {

    public static void main(String[] args) {
        var configuration = Map.of("red", 12, "green", 13, "blue", 14);
        var inputLines = PuzzleUtils.readLinesFromFile("/day2/CubeGamePossibilityInput.txt");

        var cubeGame = new CubeGamePossibility(configuration);
        var solution = cubeGame.solve(inputLines);

        System.out.printf("Solution is %s%n", solution);
    }

    static class CubeGamePossibility implements AdventOfCodePuzzle {
        private final Map<String, Integer> configuration;

        public CubeGamePossibility(Map<String, Integer> configuration) {
            this.configuration = configuration;
        }


        @Override
        public String solve(List<String> input) {
            var gameIdSum = input.stream()
                    .map(CubeGamePossibility::parseGame)
                    .filter(this::isGameValid)
                    .mapToInt(CubeGame::id)
                    .sum();

            return String.valueOf(gameIdSum);
        }


        private boolean isGameValid(CubeGame game) {
            return game.rounds().stream()
                    .allMatch(this::isRoundValid);
        }

        private boolean isRoundValid(Map<String, Integer> round) {
            return round.entrySet()
                    .stream()
                    .allMatch(e -> configuration.containsKey(e.getKey()) &&
                            e.getValue() <= configuration.get(e.getKey()));
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
            // toLowerCase() probably not needed, but better safe than sorry
            return Arrays.stream(round.split(","))
                    .map(String::trim)
                    .map(entry -> entry.split(" "))
                    .collect(Collectors.toMap(spl -> spl[1].toLowerCase(), spl -> Integer.parseInt(spl[0])));
        }

        record CubeGame(int id, List<Map<String, Integer>> rounds) {
        }
    }

}

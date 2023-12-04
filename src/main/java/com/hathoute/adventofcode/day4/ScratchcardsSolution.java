package com.hathoute.adventofcode.day4;

import com.hathoute.adventofcode.AdventOfCodePuzzle;
import com.hathoute.adventofcode.PuzzleUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.hathoute.adventofcode.PuzzleUtils.parseNumbers;
import static com.hathoute.adventofcode.PuzzleUtils.substringAfter;

public class ScratchcardsSolution {
    public static void main(String[] args) {
        var input = PuzzleUtils.readLinesFromFile("/day4/ScratchcardsInput.txt");
        var resolver = new Scratchcards();

        var result = resolver.solve(input);
        System.out.printf("Solution is: %s%n", result);
    }

    static class Scratchcards implements AdventOfCodePuzzle {

        @Override
        public String solve(List<String> input) {
            var userScore = input.stream()
                    .map(Scratchcards::parseGame)
                    .map(this::getGameScore)
                    .filter(i -> i > 0)
                    .mapToInt(s -> (int) Math.pow(2, s - 1))
                    .sum();

            return String.valueOf(userScore);
        }

        private long getGameScore(Game game) {
            return game.playerNumbers.stream()
                    .filter(game.winningNumbers::contains)
                    .count();
        }

        private static Game parseGame(String line) {
            var numbers = substringAfter(line, ':').split("\\|");
            return new Game(parseNumbers(numbers[0], " ").collect(Collectors.toSet()),
                    parseNumbers(numbers[1], " ").collect(Collectors.toSet()));
        }

        record Game(Set<Integer> winningNumbers, Set<Integer> playerNumbers) {
        }
    }
}

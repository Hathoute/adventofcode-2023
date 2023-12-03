package com.hathoute.adventofcode.day1;

import com.hathoute.adventofcode.AdventOfCodePuzzle;
import com.hathoute.adventofcode.PuzzleUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.of;
import static java.util.function.Predicate.not;

public class CalibrationDocumentPart2Solution {

    public static void main(String[] args) {
        var solver = new CalibrationDocumentPart2();
        var lines = PuzzleUtils.readLinesFromFile("/day1/CalibrationDocumentInput.txt");
        var result = solver.solve(lines);
        System.out.printf("Solution is: %s%n", result);
    }

    static class CalibrationDocumentPart2 implements AdventOfCodePuzzle {
        private static final Map<String, Character> WORD_TO_NUM = Map.of("zero", '0', "one", '1', "two", '2',
                "three", '3', "four", '4', "five", '5', "six", '6',
                "seven", '7', "eight", '8', "nine", '9');

        @Override
        public String solve(List<String> inputLines) {
            var calibrationSum = inputLines.stream()
                    .filter(not(String::isBlank))
                    .mapToInt(this::processLine)
                    .sum();

            return String.valueOf(calibrationSum);
        }

        private int processLine(String line) {
            char firstVal = '\0';
            char secondVal = '\0';
            var foundIndex = 0;

            for (var i = 0; i < line.length(); i++) {
                var optChar = processCharacter(line, i);
                if (optChar.isPresent()) {
                    firstVal = optChar.get();
                    foundIndex = i;
                    break;
                }
            }

            // It is unnecessary to redo the first loop job, so we process the remaining substring
            // Too lazy to do this in the first part ;-;
            for (var i = line.length() - 1; i > foundIndex; i--) {
                var optChar = processCharacter(line, i);
                if (optChar.isPresent()) {
                    secondVal = optChar.get();
                    break;
                }
            }

            // Nothing found in the remaining substring, use the firstValue.
            if (secondVal == '\0') {
                secondVal = firstVal;
            }

            System.out.printf("%s -> %c%c%n", line, firstVal, secondVal);
            return Integer.parseInt(String.valueOf(firstVal) + secondVal);
        }

        private Optional<Character> processCharacter(String line, int index) {
            if (PuzzleUtils.isNumber(line.charAt(index))) {
                return of(line.charAt(index));
            }

            var subStr = line.substring(index);
            return WORD_TO_NUM.entrySet().stream()
                    .filter(e -> e.getKey().length() <= subStr.length())
                    .filter(e -> subStr.startsWith(e.getKey()))
                    .findFirst()
                    .map(Map.Entry::getValue);

        }
    }
}
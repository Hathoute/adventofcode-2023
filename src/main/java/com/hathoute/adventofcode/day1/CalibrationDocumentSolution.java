package com.hathoute.adventofcode.day1;

import com.hathoute.adventofcode.AdventOfCodePuzzle;
import com.hathoute.adventofcode.PuzzleUtils;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.function.Predicate.not;

public class CalibrationDocumentSolution {

    public static void main(String[] args) {
        var solver = new CalibrationDocument();
        var lines = PuzzleUtils.readLinesFromFile("/day1/CalibrationDocumentInput.txt");
        var result = solver.solve(lines);
        System.out.printf("Solution is: %s%n", result);
    }

    static class CalibrationDocument implements AdventOfCodePuzzle {

        @Override
        public String solve(List<String> inputLines) {
            var calibrationSum = inputLines.stream()
                    .filter(not(String::isBlank))
                    .mapToInt(this::processLine)
                    .sum();

            return String.valueOf(calibrationSum);
        }

        private int processLine(String line) {
            Character firstVal = null;
            Character secondVal = null;
            var lineSize = line.length();
            for (var i = 0; i < lineSize; i++) {
                if (isNull(firstVal) && PuzzleUtils.isNumber(line.charAt(i))) {
                    firstVal = line.charAt(i);
                }

                if (isNull(secondVal) && PuzzleUtils.isNumber(line.charAt(lineSize - 1 - i))) {
                    secondVal = line.charAt(lineSize - 1 - i);
                }

                if (nonNull(firstVal) && nonNull(secondVal)) {
                    break;
                }
            }

            return Integer.parseInt(String.valueOf(firstVal) + secondVal);
        }

    }
}
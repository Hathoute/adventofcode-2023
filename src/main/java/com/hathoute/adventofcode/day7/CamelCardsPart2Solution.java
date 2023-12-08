package com.hathoute.adventofcode.day7;

import static com.hathoute.adventofcode.PuzzleUtils.Tuple2.of;
import static com.hathoute.adventofcode.PuzzleUtils.isNumber;
import static java.util.function.Function.identity;
import static java.util.function.Predicate.not;

import com.hathoute.adventofcode.AdventOfCodePuzzle;
import com.hathoute.adventofcode.PuzzleUtils;
import com.hathoute.adventofcode.PuzzleUtils.Tuple2;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CamelCardsPart2Solution {

  public static void main(final String[] args) {
    final var solver = new CamelCards();
    final var lines = PuzzleUtils.readLinesFromFile("/day7/CamelCardsInput.txt");
    final var result = solver.solve(lines);
    System.out.printf("Solution is: %s%n", result);
  }

  static class CamelCards implements AdventOfCodePuzzle {

    @Override
    public String solve(final List<String> input) {
      final var ordered = input.stream()
                               .map(this::parseInput)
                               .sorted(Comparator.comparing(Tuple2::left))
                               .toList();

      final var total = IntStream.range(0, ordered.size())
                                 .mapToLong(r -> ordered.get(r).right() * (r + 1))
                                 .sum();

      return String.valueOf(total);
    }

    private Tuple2<Hand, Long> parseInput(final String line) {
      final var cards = line.substring(0, 5);
      final var bid = line.substring(6);
      return of(new Hand(cards), Long.parseLong(bid));
    }

    enum HandType {
      HIGH_CARD, ONE_PAIR, TWO_PAIR, THREE_OF_KIND, FULL_HOUSE, FOUR_OF_KIND, FIVE_OF_KIND
    }

    enum CamelCard {
      J, N2, N3, N4, N5, N6, N7, N8, N9, T, Q, K, A
    }

    class Hand implements Comparable<Hand> {
      private static final Map<Tuple2<Integer, Integer>, HandType> CARD_GROUPS = Map.of(of(1, 5),
          HandType.FIVE_OF_KIND, of(2, 6), HandType.FULL_HOUSE, of(2, 4), HandType.FOUR_OF_KIND,
          of(3, 3), HandType.THREE_OF_KIND, of(3, 4), HandType.TWO_PAIR, of(4, 2),
          HandType.ONE_PAIR, of(5, 1), HandType.HIGH_CARD);

      private final List<CamelCard> cards;
      private final HandType type;

      Hand(final String cards) {
        this.cards = cards.chars()
                          .mapToObj(c -> "%s%c".formatted(isNumber((char) c) ? "N" : "", (char) c))
                          .map(CamelCard::valueOf)
                          .toList();
        this.type = getType(this.cards);
      }

      @Override
      public int compareTo(final Hand o) {
        if (this.type.compareTo(o.type) != 0) {
          return this.type.compareTo(o.type);
        }

        return IntStream.range(0, 5)
                        .map(i -> cards.get(i).compareTo(o.cards.get(i)))
                        .filter(i -> i != 0)
                        .findFirst()
                        .orElse(0);
      }

      private static HandType getType(final List<CamelCard> cards) {
        final var js = cards.stream().filter(CamelCard.J::equals).count();
        if (js == 5) {
          return HandType.FIVE_OF_KIND;
        }

        final var groups = cards.stream()
                                .filter(not(CamelCard.J::equals))
                                .collect(Collectors.groupingBy(identity()));
        final var groupsCount = groups.size();
        final var groupsSizes = groups.values()
                                      .stream()
                                      .map(List::size)
                                      .sorted(Comparator.<Integer>naturalOrder().reversed())
                                      .toList();

        final var dominatingGroup = groupsSizes.get(0) + js;
        final var groupsMul = (int) dominatingGroup * groupsSizes.stream()
                                                                 .skip(1)
                                                                 .reduce(Math::multiplyExact)
                                                                 .orElse(1);

        return CARD_GROUPS.get(of(groupsCount, groupsMul));
      }
    }
  }
}
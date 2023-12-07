package day

object Day07 : Day {
    override fun part1(input: List<String>): Result {
        return parse(input)
            .sorted()
            .mapIndexed { index, handWithBid -> (index + 1) * handWithBid.bid }
            .sum()
            .asSuccess()
    }
    override fun part2(input: List<String>): Result {
        return parse2(input)
            .sorted()
            .mapIndexed { index, handWithBid -> (index + 1) * handWithBid.bid }
            .sum()
            .asSuccess()
    }

    fun parse(input: List<String>): List<HandWithBid> {
        return input.map(::parse)
    }

    fun parse2(input: List<String>): List<HandWithBid2> {
        return input.map(::parse2)
    }

    fun parse(line: String): HandWithBid {
        val split = line.split(' ')
        val cards = split[0].map { char -> Card.entries.first { it.char == char } }
        val bid = split[1].toInt()
        return HandWithBid(cards, bid)
    }
    fun parse2(line: String): HandWithBid2 {
        val split = line.split(' ')
        val cards = split[0].map { char -> Card.entries.first { it.char == char } }
        val bid = split[1].toInt()
        return HandWithBid2(cards, bid)
    }

    data class HandWithBid(val cards: List<Card>, val bid: Int) : Comparable<HandWithBid> {
        val cardCounts = cards.groupBy { it }.mapValues { (_, values) -> values.count() }
        val type = HandType.entries.first { it.check(cardCounts) }
        override fun compareTo(other: HandWithBid): Int {
            val typeComparison = -type.compareTo(other.type)
            if (typeComparison != 0) {
                return typeComparison
            }
            return cards.zip(other.cards).map { (left, right) -> left.compareTo(right) }.first { it != 0 }
        }
    }

    data class HandWithBid2(val cards: List<Card>, val bid: Int) : Comparable<HandWithBid2> {
        val cardCounts = cards.groupBy { it }.mapValues { (_, values) -> values.count() }
        val type = HandType.entries.first { it.check(cardCounts.spreadJacks()) }
        override fun compareTo(other: HandWithBid2): Int {
            val typeComparison = -type.compareTo(other.type)
            if (typeComparison != 0) {
                return typeComparison
            }
            return cards.zip(other.cards).map { (left, right) -> CardComparator.compare(left, right) }.first { it != 0 }
        }

        fun Map<Card, Int>.spreadJacks(): Map<Card, Int> {
            // The best way to spread Jacks/Jokers is always to just add them to the highest count
            val returnValue = toMutableMap()
            val jackCount = returnValue.remove(Card.Jack) ?: 0
            if (jackCount == 5) {
                returnValue[Card.Jack] = 5
            } else {
                val maxEntry = returnValue.maxBy { (_, value) -> value }
                returnValue[maxEntry.key] = maxEntry.value + jackCount
            }
            return returnValue
        }

        object CardComparator : Comparator<Card> {
            override fun compare(left: Card, right: Card): Int {
                return ordinal(left).compareTo(ordinal(right))
            }

            fun ordinal(card: Card): Int = if (card == Card.Jack) -1 else card.ordinal

        }
    }

    enum class Card(val char: Char) {
        Two('2'),
        Three('3'),
        Four('4'),
        Five('5'),
        Six('6'),
        Seven('7'),
        Eight('8'),
        Nine('9'),
        Ten('T'),
        Jack('J'),
        Queen('Q'),
        King('K'),
        Ace('A');
    }

    enum class HandType(val check: (Map<Card, Int>) -> Boolean) {
        FiveOfKind({ hand -> hand.size == 1 }),
        FourOfKind({ hand -> hand.any { it.value == 4 } }),
        FullHouse({ hand -> hand.any { it.value == 2 } && hand.any { it.value == 3 } }),
        ThreeOfKind({ hand -> hand.any { it.value == 3 } }),
        TwoPair({ hand -> hand.count { it.value == 2 } == 2 }),
        OnePair({ hand -> hand.any { it.value == 2 } }),
        HighCard({ _ -> true }),
    }

    override fun testData(): Day.TestData {
        return Day.TestData(
            6440,
            5905,
            """32T3K 765
T55J5 684
KK677 28
KTJJT 220
QQQJA 483"""
                .lines()
        )
    }
}
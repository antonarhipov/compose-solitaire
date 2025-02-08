package org.arhan.solitaire.model

data class Card(
    val suit: Suit,
    val rank: Rank,
    val faceUp: Boolean
) {
    enum class Suit {
        HEARTS, DIAMONDS, CLUBS, SPADES;

        val isRed: Boolean
            get() = this == HEARTS || this == DIAMONDS
    }

    enum class Rank(val value: Int) {
        ACE(1),
        TWO(2),
        THREE(3),
        FOUR(4),
        FIVE(5),
        SIX(6),
        SEVEN(7),
        EIGHT(8),
        NINE(9),
        TEN(10),
        JACK(11),
        QUEEN(12),
        KING(13)
    }

    val isRed: Boolean
        get() = suit.isRed
}

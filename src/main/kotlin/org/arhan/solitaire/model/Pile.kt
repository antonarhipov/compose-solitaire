package org.arhan.solitaire.model

data class Pile(
    val type: Type,
    val cards: List<Card> = emptyList()
) {
    enum class Type {
        TABLEAU, FOUNDATION, STOCK, WASTE
    }

    val topCard: Card?
        get() = cards.lastOrNull()

    fun addCard(card: Card): Pile = copy(cards = cards + card)

    fun addCards(newCards: List<Card>): Pile = copy(cards = cards + newCards)

    fun removeTopCard(): Pair<Card, Pile> {
        require(cards.isNotEmpty()) { "Cannot remove card from empty pile" }
        return cards.last() to copy(cards = cards.dropLast(1))
    }

    fun removeCards(count: Int): Pair<List<Card>, Pile> {
        require(count <= cards.size) { "Cannot remove more cards than available" }
        return cards.takeLast(count) to copy(cards = cards.dropLast(count))
    }

    val isEmpty: Boolean get() = cards.isEmpty()

    val size: Int get() = cards.size

    companion object {
        fun isValidTableauSequence(cards: List<Card>): Boolean {
            if (cards.size <= 1) return true
            val result = cards.zipWithNext().all { (current, next) ->
                val valid = current.canBePlacedOnInTableau(next)
                System.err.println("[DEBUG_LOG] Checking ${current.rank}${current.suit} on ${next.rank}${next.suit}: $valid")
                valid
            }
            return result
        }
    }
}

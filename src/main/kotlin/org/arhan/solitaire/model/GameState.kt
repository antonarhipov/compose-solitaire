package org.arhan.solitaire.model

data class GameState(
    val tableau: List<Pile>,
    val foundation: List<Pile>,
    val stock: Pile,
    val waste: Pile
) {
    // Properties for accessing updated stock and waste piles
    val newStock: Pile get() = stock
    val newWaste: Pile get() = waste
    init {
        require(tableau.all { it.type == Pile.Type.TABLEAU }) { "All tableau piles must be of TABLEAU type" }
        require(foundation.all { it.type == Pile.Type.FOUNDATION }) { "All foundation piles must be of FOUNDATION type" }
        require(stock.type == Pile.Type.STOCK) { "Stock pile must be of STOCK type" }
        require(waste.type == Pile.Type.WASTE) { "Waste pile must be of WASTE type" }
    }

    fun isGameWon(): Boolean {
        // Game is won when all foundation piles have 13 cards (Ace to King)
        // Game is won when all 4 foundation piles have 13 cards (Ace to King)
        return foundation.size == 4 && foundation.all { it.size == 13 }
    }

    fun findPileWithCard(card: Card): Pile? {
        return (tableau + foundation + listOf(waste))
            .find { pile -> pile.cards.contains(card) }
    }

    fun updatePile(oldPile: Pile, newPile: Pile): GameState {
        return when (oldPile.type) {
            Pile.Type.TABLEAU -> copy(
                tableau = tableau.map { if (it == oldPile) newPile else it }
            )
            Pile.Type.FOUNDATION -> copy(
                foundation = foundation.map { if (it == oldPile) newPile else it }
            )
            Pile.Type.STOCK -> copy(stock = newPile)
            Pile.Type.WASTE -> copy(waste = newPile)
        }
    }

    companion object {
        fun createInitialState(): GameState {
            val deck = createShuffledDeck()

            // Create tableau piles
            val tableau = List(7) { index ->
                val cards = deck.subList(index * (index + 1) / 2, index * (index + 1) / 2 + index + 1)
                    .mapIndexed { cardIndex, card ->
                        // Only the top card is face up
                        if (cardIndex == index) card.copy(faceUp = true) else card
                    }
                Pile(Pile.Type.TABLEAU, cards)
            }

            // Remaining cards go to stock
            val stockCards = deck.drop(28)
            val stock = Pile(Pile.Type.STOCK, stockCards)

            return GameState(
                tableau = tableau,
                foundation = List(4) { Pile(Pile.Type.FOUNDATION) },
                stock = stock,
                waste = Pile(Pile.Type.WASTE)
            )
        }

        private fun createShuffledDeck(): List<Card> {
            return Card.Suit.values().flatMap { suit ->
                Card.Rank.values().map { rank ->
                    Card(suit, rank, faceUp = false)
                }
            }.shuffled()
        }
    }
}

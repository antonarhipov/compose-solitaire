package org.arhan.solitaire.model

data class Pile(
    val type: Type,
    val cards: List<Card> = emptyList()
) {
    enum class Type {
        TABLEAU,
        FOUNDATION,
        STOCK,
        WASTE
    }

    val isEmpty: Boolean
        get() = cards.isEmpty()

    val size: Int
        get() = cards.size

    val topCard: Card?
        get() = cards.lastOrNull()

    fun addCard(card: Card): Pile = copy(cards = cards + card)
    
    fun removeCard(card: Card): Pile = copy(cards = cards - card)
    
    fun addCards(newCards: List<Card>): Pile = copy(cards = cards + newCards)
    
    fun removeCards(cardsToRemove: List<Card>): Pile = copy(cards = cards.filter { it !in cardsToRemove })
}

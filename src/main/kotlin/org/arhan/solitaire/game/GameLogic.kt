package org.arhan.solitaire.game

import org.arhan.solitaire.model.Card
import org.arhan.solitaire.model.GameState
import org.arhan.solitaire.model.Pile

object GameLogic {
    fun cycleWasteToStock(gameState: GameState): GameState {
        // Move all cards from waste back to stock
        val wasteCards = gameState.waste.cards.reversed()
        return gameState.copy(
            waste = Pile(Pile.Type.WASTE),
            stock = Pile(Pile.Type.STOCK, wasteCards)
        )
    }

    fun isGameWon(gameState: GameState): Boolean = gameState.isGameWon()
    fun findValidMove(gameState: GameState, card: Card): Triple<Pile, Pile, Card>? {
        val sourcePile = gameState.findPileWithCard(card) ?: return null

        // Only allow moving the top card from waste pile
        if (sourcePile.type == Pile.Type.WASTE) {
            if (card != sourcePile.topCard) return null
            // For waste pile, try both foundation and tableau moves
            findFoundationMove(gameState, card, sourcePile)?.let { return it }
            findTableauMove(gameState, card, sourcePile)?.let { return it }
            return null
        }

        // First try foundation moves (they have priority)
        findFoundationMove(gameState, card, sourcePile)?.let { return it }

        // Then try tableau moves
        findTableauMove(gameState, card, sourcePile)?.let { return it }

        return null
    }

    private fun findFoundationMove(
        gameState: GameState,
        card: Card,
        sourcePile: Pile
    ): Triple<Pile, Pile, Card>? {
        // Can only move single cards to foundation
        if (!card.faceUp || sourcePile.cards.last() != card) return null

        val targetPile = if (card.rank == Card.Rank.ACE) {
            // For Aces, find empty foundation pile
            gameState.foundation.find { it.isEmpty }
        } else {
            // For other cards, find foundation pile with matching suit and previous rank
            gameState.foundation.find { pile ->
                pile.topCard?.let { topCard ->
                    topCard.suit == card.suit && topCard.rank.isPreviousInSequence(card.rank)
                } ?: false
            }
        }

        return targetPile?.let { Triple(sourcePile, it, card) }
    }

    private fun findTableauMove(
        gameState: GameState,
        card: Card,
        sourcePile: Pile
    ): Triple<Pile, Pile, Card>? {
        if (!card.faceUp) return null

        // Get cards being moved (the card and all cards on top of it)
        val cardIndex = sourcePile.cards.indexOf(card)
        if (cardIndex == -1) return null

        // Check if moving this card would reveal a face-down card
        val wouldRevealFaceDownCard = cardIndex > 0 && !sourcePile.cards[cardIndex - 1].faceUp

        val cardsToMove = sourcePile.cards.subList(cardIndex, sourcePile.cards.size)

        // Find potential target piles
        val potentialTargets = gameState.tableau.filter { pile ->
            pile != sourcePile && (
                // For empty tableau, only Kings can be placed
                (pile.isEmpty && card.rank == Card.Rank.KING) ||
                // For non-empty tableau, check if the first card can be placed
                (!pile.isEmpty && canPlaceOnTableau(cardsToMove.first(), pile))
            )
        }.filter { pile ->
            // For non-empty piles, validate the sequence
            pile.isEmpty || isValidSequence(cardsToMove)
        }

        // If this move would reveal a face-down card, prioritize it
        if (wouldRevealFaceDownCard) {
            potentialTargets.firstOrNull()?.let { return Triple(sourcePile, it, card) }
        }

        // Otherwise, return the first valid target
        return potentialTargets.firstOrNull()?.let { Triple(sourcePile, it, card) }
    }

    fun isValidSequence(cards: List<Card>): Boolean {
        System.err.println("[DEBUG_LOG] Validating sequence: ${cards.joinToString { "${it.rank}${it.suit}" }}")
        if (!cards.all { it.faceUp }) {
            System.err.println("[DEBUG_LOG] Sequence invalid: contains face-down cards")
            return false
        }
        val result = Pile.isValidTableauSequence(cards)
        System.err.println("[DEBUG_LOG] Sequence validation result: $result")
        return result
    }

    private fun canPlaceOnTableau(card: Card, targetPile: Pile): Boolean {
        // For empty tableau, only Kings can be placed
        if (targetPile.isEmpty) {
            return card.rank == Card.Rank.KING
        }

        // For non-empty tableau, check if the card can be placed on the top card
        return targetPile.topCard?.let { topCard ->
            // Colors must alternate and moving card must be one less
            card.isRed != topCard.isRed && topCard.rank.isNextInSequence(card.rank)
        } ?: false
    }
}

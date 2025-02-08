package org.arhan.solitaire.game

import org.arhan.solitaire.model.Card
import org.arhan.solitaire.model.GameState
import org.arhan.solitaire.model.Pile

object GameLogic {
    fun findValidMove(gameState: GameState, card: Card): Triple<Pile, Pile, Card>? {
        if (!card.faceUp) return null

        // Check if the card is part of a sequence
        val sourcePile = findSourcePile(gameState, card) ?: return null
        val cardIndex = sourcePile.cards.indexOf(card)
        val isPartOfSequence = cardIndex < sourcePile.cards.size - 1

        // For foundation moves, we only allow single cards (no sequences)
        if (!isPartOfSequence) {
            findFoundationMove(gameState, card)?.let { return it }
        }

        // Then try tableau moves
        return findTableauMove(gameState, card)
    }

    private fun findFoundationMove(gameState: GameState, card: Card): Triple<Pile, Pile, Card>? {
        val sourcePile = findSourcePile(gameState, card) ?: return null

        // Find matching foundation pile or empty one for Ace
        val targetPile = gameState.foundation.find { foundationPile ->
            when {
                foundationPile.isEmpty -> card.rank == Card.Rank.ACE
                foundationPile.topCard != null -> {
                    foundationPile.topCard?.let { topCard ->
                        topCard.suit == card.suit && topCard.rank.value == card.rank.value - 1
                    } ?: false
                }
                else -> false
            }
        } ?: return null

        // Don't allow moving sequences to foundation
        val cardIndex = sourcePile.cards.indexOf(card)
        if (cardIndex < sourcePile.cards.size - 1) return null

        return Triple(sourcePile, targetPile, card)
    }

    private fun findTableauMove(gameState: GameState, card: Card): Triple<Pile, Pile, Card>? {
        val sourcePile = findSourcePile(gameState, card) ?: return null
        
        // Check if the card is part of a valid sequence
        val cardIndex = sourcePile.cards.indexOf(card)
        if (cardIndex < sourcePile.cards.size - 1) {
            val sequence = sourcePile.cards.subList(cardIndex, sourcePile.cards.size)
            if (!isValidSequence(sequence)) return null
        }

        // Find valid tableau pile to move to
        val targetPile = gameState.tableau.find { tableauPile ->
            if (tableauPile == sourcePile) return@find false

            when {
                tableauPile.isEmpty -> card.rank == Card.Rank.KING
                tableauPile.topCard != null -> {
                    tableauPile.topCard?.let { topCard ->
                        topCard.isRed != card.isRed && topCard.rank.value == card.rank.value + 1
                    } ?: false
                }
                else -> false
            }
        } ?: return null

        return Triple(sourcePile, targetPile, card)
    }

    private fun findSourcePile(gameState: GameState, card: Card): Pile? {
        return gameState.tableau.find { it.cards.contains(card) }
            ?: if (gameState.waste.cards.contains(card)) gameState.waste else null
    }

    fun isValidSequence(cards: List<Card>): Boolean {
        if (cards.isEmpty()) return true
        
        return cards.zipWithNext().all { (current, next) ->
            current.isRed != next.isRed && current.rank.value == next.rank.value + 1
        }
    }

    fun cycleWasteToStock(gameState: GameState): CycleResult {
        val wasteCards = gameState.waste.cards.reversed()
        val newStock = gameState.stock.addCards(wasteCards)
        val newWaste = Pile(Pile.Type.WASTE)
        
        return CycleResult(newStock, newWaste)
    }

    fun isGameWon(gameState: GameState): Boolean {
        // Game is won when all foundation piles are complete (have 13 cards from Ace to King)
        return gameState.foundation.size == 4 && 
               gameState.foundation.all { pile -> 
                   pile.cards.size == 13 && 
                   pile.cards.first().rank == Card.Rank.ACE &&
                   pile.cards.last().rank == Card.Rank.KING &&
                   pile.cards.zipWithNext().all { (current, next) ->
                       current.suit == next.suit && current.rank.value == next.rank.value - 1
                   }
               }
    }

    data class CycleResult(
        val newStock: Pile,
        val newWaste: Pile
    )
}

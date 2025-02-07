package org.arhan.solitaire.ui

import androidx.compose.runtime.*
import kotlinx.coroutines.*
import org.arhan.solitaire.game.GameLogic
import org.arhan.solitaire.model.Card
import org.arhan.solitaire.model.GameState
import kotlin.time.Duration.Companion.seconds

class GameViewModel {
    private var timerJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    var uiState by mutableStateOf(GameUiState(GameState.createInitialState()))
        private set

    var cardAnimationState by mutableStateOf<CardAnimationState?>(null)
        private set

    init {
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = coroutineScope.launch {
            while (isActive) {
                if (uiState.isTimerRunning) {
                    delay(1000)
                    uiState = uiState.updateTimer(uiState.elapsedTime + 1.seconds)
                } else {
                    delay(100)
                }
            }
        }
    }

    fun onCardDoubleClick(card: Card) {
        val currentState = uiState.gameState
        val sourcePile = currentState.findPileWithCard(card) ?: return

        // Try foundation move first
        for (foundation in currentState.foundation) {
            GameLogic.findValidMove(currentState, card)?.let { (from, to, moveCard) ->
                if (to.type == org.arhan.solitaire.model.Pile.Type.FOUNDATION) {
                    makeMove(from, to, moveCard)
                    return
                }
            }
        }

        // Then try tableau moves
        GameLogic.findValidMove(currentState, card)?.let { (from, to, moveCard) ->
            makeMove(from, to, moveCard)
        }
    }

    private fun makeMove(from: org.arhan.solitaire.model.Pile, to: org.arhan.solitaire.model.Pile, card: Card) {
        val oldState = uiState.gameState

        // Get all cards to move (the card and any cards on top of it)
        val cardIndex = from.cards.indexOf(card)
        if (cardIndex == -1) return
        val cardsToMove = from.cards.subList(cardIndex, from.cards.size)

        // Remove cards from source pile
        val (_, newFromPile) = from.removeCards(cardsToMove.size)

        // Add cards to target pile
        val newToPile = to.addCards(cardsToMove)

        // If source is tableau pile and has a face-down top card after move, flip it
        var finalFromPile = newFromPile
        if (from.type == org.arhan.solitaire.model.Pile.Type.TABLEAU) {
            newFromPile.topCard?.let { topCard ->
                if (!topCard.faceUp) {
                    finalFromPile = newFromPile.removeTopCard().second
                        .addCard(topCard.copy(faceUp = true))
                }
            }
        }

        // Update game state
        val newState = oldState
            .updatePile(from, finalFromPile)
            .updatePile(to, newToPile)

        uiState = uiState
            .addToUndoStack(oldState)
            .copy(gameState = newState)
            .incrementMoves()
    }

    fun undo() {
        uiState.undo()?.let { newState ->
            uiState = newState
        }
    }

    fun onStockClick(stockPosition: androidx.compose.ui.geometry.Offset, wastePosition: androidx.compose.ui.geometry.Offset) {
        if (cardAnimationState?.isAnimating == true) return

        val oldState = uiState.gameState
        val stock = oldState.stock
        val waste = oldState.waste

        if (stock.isEmpty && !waste.isEmpty) {
            // If stock is empty and waste has cards, animate recycling
            val topCard = waste.topCard!!
            cardAnimationState = CardAnimationState().apply {
                startAnimation(topCard.copy(faceUp = false), stockPosition)
            }
            coroutineScope.launch {
                delay(750) // Wait for flip and movement animations
                uiState = uiState
                    .addToUndoStack(oldState)
                    .copy(gameState = GameLogic.cycleWasteToStock(oldState))
                    .incrementMoves()
                cardAnimationState = null
            }
            return
        }

        // Draw one card from stock to waste
        drawFromStock(stockPosition, wastePosition)
    }

    private fun drawFromStock(stockPosition: androidx.compose.ui.geometry.Offset, wastePosition: androidx.compose.ui.geometry.Offset) {
        val oldState = uiState.gameState
        val stock = oldState.stock
        val waste = oldState.waste

        // Draw top card from stock and animate it
        val (card, newStock) = stock.removeTopCard()

        // Start with the card face down, it will flip during animation
        cardAnimationState = CardAnimationState().apply {
            startAnimation(card.copy(faceUp = false), wastePosition)
        }

        // Update the game state immediately to remove the card from stock
        uiState = uiState
            .addToUndoStack(oldState)
            .copy(gameState = oldState.updatePile(stock, newStock))

        coroutineScope.launch {
            delay(750) // Wait for flip and movement animations
            // Add the card face up to waste pile
            val newWaste = waste.addCard(card.copy(faceUp = true))
            uiState = uiState.copy(
                gameState = uiState.gameState.updatePile(waste, newWaste)
            ).incrementMoves()
            cardAnimationState = null
        }
    }

    fun newGame() {
        uiState = GameUiState(GameState.createInitialState())
    }

    fun onDestroy() {
        timerJob?.cancel()
        coroutineScope.cancel()
    }
}

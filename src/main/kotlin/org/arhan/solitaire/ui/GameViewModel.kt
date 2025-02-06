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

        // Update game state
        val newState = oldState
            .updatePile(from, newFromPile)
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

    fun onStockClick() {
        val oldState = uiState.gameState
        val stock = oldState.stock
        val waste = oldState.waste

        if (stock.isEmpty) {
            // If stock is empty, move all waste cards back to stock
            uiState = uiState
                .addToUndoStack(oldState)
                .copy(gameState = GameLogic.cycleWasteToStock(oldState))
                .incrementMoves()
            return
        }

        // Draw top card from stock to waste
        val (card, newStock) = stock.removeTopCard()
        val newWaste = waste.addCard(card.copy(faceUp = true))

        val newState = oldState
            .updatePile(stock, newStock)
            .updatePile(waste, newWaste)

        uiState = uiState
            .addToUndoStack(oldState)
            .copy(gameState = newState)
            .incrementMoves()
    }

    fun newGame() {
        uiState = GameUiState(GameState.createInitialState())
    }

    fun onDestroy() {
        timerJob?.cancel()
        coroutineScope.cancel()
    }
}

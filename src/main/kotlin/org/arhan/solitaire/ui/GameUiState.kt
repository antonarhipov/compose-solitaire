package org.arhan.solitaire.ui

import org.arhan.solitaire.model.GameState
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class GameUiState(
    val gameState: GameState,
    val moveCount: Int = 0,
    val elapsedTime: Duration = 0.seconds,
    val isTimerRunning: Boolean = false,
    val undoStack: List<GameState> = emptyList()
) {
    val canUndo: Boolean
        get() = undoStack.isNotEmpty()

    fun addToUndoStack(state: GameState): GameUiState =
        copy(undoStack = undoStack + state)

    fun undo(): GameUiState? {
        if (!canUndo) return null
        return copy(
            gameState = undoStack.last(),
            undoStack = undoStack.dropLast(1),
            moveCount = moveCount - 1
        )
    }

    fun incrementMoves(): GameUiState =
        copy(
            moveCount = moveCount + 1,
            isTimerRunning = true
        )

    fun updateTimer(newTime: Duration): GameUiState =
        copy(elapsedTime = newTime)

    fun resetGame(newGameState: GameState): GameUiState =
        GameUiState(gameState = newGameState)
}
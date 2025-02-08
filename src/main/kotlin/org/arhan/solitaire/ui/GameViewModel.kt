package org.arhan.solitaire.ui

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.arhan.solitaire.game.GameLogic
import org.arhan.solitaire.model.Card
import org.arhan.solitaire.model.GameState
import org.arhan.solitaire.model.Pile
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class GameViewModel {
    private val _gameState = MutableStateFlow<GameState?>(null)
    val gameState: StateFlow<GameState?> = _gameState.asStateFlow()

    private val _selectedCard = MutableStateFlow<Card?>(null)
    val selectedCard: StateFlow<Card?> = _selectedCard.asStateFlow()

    private val _moveCount = MutableStateFlow(0)
    val moveCount: StateFlow<Int> = _moveCount.asStateFlow()

    private val _gameTime = MutableStateFlow(0.seconds)
    val gameTime: StateFlow<Duration> = _gameTime.asStateFlow()

    private val _isGameStarted = MutableStateFlow(false)
    val isGameStarted: StateFlow<Boolean> = _isGameStarted.asStateFlow()

    fun startNewGame() {
        _gameState.value = createInitialGameState()
        _moveCount.value = 0
        _gameTime.value = 0.seconds
        _isGameStarted.value = false
        _selectedCard.value = null
    }

    fun onCardClick(card: Card) {
        if (!_isGameStarted.value) {
            _isGameStarted.value = true
        }

        val currentState = _gameState.value ?: return

        // Handle stock pile card clicks
        if (currentState.stock.cards.contains(card)) {
            if (currentState.stock.isEmpty) {
                val cycleResult = GameLogic.cycleWasteToStock(currentState)
                _gameState.value = currentState.copy(
                    stock = cycleResult.newStock,
                    waste = cycleResult.newWaste
                )
            } else {
                // Get the top card from stock
                val drawnCard = currentState.stock.cards.last()
                
                // Remove it from stock and add it to waste pile with faceUp = true
                _gameState.value = currentState.copy(
                    stock = currentState.stock.removeCard(drawnCard),
                    waste = currentState.waste.addCard(drawnCard.copy(faceUp = true))
                )
            }
            _moveCount.value += 1
            return
        }

        val currentSelected = _selectedCard.value

        if (currentSelected == null) {
            if (card.faceUp) {
                _selectedCard.value = card
            }
        } else {
            if (currentSelected == card) {
                _selectedCard.value = null
            } else {
                tryMove(currentState, currentSelected, card)
            }
        }
    }

    fun onCardDoubleClick(card: Card) {
        if (!_isGameStarted.value) {
            _isGameStarted.value = true
        }

        val currentState = _gameState.value ?: return

        // Handle stock pile card double-clicks the same as single clicks
        if (currentState.stock.cards.contains(card)) {
            onCardClick(card)
            return
        }

        if (!card.faceUp) return
        
        // First try foundation move
        findBestMove(currentState, card)?.let { move ->
            executeMove(move)
            return
        }
    }

    private fun findBestMove(gameState: GameState, card: Card): Triple<Pile, Pile, Card>? {
        // First try foundation moves (higher priority)
        GameLogic.findValidMove(gameState, card)?.let { move ->
            if (move.second.type == Pile.Type.FOUNDATION) {
                return move
            }
        }

        // Then try tableau moves
        return GameLogic.findValidMove(gameState, card)?.let { move ->
            if (move.second.type == Pile.Type.TABLEAU) {
                move
            } else {
                null
            }
        }
    }

    fun onPileClick(pile: Pile) {
        if (!_isGameStarted.value) {
            _isGameStarted.value = true
        }

        val currentState = _gameState.value ?: return
        val currentSelected = _selectedCard.value

        when {
            currentSelected != null -> {
                val validMove = GameLogic.findValidMove(currentState, currentSelected)
                if (validMove != null && validMove.second == pile) {
                    executeMove(validMove)
                }
                _selectedCard.value = null
            }
            pile.type == Pile.Type.STOCK -> {
                if (pile.isEmpty) {
                    val cycleResult = GameLogic.cycleWasteToStock(currentState)
                    _gameState.value = currentState.copy(
                        stock = cycleResult.newStock,
                        waste = cycleResult.newWaste
                    )
                } else {
                    // Get the top card from stock
                    val drawnCard = currentState.stock.cards.last()
                    
                    // Remove it from stock and add it to waste pile with faceUp = true
                    _gameState.value = currentState.copy(
                        stock = currentState.stock.removeCard(drawnCard),
                        waste = currentState.waste.addCard(drawnCard.copy(faceUp = true))
                    )
                }
                _moveCount.value += 1
            }
            pile.type == Pile.Type.TABLEAU && pile.isEmpty && currentSelected == null -> {
                // Handle empty tableau click when no card is selected
                // This is where you might want to handle dragging a King to an empty spot
            }
        }
    }

    private fun tryMove(currentState: GameState, selectedCard: Card, targetCard: Card) {
        val validMove = GameLogic.findValidMove(currentState, selectedCard)
        if (validMove != null && validMove.second.cards.contains(targetCard)) {
            executeMove(validMove)
        }
        _selectedCard.value = null
    }

    private fun executeMove(move: Triple<Pile, Pile, Card>) {
        val currentState = _gameState.value ?: return
        val (sourcePile, targetPile, card) = move

        val sourceIndex = when (sourcePile.type) {
            Pile.Type.TABLEAU -> currentState.tableau.indexOf(sourcePile)
            Pile.Type.WASTE -> -1
            else -> return
        }

        val targetIndex = when (targetPile.type) {
            Pile.Type.TABLEAU -> currentState.tableau.indexOf(targetPile)
            Pile.Type.FOUNDATION -> currentState.foundation.indexOf(targetPile)
            else -> return
        }

        val cardIndex = sourcePile.cards.indexOf(card)
        val cardsToMove = sourcePile.cards.subList(cardIndex, sourcePile.cards.size)

        val newSourcePile = sourcePile.removeCards(cardsToMove)
            .let { pile ->
                if (pile.cards.isNotEmpty() && !pile.cards.last().faceUp) {
                    pile.copy(cards = pile.cards.dropLast(1) + pile.cards.last().copy(faceUp = true))
                } else {
                    pile
                }
            }

        val newTargetPile = targetPile.addCards(cardsToMove)

        _gameState.value = currentState.copy(
            tableau = currentState.tableau.mapIndexed { index, pile ->
                when (index) {
                    sourceIndex -> newSourcePile
                    targetIndex -> if (targetPile.type == Pile.Type.TABLEAU) newTargetPile else pile
                    else -> pile
                }
            },
            foundation = currentState.foundation.mapIndexed { index, pile ->
                if (targetPile.type == Pile.Type.FOUNDATION && index == targetIndex) newTargetPile else pile
            },
            waste = if (sourcePile.type == Pile.Type.WASTE) newSourcePile else currentState.waste
        )

        _moveCount.value += 1
    }

    private fun createInitialGameState(): GameState {
        val deck = createShuffledDeck()
        val tableau = createTableau(deck.take(28))
        val stock = Pile(Pile.Type.STOCK, deck.drop(28).map { it.copy(faceUp = false) })
        val foundation = List(4) { Pile(Pile.Type.FOUNDATION) }
        val waste = Pile(Pile.Type.WASTE)

        return GameState(tableau, foundation, stock, waste)
    }

    private fun createShuffledDeck(): List<Card> {
        return Card.Suit.values().flatMap { suit ->
            Card.Rank.values().map { rank ->
                Card(suit, rank, false)
            }
        }.shuffled()
    }

    private fun createTableau(cards: List<Card>): List<Pile> {
        var remainingCards = cards
        return List(7) { column ->
            val pileCards = remainingCards.take(column + 1).mapIndexed { index, card ->
                if (index == column) card.copy(faceUp = true) else card
            }
            remainingCards = remainingCards.drop(column + 1)
            Pile(Pile.Type.TABLEAU, pileCards)
        }
    }

    fun updateGameTime(duration: Duration) {
        if (_isGameStarted.value) {
            _gameTime.value = duration
        }
    }
}

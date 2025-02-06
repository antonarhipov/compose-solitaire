package org.arhan.solitaire.game

import org.arhan.solitaire.model.Card
import org.arhan.solitaire.model.GameState
import org.arhan.solitaire.model.Pile
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class GameLogicTest {
    @Test
    fun `test tableau moves - descending order and alternating colors`() {
        // Test all possible color combinations
        val blackEight = Card(Card.Suit.SPADES, Card.Rank.EIGHT, true)
        val redSeven = Card(Card.Suit.HEARTS, Card.Rank.SEVEN, true)
        val blackSix = Card(Card.Suit.CLUBS, Card.Rank.SIX, true)
        val redFive = Card(Card.Suit.DIAMONDS, Card.Rank.FIVE, true)

        // Test valid move: red seven on black eight
        run {
            val sourcePile = Pile(Pile.Type.TABLEAU, listOf(redSeven))
            val targetPile = Pile(Pile.Type.TABLEAU, listOf(blackEight))

            val gameState = GameState(
                tableau = listOf(sourcePile, targetPile),
                foundation = listOf(),
                stock = Pile(Pile.Type.STOCK),
                waste = Pile(Pile.Type.WASTE)
            )

            val moveRedSeven = GameLogic.findValidMove(gameState, redSeven)
            assertNotNull(moveRedSeven, "Should find valid move for red seven on black eight")
            moveRedSeven?.let { (from, to, card) ->
                assertEquals(sourcePile, from)
                assertEquals(targetPile, to)
                assertEquals(redSeven, card)
            }
        }

        // Test invalid move: black six on black eight (same color)
        run {
            val sourcePile = Pile(Pile.Type.TABLEAU, listOf(blackSix))
            val targetPile = Pile(Pile.Type.TABLEAU, listOf(blackEight))

            val gameState = GameState(
                tableau = listOf(sourcePile, targetPile),
                foundation = listOf(),
                stock = Pile(Pile.Type.STOCK),
                waste = Pile(Pile.Type.WASTE)
            )

            val moveBlackSix = GameLogic.findValidMove(gameState, blackSix)
            assertNull(moveBlackSix, "Should not allow black six on black eight (same color)")
        }

        // Test invalid move: red five on black eight (non-sequential)
        run {
            val sourcePile = Pile(Pile.Type.TABLEAU, listOf(redFive))
            val targetPile = Pile(Pile.Type.TABLEAU, listOf(blackEight))

            val gameState = GameState(
                tableau = listOf(sourcePile, targetPile),
                foundation = listOf(),
                stock = Pile(Pile.Type.STOCK),
                waste = Pile(Pile.Type.WASTE)
            )

            val moveRedFive = GameLogic.findValidMove(gameState, redFive)
            assertNull(moveRedFive, "Should not allow red five on black eight (non-sequential)")
        }
    }

    @Test
    fun `test foundation moves - same suit ascending order`() {
        val aceHearts = Card(Card.Suit.HEARTS, Card.Rank.ACE, true)
        val twoHearts = Card(Card.Suit.HEARTS, Card.Rank.TWO, true)
        val twoDiamonds = Card(Card.Suit.DIAMONDS, Card.Rank.TWO, true)

        // Test Ace to empty foundation
        val emptyFoundation = Pile(Pile.Type.FOUNDATION)
        val sourcePile1 = Pile(Pile.Type.TABLEAU, listOf(aceHearts))

        val gameState1 = GameState(
            tableau = listOf(sourcePile1),
            foundation = listOf(emptyFoundation),
            stock = Pile(Pile.Type.STOCK),
            waste = Pile(Pile.Type.WASTE)
        )

        val moveAce = GameLogic.findValidMove(gameState1, aceHearts)
        assertNotNull(moveAce, "Should allow Ace to empty foundation")
        moveAce?.let { (from, to, card) ->
            assertEquals(sourcePile1, from)
            assertEquals(emptyFoundation, to)
            assertEquals(aceHearts, card)
        }

        // Test building on foundation
        val foundationWithAce = Pile(Pile.Type.FOUNDATION, listOf(aceHearts))
        val sourcePile2 = Pile(Pile.Type.TABLEAU, listOf(twoDiamonds, twoHearts))

        val gameState2 = GameState(
            tableau = listOf(sourcePile2),
            foundation = listOf(foundationWithAce),
            stock = Pile(Pile.Type.STOCK),
            waste = Pile(Pile.Type.WASTE)
        )

        val moveTwoHearts = GameLogic.findValidMove(gameState2, twoHearts)
        assertNotNull(moveTwoHearts, "Should allow two of hearts on ace of hearts")

        val moveTwoDiamonds = GameLogic.findValidMove(gameState2, twoDiamonds)
        assertNull(moveTwoDiamonds, "Should not allow two of diamonds on ace of hearts")
    }

    @Test
    fun `test empty tableau column - kings only`() {
        val redKing = Card(Card.Suit.HEARTS, Card.Rank.KING, true)
        val blackQueen = Card(Card.Suit.SPADES, Card.Rank.QUEEN, true)

        val sourcePile = Pile(Pile.Type.TABLEAU, listOf(redKing, blackQueen))
        val emptyTableau = Pile(Pile.Type.TABLEAU)

        val gameState = GameState(
            tableau = listOf(sourcePile, emptyTableau),
            foundation = listOf(),
            stock = Pile(Pile.Type.STOCK),
            waste = Pile(Pile.Type.WASTE)
        )

        val moveKing = GameLogic.findValidMove(gameState, redKing)
        assertNotNull(moveKing, "Should allow King to empty tableau")
        moveKing?.let { (from, to, card) ->
            assertEquals(sourcePile, from)
            assertEquals(emptyTableau, to)
            assertEquals(redKing, card)
        }

        val moveQueen = GameLogic.findValidMove(gameState, blackQueen)
        assertNull(moveQueen, "Should not allow Queen to empty tableau")
    }

    @Test
    fun `test waste pile moves`() {
        val aceHearts = Card(Card.Suit.HEARTS, Card.Rank.ACE, true)
        val blackKing = Card(Card.Suit.SPADES, Card.Rank.KING, true)
        val redQueen = Card(Card.Suit.HEARTS, Card.Rank.QUEEN, true)

        // Test waste to foundation
        val wastePile1 = Pile(Pile.Type.WASTE, listOf(aceHearts))
        val emptyFoundation = Pile(Pile.Type.FOUNDATION)

        val gameState1 = GameState(
            tableau = listOf(),
            foundation = listOf(emptyFoundation),
            stock = Pile(Pile.Type.STOCK),
            waste = wastePile1
        )

        val moveAce = GameLogic.findValidMove(gameState1, aceHearts)
        assertNotNull(moveAce, "Should allow Ace from waste to foundation")

        // Test waste to tableau
        val wastePile2 = Pile(Pile.Type.WASTE, listOf(redQueen))
        val tableauPile = Pile(Pile.Type.TABLEAU, listOf(blackKing))

        val gameState2 = GameState(
            tableau = listOf(tableauPile),
            foundation = listOf(),
            stock = Pile(Pile.Type.STOCK),
            waste = wastePile2
        )

        val moveQueen = GameLogic.findValidMove(gameState2, redQueen)
        assertNotNull(moveQueen, "Should allow Queen from waste to tableau")
    }
    @Test
    fun testFoundationMove() {
        // Create test cards
        val aceHearts = Card(Card.Suit.HEARTS, Card.Rank.ACE, true)
        val twoHearts = Card(Card.Suit.HEARTS, Card.Rank.TWO, true)

        // Create test piles
        val sourcePile = Pile(Pile.Type.TABLEAU, listOf(twoHearts))
        val foundationPile = Pile(Pile.Type.FOUNDATION, listOf(aceHearts))

        // Create game state
        val gameState = GameState(
            tableau = listOf(sourcePile),
            foundation = listOf(foundationPile),
            stock = Pile(Pile.Type.STOCK),
            waste = Pile(Pile.Type.WASTE)
        )

        val move = GameLogic.findValidMove(gameState, twoHearts)
        assertNotNull(move, "Should find a valid foundation move for 2♥")
        move?.let { (from, to, card) ->
            assertEquals(sourcePile, from, "Source pile should be the tableau pile")
            assertEquals(foundationPile, to, "Target pile should be the foundation pile")
            assertEquals(twoHearts, card, "Moving card should be 2♥")
        }
    }

    @Test
    fun testTableauMove() {
        // Test Queen of Hearts (red) to King of Spades (black)
        val blackKing = Card(Card.Suit.SPADES, Card.Rank.KING, true)
        val redQueenHearts = Card(Card.Suit.HEARTS, Card.Rank.QUEEN, true)

        // Create test piles
        val sourcePile = Pile(Pile.Type.TABLEAU, listOf(redQueenHearts))
        val targetPile = Pile(Pile.Type.TABLEAU, listOf(blackKing))

        // Create game state
        val gameState = GameState(
            tableau = listOf(sourcePile, targetPile),
            foundation = listOf(),
            stock = Pile(Pile.Type.STOCK),
            waste = Pile(Pile.Type.WASTE)
        )

        val moveHearts = GameLogic.findValidMove(gameState, redQueenHearts)
        assertNotNull(moveHearts, "Should find a valid tableau move for Q♥")
        moveHearts?.let { (from, to, card) ->
            assertEquals(sourcePile, from, "Source pile should be the source tableau pile")
            assertEquals(targetPile, to, "Target pile should be the target tableau pile")
            assertEquals(redQueenHearts, card, "Moving card should be Q♥")
        }

        // Test Queen of Diamonds (red) to King of Spades (black)
        val redQueenDiamonds = Card(Card.Suit.DIAMONDS, Card.Rank.QUEEN, true)
        val diamondsSourcePile = Pile(Pile.Type.TABLEAU, listOf(redQueenDiamonds))

        val gameStateWithDiamonds = GameState(
            tableau = listOf(diamondsSourcePile, targetPile),
            foundation = listOf(),
            stock = Pile(Pile.Type.STOCK),
            waste = Pile(Pile.Type.WASTE)
        )

        val moveDiamonds = GameLogic.findValidMove(gameStateWithDiamonds, redQueenDiamonds)
        assertNotNull(moveDiamonds, "Should find a valid tableau move for Q♦")
        moveDiamonds?.let { (from, to, card) ->
            assertEquals(diamondsSourcePile, from, "Source pile should be the source tableau pile")
            assertEquals(targetPile, to, "Target pile should be the target tableau pile")
            assertEquals(redQueenDiamonds, card, "Moving card should be Q♦")
        }
    }

    @Test
    fun testFaceDownCard() {
        // Create test cards
        val faceDownCard = Card(Card.Suit.HEARTS, Card.Rank.ACE, false)

        // Create test pile
        val sourcePile = Pile(Pile.Type.TABLEAU, listOf(faceDownCard))

        // Create game state
        val gameState = GameState(
            tableau = listOf(sourcePile),
            foundation = listOf(),
            stock = Pile(Pile.Type.STOCK),
            waste = Pile(Pile.Type.WASTE)
        )

        val move = GameLogic.findValidMove(gameState, faceDownCard)
        assertNull(move, "Should not find a valid move for face-down card")
    }

    @Test
    fun `test stock pile cycling`() {
        // Create test cards
        val aceHearts = Card(Card.Suit.HEARTS, Card.Rank.ACE, true)
        val twoHearts = Card(Card.Suit.HEARTS, Card.Rank.TWO, true)
        val threeHearts = Card(Card.Suit.HEARTS, Card.Rank.THREE, true)

        // Create initial game state with cards in waste pile
        val wastePile = Pile(Pile.Type.WASTE, listOf(aceHearts, twoHearts, threeHearts))
        val stockPile = Pile(Pile.Type.STOCK)

        val gameState = GameState(
            tableau = listOf(),
            foundation = listOf(),
            stock = stockPile,
            waste = wastePile
        )

        // Test cycling waste back to stock
        val result = GameLogic.cycleWasteToStock(gameState)
        assertEquals(3, result.newStock.size, "All cards should be moved to stock")
        assertTrue(result.newWaste.isEmpty, "Waste pile should be empty")

        // Cards should be in reverse order when moved back to stock
        val stockCards = result.newStock.cards
        assertEquals(threeHearts, stockCards[0], "Third card should be first in stock")
        assertEquals(twoHearts, stockCards[1], "Second card should be second in stock")
        assertEquals(aceHearts, stockCards[2], "First card should be last in stock")
    }

    @Test
    fun `test win condition`() {
        // Create a complete suit of hearts from Ace to King
        val heartsSuit = listOf(
            Card(Card.Suit.HEARTS, Card.Rank.ACE, true),
            Card(Card.Suit.HEARTS, Card.Rank.TWO, true),
            Card(Card.Suit.HEARTS, Card.Rank.THREE, true),
            Card(Card.Suit.HEARTS, Card.Rank.FOUR, true),
            Card(Card.Suit.HEARTS, Card.Rank.FIVE, true),
            Card(Card.Suit.HEARTS, Card.Rank.SIX, true),
            Card(Card.Suit.HEARTS, Card.Rank.SEVEN, true),
            Card(Card.Suit.HEARTS, Card.Rank.EIGHT, true),
            Card(Card.Suit.HEARTS, Card.Rank.NINE, true),
            Card(Card.Suit.HEARTS, Card.Rank.TEN, true),
            Card(Card.Suit.HEARTS, Card.Rank.JACK, true),
            Card(Card.Suit.HEARTS, Card.Rank.QUEEN, true),
            Card(Card.Suit.HEARTS, Card.Rank.KING, true)
        )

        // Test not winning state - only one foundation pile complete
        run<Unit> {
            val gameState = GameState(
                tableau = listOf(),
                foundation = listOf(
                    Pile(Pile.Type.FOUNDATION, heartsSuit)
                ),
                stock = Pile(Pile.Type.STOCK),
                waste = Pile(Pile.Type.WASTE)
            )
            assertFalse(GameLogic.isGameWon(gameState), "Game should not be won with only one foundation pile complete")
        }

        // Test winning state - all foundation piles complete
        run<Unit> {
            val gameState = GameState(
                tableau = listOf(),
                foundation = listOf(
                    Pile(Pile.Type.FOUNDATION, heartsSuit),
                    Pile(Pile.Type.FOUNDATION, heartsSuit.map { it.copy(suit = Card.Suit.DIAMONDS) }),
                    Pile(Pile.Type.FOUNDATION, heartsSuit.map { it.copy(suit = Card.Suit.CLUBS) }),
                    Pile(Pile.Type.FOUNDATION, heartsSuit.map { it.copy(suit = Card.Suit.SPADES) })
                ),
                stock = Pile(Pile.Type.STOCK),
                waste = Pile(Pile.Type.WASTE)
            )
            assertTrue(GameLogic.isGameWon(gameState), "Game should be won with all foundation piles complete")
        }
    }

    @Test
    fun testMovePriority() {
        // Create test cards
        val aceHearts = Card(Card.Suit.HEARTS, Card.Rank.ACE, true)
        val twoHearts = Card(Card.Suit.HEARTS, Card.Rank.TWO, true)
        val blackThree = Card(Card.Suit.SPADES, Card.Rank.THREE, true)

        // Create test piles
        val sourcePile = Pile(Pile.Type.TABLEAU, listOf(twoHearts))
        val foundationPile = Pile(Pile.Type.FOUNDATION, listOf(aceHearts))
        val tableauPile = Pile(Pile.Type.TABLEAU, listOf(blackThree))

        // Create game state
        val gameState = GameState(
            tableau = listOf(sourcePile, tableauPile),
            foundation = listOf(foundationPile),
            stock = Pile(Pile.Type.STOCK),
            waste = Pile(Pile.Type.WASTE)
        )

        val move = GameLogic.findValidMove(gameState, twoHearts)
        assertNotNull(move, "Should find a valid move")
        move?.let { (_, to, _) ->
            assertEquals(foundationPile, to, "Should prioritize foundation move over tableau move")
        }
    }

    @Test
    fun testEmptyTableauMove() {
        // Create test cards
        val redKing = Card(Card.Suit.HEARTS, Card.Rank.KING, true)
        val redQueen = Card(Card.Suit.HEARTS, Card.Rank.QUEEN, true)

        // Create test piles
        val sourcePile = Pile(Pile.Type.TABLEAU, listOf(redKing))
        val emptyTableau = Pile(Pile.Type.TABLEAU)

        // Create game state
        val gameState = GameState(
            tableau = listOf(sourcePile, emptyTableau),
            foundation = listOf(),
            stock = Pile(Pile.Type.STOCK),
            waste = Pile(Pile.Type.WASTE)
        )

        // Test King to empty tableau
        val kingMove = GameLogic.findValidMove(gameState, redKing)
        assertNotNull(kingMove, "Should find a valid move for K♥ to empty tableau")
        kingMove?.let { (_, to, _) ->
            assertEquals(emptyTableau, to, "Target pile should be the empty tableau")
        }

        // Test non-King to empty tableau
        val queenMove = GameLogic.findValidMove(gameState, redQueen)
        assertNull(queenMove, "Should not find a valid move for Q♥ to empty tableau")
    }

    @Test
    fun testWastePileMove() {
        // Create test cards
        val aceHearts = Card(Card.Suit.HEARTS, Card.Rank.ACE, true)
        val twoHearts = Card(Card.Suit.HEARTS, Card.Rank.TWO, true)

        // Create test piles
        val wastePile = Pile(Pile.Type.WASTE, listOf(twoHearts))
        val foundationPile = Pile(Pile.Type.FOUNDATION, listOf(aceHearts))

        // Create game state
        val gameState = GameState(
            tableau = listOf(),
            foundation = listOf(foundationPile),
            stock = Pile(Pile.Type.STOCK),
            waste = wastePile
        )

        val move = GameLogic.findValidMove(gameState, twoHearts)
        assertNotNull(move, "Should find a valid move from waste pile")
        move?.let { (from, to, card) ->
            assertEquals(wastePile, from, "Source pile should be the waste pile")
            assertEquals(foundationPile, to, "Target pile should be the foundation pile")
            assertEquals(twoHearts, card, "Moving card should be 2♥")
        }
    }

    @Test
    fun testInvalidMoves() {
        // Create test cards with invalid sequences
        val redKing = Card(Card.Suit.HEARTS, Card.Rank.KING, true)
        val redQueen = Card(Card.Suit.HEARTS, Card.Rank.QUEEN, true)
        val aceSpades = Card(Card.Suit.SPADES, Card.Rank.ACE, true)
        val threeHearts = Card(Card.Suit.HEARTS, Card.Rank.THREE, true)

        // Create test piles
        val sourcePile = Pile(Pile.Type.TABLEAU, listOf(redQueen))
        val targetPile = Pile(Pile.Type.TABLEAU, listOf(redKing)) // Same color, invalid
        val foundationPile = Pile(Pile.Type.FOUNDATION, listOf(aceSpades)) // Wrong suit

        // Create game state
        val gameState = GameState(
            tableau = listOf(sourcePile, targetPile),
            foundation = listOf(foundationPile),
            stock = Pile(Pile.Type.STOCK),
            waste = Pile(Pile.Type.WASTE)
        )

        // Test same color tableau move
        val sameColorMove = GameLogic.findValidMove(gameState, redQueen)
        assertNull(sameColorMove, "Should not find a valid move for same color cards")

        // Test wrong suit foundation move
        val wrongSuitMove = GameLogic.findValidMove(gameState, threeHearts)
        assertNull(wrongSuitMove, "Should not find a valid move for wrong suit in foundation")
    }

    @Test
    fun testRevealFaceDownCard() {
        // Create test cards
        val fourSpades = Card(Card.Suit.SPADES, Card.Rank.FOUR, false) // face-down card
        val fiveHearts = Card(Card.Suit.HEARTS, Card.Rank.FIVE, true)
        val sixSpades = Card(Card.Suit.SPADES, Card.Rank.SIX, true)

        // Create test piles
        val sourcePile = Pile(Pile.Type.TABLEAU, listOf(fourSpades, fiveHearts))
        val targetPile = Pile(Pile.Type.TABLEAU, listOf(sixSpades))

        // Create game state
        val gameState = GameState(
            tableau = listOf(sourcePile, targetPile),
            foundation = listOf(),
            stock = Pile(Pile.Type.STOCK),
            waste = Pile(Pile.Type.WASTE)
        )

        System.err.println("\n[DEBUG_LOG] ===== Testing move that reveals face-down card =====")
        val move = GameLogic.findValidMove(gameState, fiveHearts)
        assertNotNull(move, "Should find a move for 5♥ that reveals face-down card")
        move?.let { (from, to, card) ->
            assertEquals(sourcePile, from, "Source should be tableau with face-down card")
            assertEquals(targetPile, to, "Should move to tableau with 6♠")
            assertEquals(fiveHearts, card, "Moving card should be 5♥")
        }
    }

    @Test
    fun `test invalid sequence moves`() {
        // Create an invalid sequence: black 8 -> black 7 (same color)
        val blackEight = Card(Card.Suit.SPADES, Card.Rank.EIGHT, true)
        val blackSeven = Card(Card.Suit.CLUBS, Card.Rank.SEVEN, true)
        val redNine = Card(Card.Suit.HEARTS, Card.Rank.NINE, true)

        // Create source pile with invalid sequence
        val sourcePile = Pile(Pile.Type.TABLEAU, listOf(blackEight, blackSeven))
        val targetPile = Pile(Pile.Type.TABLEAU, listOf(redNine))

        val gameState = GameState(
            tableau = listOf(sourcePile, targetPile),
            foundation = listOf(),
            stock = Pile(Pile.Type.STOCK),
            waste = Pile(Pile.Type.WASTE)
        )

        // Test moving the invalid sequence
        val move = GameLogic.findValidMove(gameState, blackEight)
        assertNull(move, "Should not find a valid move for invalid sequence")
    }

    @Test
    fun `test moving sequence to foundation`() {
        // Create a valid sequence: red 2 -> black A
        val redTwo = Card(Card.Suit.HEARTS, Card.Rank.TWO, true)
        val blackAce = Card(Card.Suit.SPADES, Card.Rank.ACE, true)
        val aceHearts = Card(Card.Suit.HEARTS, Card.Rank.ACE, true)

        // Create source pile with sequence and foundation pile
        val sourcePile = Pile(Pile.Type.TABLEAU, listOf(redTwo, blackAce))
        val foundationPile = Pile(Pile.Type.FOUNDATION, listOf(aceHearts))

        val gameState = GameState(
            tableau = listOf(sourcePile),
            foundation = listOf(foundationPile),
            stock = Pile(Pile.Type.STOCK),
            waste = Pile(Pile.Type.WASTE)
        )

        // Test moving the sequence to foundation
        val move = GameLogic.findValidMove(gameState, redTwo)
        assertNull(move, "Should not find a valid move for sequence to foundation")
    }

    @Test
    fun `test moving multiple cards in sequence`() {
        // Create a sequence of cards: black 8 -> red 7 -> black 6
        val blackEight = Card(Card.Suit.SPADES, Card.Rank.EIGHT, true)
        val redSeven = Card(Card.Suit.HEARTS, Card.Rank.SEVEN, true)
        val blackSix = Card(Card.Suit.CLUBS, Card.Rank.SIX, true)
        val redNine = Card(Card.Suit.HEARTS, Card.Rank.NINE, true)

        // Create source pile with the sequence
        val sourcePile = Pile(Pile.Type.TABLEAU, listOf(blackEight, redSeven, blackSix))
        val targetPile = Pile(Pile.Type.TABLEAU, listOf(redNine))

        // Create game state
        val gameState = GameState(
            tableau = listOf(sourcePile, targetPile),
            foundation = listOf(),
            stock = Pile(Pile.Type.STOCK),
            waste = Pile(Pile.Type.WASTE)
        )

        // Test moving the sequence starting with 8♠
        val move = GameLogic.findValidMove(gameState, blackEight)
        assertNotNull(move, "Should find a valid move for sequence starting with 8♠")
        move?.let { (from, to, card): Triple<Pile, Pile, Card> ->
            assertEquals(sourcePile, from, "Source should be tableau with sequence")
            assertEquals(targetPile, to, "Should move to tableau with 9♥")
            assertEquals(blackEight, card, "Moving card should be 8♠")

            // Verify that the sequence is maintained
            assertTrue(GameLogic.isValidSequence(listOf(blackEight, redSeven, blackSix)), 
                "Cards should form a valid sequence")
        }
    }
}

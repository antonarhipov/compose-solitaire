package org.arhan.solitaire.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.animation.core.*
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import org.arhan.solitaire.game.GameLogic
import org.arhan.solitaire.model.Card
import org.arhan.solitaire.model.GameState
import org.arhan.solitaire.model.Pile

@Composable
fun PileView(
    pile: Pile,
    gameState: GameState,
    selectedCard: Card?,
    draggedCard: Card?,
    dragSourcePile: Pile?,
    onCardClick: (Card) -> Unit,
    onCardDoubleClick: (Card) -> Unit,
    onPileClick: (Pile) -> Unit,
    onPileDoubleClick: (Pile) -> Unit = { onPileClick(it) },
    onDragStart: (Card, Pile) -> Unit,
    onDragEnd: () -> Unit,
    onDrop: (Pile) -> Unit,
    modifier: Modifier = Modifier
) {
    var dragOffset by remember { mutableStateOf(Offset.Zero) }

    val isValidDropTarget = draggedCard != null && GameLogic.findValidMove(gameState, draggedCard)?.second == pile
    val isDragging = dragSourcePile == pile

    val animatedAlpha by animateFloatAsState(
        targetValue = when {
            draggedCard == null -> 1f
            isDragging -> 1f
            isValidDropTarget -> 0.8f
            else -> 0.6f
        },
        animationSpec = tween(durationMillis = 200)
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (isDragging) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    val animatedElevation by animateFloatAsState(
        targetValue = if (isDragging) 8f else 0f,
        animationSpec = tween(durationMillis = 200)
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            draggedCard == null -> Color.Transparent
            isValidDropTarget -> MaterialTheme.colorScheme.primary
            draggedCard != null -> Color.Red.copy(alpha = 0.3f)
            else -> Color.Transparent
        },
        animationSpec = tween(durationMillis = 300)
    )

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { offset ->
                        if (!pile.isEmpty && (pile.cards.last().faceUp || pile.type == Pile.Type.STOCK)) {
                            onDragStart(pile.cards.last(), pile)
                            dragOffset = Offset.Zero
                        }
                    },
                    onDragEnd = {
                        if (draggedCard != null) {
                            onDrop(pile)
                        }
                        dragOffset = Offset.Zero
                        onDragEnd()
                    },
                    onDragCancel = {
                        dragOffset = Offset.Zero
                        onDragEnd()
                    }
                ) { change, dragAmount ->
                    if (draggedCard != null && dragSourcePile == pile) {
                        change.consume()
                        dragOffset += dragAmount
                    }
                }
            }
            .graphicsLayer(
                alpha = animatedAlpha,
                translationX = if (isDragging) dragOffset.x else 0f,
                translationY = if (isDragging) dragOffset.y else 0f,
                scaleX = animatedScale,
                scaleY = animatedScale,
                shadowElevation = animatedElevation
            )
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        if (pile.isEmpty) {
            EmptyCardSlot(onClick = { onPileClick(pile) })
        } else {
            when (pile.type) {
                Pile.Type.TABLEAU -> TableauPileView(
                    pile = pile,
                    selectedCard = selectedCard,
                    onCardClick = onCardClick,
                    onCardDoubleClick = onCardDoubleClick,
                    onDragStart = onDragStart,
                    onDragEnd = onDragEnd
                )
                Pile.Type.FOUNDATION -> FoundationPileView(
                    pile = pile,
                    selectedCard = selectedCard,
                    onCardClick = onCardClick,
                    onCardDoubleClick = onCardDoubleClick,
                    onPileClick = onPileClick,
                    onDragStart = onDragStart,
                    onDragEnd = onDragEnd
                )
                Pile.Type.STOCK -> StockPileView(
                    pile = pile,
                    onCardClick = onCardClick,
                    onCardDoubleClick = onCardDoubleClick,
                    onDragStart = onDragStart,
                    onDragEnd = onDragEnd
                )
                Pile.Type.WASTE -> WastePileView(
                    pile = pile,
                    selectedCard = selectedCard,
                    onCardClick = onCardClick,
                    onCardDoubleClick = onCardDoubleClick,
                    onDragStart = onDragStart,
                    onDragEnd = onDragEnd
                )
            }
        }
    }
}

@Composable
private fun TableauPileView(
    pile: Pile,
    selectedCard: Card?,
    onCardClick: (Card) -> Unit,
    onCardDoubleClick: (Card) -> Unit,
    onDragStart: (Card, Pile) -> Unit,
    onDragEnd: () -> Unit
) {
    Column(
        modifier = Modifier.width(80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy((-80).dp)
    ) {
        pile.cards.forEach { card ->
            CardView(
                card = card,
                sourcePile = pile,
                isSelected = card == selectedCard,
                onClick = { onCardClick(card) },
                onDoubleClick = { onCardDoubleClick(card) },
                onDragStart = onDragStart,
                onDragEnd = onDragEnd
            )
        }
    }
}

@Composable
private fun FoundationPileView(
    pile: Pile,
    selectedCard: Card?,
    onCardClick: (Card) -> Unit,
    onCardDoubleClick: (Card) -> Unit,
    onPileClick: (Pile) -> Unit,
    onDragStart: (Card, Pile) -> Unit,
    onDragEnd: () -> Unit
) {
    Box(modifier = Modifier.width(80.dp)) {
        if (pile.isEmpty) {
            EmptyCardSlot(onClick = { onPileClick(pile) })
        } else {
            CardView(
                card = pile.cards.last(),
                sourcePile = pile,
                isSelected = pile.cards.last() == selectedCard,
                onClick = { onCardClick(pile.cards.last()) },
                onDoubleClick = { onCardDoubleClick(pile.cards.last()) },
                onDragStart = onDragStart,
                onDragEnd = onDragEnd
            )
        }
    }
}

@Composable
private fun StockPileView(
    pile: Pile,
    onCardClick: (Card) -> Unit,
    onCardDoubleClick: (Card) -> Unit,
    onDragStart: (Card, Pile) -> Unit,
    onDragEnd: () -> Unit
) {
    Box(modifier = Modifier.width(80.dp)) {
        if (pile.isEmpty) {
            EmptyCardSlot(onClick = { onCardClick(Card(Card.Suit.SPADES, Card.Rank.ACE, false)) })
        } else {
            val topCard = pile.cards.last()
            CardView(
                card = topCard.copy(faceUp = false),
                sourcePile = pile,
                isSelected = false,
                onClick = { onCardClick(topCard) },
                onDoubleClick = { onCardDoubleClick(topCard) },
                onDragStart = onDragStart,
                onDragEnd = onDragEnd,
                allowFaceDownClick = true
            )
        }
    }
}

@Composable
private fun WastePileView(
    pile: Pile,
    selectedCard: Card?,
    onCardClick: (Card) -> Unit,
    onCardDoubleClick: (Card) -> Unit,
    onDragStart: (Card, Pile) -> Unit,
    onDragEnd: () -> Unit
) {
    Box(modifier = Modifier.width(80.dp)) {
        if (!pile.isEmpty) {
            CardView(
                card = pile.cards.last(),
                sourcePile = pile,
                isSelected = pile.cards.last() == selectedCard,
                onClick = { onCardClick(pile.cards.last()) },
                onDoubleClick = { onCardDoubleClick(pile.cards.last()) },
                onDragStart = onDragStart,
                onDragEnd = onDragEnd
            )
        }
    }
}

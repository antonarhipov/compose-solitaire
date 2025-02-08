package org.arhan.solitaire.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.arhan.solitaire.model.Card
import org.arhan.solitaire.model.Pile

@Composable
fun PileView(
    pile: Pile,
    selectedCard: Card?,
    onCardClick: (Card) -> Unit,
    onCardDoubleClick: (Card) -> Unit,
    onPileClick: (Pile) -> Unit,
    onPileDoubleClick: (Pile) -> Unit = { onPileClick(it) },
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ) {
        if (pile.isEmpty) {
            EmptyCardSlot(onClick = { onPileClick(pile) })
        } else {
            when (pile.type) {
                Pile.Type.TABLEAU -> TableauPileView(pile, selectedCard, onCardClick, onCardDoubleClick)
                Pile.Type.FOUNDATION -> FoundationPileView(pile, selectedCard, onCardClick, onCardDoubleClick, onPileClick)
                Pile.Type.STOCK -> StockPileView(pile, onCardClick, onCardDoubleClick)
                Pile.Type.WASTE -> WastePileView(pile, selectedCard, onCardClick, onCardDoubleClick)
            }
        }
    }
}

@Composable
private fun TableauPileView(
    pile: Pile,
    selectedCard: Card?,
    onCardClick: (Card) -> Unit,
    onCardDoubleClick: (Card) -> Unit
) {
    Column(
        modifier = Modifier.width(80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy((-80).dp)
    ) {
        pile.cards.forEach { card ->
            CardView(
                card = card,
                isSelected = card == selectedCard,
                onClick = { onCardClick(card) },
                onDoubleClick = { onCardDoubleClick(card) }
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
    onPileClick: (Pile) -> Unit
) {
    Box(modifier = Modifier.width(80.dp)) {
        if (pile.isEmpty) {
            EmptyCardSlot(onClick = { onPileClick(pile) })
        } else {
            CardView(
                card = pile.cards.last(),
                isSelected = pile.cards.last() == selectedCard,
                onClick = { onCardClick(pile.cards.last()) },
                onDoubleClick = { onCardDoubleClick(pile.cards.last()) }
            )
        }
    }
}

@Composable
private fun StockPileView(
    pile: Pile,
    onCardClick: (Card) -> Unit,
    onCardDoubleClick: (Card) -> Unit
) {
    Box(modifier = Modifier.width(80.dp)) {
        if (pile.isEmpty) {
            EmptyCardSlot(onClick = { onCardClick(Card(Card.Suit.SPADES, Card.Rank.ACE, false)) })
        } else {
            val topCard = pile.cards.last()
            CardView(
                card = topCard.copy(faceUp = false),
                isSelected = false,
                onClick = { onCardClick(topCard) },
                onDoubleClick = { onCardDoubleClick(topCard) },
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
    onCardDoubleClick: (Card) -> Unit
) {
    Box(modifier = Modifier.width(80.dp)) {
        if (!pile.isEmpty) {
            CardView(
                card = pile.cards.last(),
                isSelected = pile.cards.last() == selectedCard,
                onClick = { onCardClick(pile.cards.last()) },
                onDoubleClick = { onCardDoubleClick(pile.cards.last()) }
            )
        }
    }
}

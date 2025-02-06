package org.arhan.solitaire.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.arhan.solitaire.model.Card
import org.arhan.solitaire.model.Pile

@Composable
fun FoundationPile(
    pile: Pile,
    onCardDoubleClick: (Card) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(80.dp)
            .height(120.dp)
            .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
    ) {
        pile.topCard?.let { card ->
            CardView(
                card = card,
                onDoubleClick = { onCardDoubleClick(card) },
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun StockPile(
    pile: Pile,
    onStockClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(80.dp)
            .height(120.dp)
            .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
            .clickable { onStockClick() }
    ) {
        pile.topCard?.let { card ->
            CardView(
                card = card,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun WastePile(
    pile: Pile,
    onCardDoubleClick: (Card) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(80.dp)
            .height(120.dp)
            .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
    ) {
        pile.topCard?.let { card ->
            CardView(
                card = card,
                onDoubleClick = { onCardDoubleClick(card) },
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun TableauPile(
    pile: Pile,
    onCardDoubleClick: (Card) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(80.dp)
            .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy((-80).dp) // Overlap cards
    ) {
        pile.cards.forEach { card ->
            CardView(
                card = card,
                onDoubleClick = { onCardDoubleClick(card) }
            )
        }
    }
}

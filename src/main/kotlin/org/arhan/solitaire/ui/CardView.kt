package org.arhan.solitaire.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.arhan.solitaire.model.Card

@Composable
fun CardView(
    card: Card,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDoubleClick: () -> Unit,
    modifier: Modifier = Modifier,
    allowFaceDownClick: Boolean = false
) {
    val scale by animateFloatAsState(if (isSelected) 1.05f else 1f)
    var lastClickTime by remember { mutableStateOf(0L) }

    Surface(
        modifier = modifier
            .scale(scale)
            .size(80.dp, 120.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray, RoundedCornerShape(8.dp))
            .clickable(enabled = card.faceUp || allowFaceDownClick) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastClickTime < 300) { // 300ms threshold for double-click
                    onDoubleClick()
                    lastClickTime = 0L // Reset to prevent triple-click
                } else {
                    onClick()
                    lastClickTime = currentTime
                }
            },
        color = if (card.faceUp) Color.White else Color.Blue.copy(alpha = 0.8f),
        shadowElevation = if (isSelected) 8.dp else 2.dp
    ) {
        if (card.faceUp) {
            Box(
                modifier = Modifier.fillMaxSize().padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${getCardDisplayValue(card)}${getSuitSymbol(card.suit)}",
                    color = if (card.isRed) Color.Red else Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun getCardDisplayValue(card: Card): String = when (card.rank) {
    Card.Rank.ACE -> "A"
    Card.Rank.JACK -> "J"
    Card.Rank.QUEEN -> "Q"
    Card.Rank.KING -> "K"
    else -> card.rank.value.toString()
}

@Composable
fun EmptyCardSlot(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .size(80.dp, 120.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        color = Color.Gray.copy(alpha = 0.1f)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                color = Color.Gray,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

private fun getSuitSymbol(suit: Card.Suit): String = when (suit) {
    Card.Suit.HEARTS -> "♥"
    Card.Suit.DIAMONDS -> "♦"
    Card.Suit.CLUBS -> "♣"
    Card.Suit.SPADES -> "♠"
}

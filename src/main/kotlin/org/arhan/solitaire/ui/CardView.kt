package org.arhan.solitaire.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.arhan.solitaire.model.Card
import org.arhan.solitaire.model.Pile

@Composable
fun CardView(
    card: Card,
    sourcePile: Pile,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDoubleClick: () -> Unit,
    onDragStart: (Card, Pile) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier,
    allowFaceDownClick: Boolean = false
) {
    val scale by animateFloatAsState(if (isSelected) 1.05f else 1f)
    var lastClickTime by remember { mutableStateOf(0L) }

    val cardBack = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFFFFFF), // Pure white
            Color(0xFFF5F5F5), // Almost white
            Color(0xFFE0E0E0), // Light grey
            Color(0xFFBDBDBD), // Medium light grey
            Color(0xFFE0E0E0), // Light grey
            Color(0xFFF5F5F5), // Almost white
            Color(0xFFFFFFFF)  // Pure white
        )
    )


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
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { if (card.faceUp || allowFaceDownClick) onDragStart(card, sourcePile) },
                    onDragEnd = { onDragEnd() }
                ) { change, _ ->
                    change.consume()
                }
            },
        color = if (card.faceUp) Color.White else Color.Blue.copy(alpha = 0.8f),
        shadowElevation = if (isSelected) 8.dp else 2.dp
    ) {
        if (card.faceUp) {
            Box(
                modifier = Modifier.fillMaxSize().padding(4.dp)
            ) {
                // Top-left corner
                Text(
                    text = "${getCardDisplayValue(card)}${getSuitSymbol(card.suit)}",
                    color = if (card.isRed) Color.Red else Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(4.dp)
                )

                // Center
                Text(
                    text = "${getCardDisplayValue(card)}${getSuitSymbol(card.suit)}",
                    color = if (card.isRed) Color.Red else Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.align(Alignment.Center)
                )

                // Bottom-right corner (rotated 180 degrees)
                Text(
                    text = "${getCardDisplayValue(card)}${getSuitSymbol(card.suit)}",
                    color = if (card.isRed) Color.Red else Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .rotate(180f)
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
            .clip(RoundedCornerShape(5.dp))
            .background(Color.Gray.copy(alpha = 0.3f))
            .border(1.dp, Color.Gray.copy(alpha = 0.9f), RoundedCornerShape(5.dp))
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

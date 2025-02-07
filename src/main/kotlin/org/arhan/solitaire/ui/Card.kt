package org.arhan.solitaire.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.arhan.solitaire.model.Card

@Composable
fun CardView(
    card: Card,
    onDoubleClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var lastClickTime by remember { mutableStateOf(0L) }

    Box(
        modifier = modifier
            .width(80.dp)
            .height(120.dp)
            .shadow(2.dp, RoundedCornerShape(8.dp))
            .background(
                color = if (card.faceUp) Color.White else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                brush = if (!card.faceUp) Brush.linearGradient(
                    colors = listOf(
                        Color.DarkGray,
                        Color.LightGray,
                        Color.LightGray,
                        Color.DarkGray,
                        Color.DarkGray,
                        Color.LightGray,
                        Color.LightGray,
                        Color.DarkGray
                    ),
                    tileMode = TileMode.Repeated,
                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end = androidx.compose.ui.geometry.Offset(40f, 40f)
                ) else Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent)),
                shape = RoundedCornerShape(8.dp)
            )
            .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
            .then(
                if (card.faceUp) {
                    Modifier.border(
                        1.dp,
                        Color.LightGray.copy(alpha = 0.5f),
                        RoundedCornerShape(7.dp)
                    )
                } else {
                    Modifier
                }
            )
            .clickable {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastClickTime < 300) { // Double click threshold
                    onDoubleClick()
                }
                lastClickTime = currentTime
            }
    ) {
        if (card.faceUp) {
            val color = if (card.isRed) Color.Red else Color.Black
            val rankText = when (card.rank) {
                Card.Rank.ACE -> "A"
                Card.Rank.KING -> "K"
                Card.Rank.QUEEN -> "Q"
                Card.Rank.JACK -> "J"
                Card.Rank.TEN -> "10"
                else -> card.rank.value.toString()
            }
            val suitSymbol = when (card.suit) {
                Card.Suit.HEARTS -> "♥"
                Card.Suit.DIAMONDS -> "♦"
                Card.Suit.CLUBS -> "♣"
                Card.Suit.SPADES -> "♠"
            }

            // Top-left corner
            Column(
                modifier = Modifier
                    .padding(6.dp)
                    .align(Alignment.TopStart)
            ) {
                Text(
                    text = rankText,
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(20.dp)
                )
                Text(
                    text = suitSymbol,
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(20.dp)
                )
            }

            // Center suit
            Text(
                text = suitSymbol,
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )

            // Bottom-right corner (rotated)
            Column(
                modifier = Modifier
                    .padding(6.dp)
                    .align(Alignment.BottomEnd)
                    .rotate(180f)
            ) {
                Text(
                    text = rankText,
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(20.dp)
                )
                Text(
                    text = suitSymbol,
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(20.dp)
                )
            }
        }
    }
}

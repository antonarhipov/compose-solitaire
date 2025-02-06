package org.arhan.solitaire.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
            .background(
                if (card.faceUp) Color.White else Color.Blue,
                RoundedCornerShape(8.dp)
            )
            .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
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
            Text(
                text = "${card.rank}${card.suit}",
                color = color,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}
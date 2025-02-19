package org.arhan.solitaire.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import org.arhan.solitaire.model.Card
import org.arhan.solitaire.model.Pile
import kotlin.time.Duration

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val state = viewModel.gameState.collectAsState().value ?: return
    val selectedCard = viewModel.selectedCard.collectAsState().value
    val draggedCard = viewModel.draggedCard
    val dragSourcePile = viewModel.dragSourcePile
    val moveCount = viewModel.moveCount.collectAsState().value
    val gameTime = viewModel.gameTime.collectAsState().value

    val backgroundGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF1B5E20), // Dark green
            Color(0xFF2E7D32), // Forest green
            Color(0xFF388E3C), // Medium green
            Color(0xFF2E7D32), // Forest green
            Color(0xFF388E3C), // Medium green
            Color(0xFF2E7D32), // Forest green
            Color(0xFF1B5E20)  // Dark green
        ),
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset(1200f, 1200f)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(brush = backgroundGradient)
            .padding(16.dp)
    ) {
        // Game stats and controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Moves: $moveCount")
            Button(onClick = viewModel::startNewGame) {
                Text("New Game")
            }
            Text("Time: ${formatTime(gameTime)}")
        }

        // Stock and Foundation piles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PileView(
                    pile = state.stock,
                    gameState = state,
                    selectedCard = selectedCard,
                    draggedCard = draggedCard,
                    dragSourcePile = dragSourcePile,
                    onCardClick = viewModel::onCardClick,
                    onCardDoubleClick = viewModel::onCardDoubleClick,
                    onPileClick = viewModel::onPileClick,
                    onDragStart = viewModel::onDragStart,
                    onDragEnd = viewModel::onDragEnd,
                    onDrop = viewModel::onDrop
                )
                PileView(
                    pile = state.waste,
                    gameState = state,
                    selectedCard = selectedCard,
                    draggedCard = draggedCard,
                    dragSourcePile = dragSourcePile,
                    onCardClick = viewModel::onCardClick,
                    onCardDoubleClick = viewModel::onCardDoubleClick,
                    onPileClick = viewModel::onPileClick,
                    onDragStart = viewModel::onDragStart,
                    onDragEnd = viewModel::onDragEnd,
                    onDrop = viewModel::onDrop
                )
            }

            Spacer(modifier = Modifier.width(32.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                state.foundation.forEach { pile ->
                    PileView(
                        pile = pile,
                        gameState = state,
                        selectedCard = selectedCard,
                        draggedCard = draggedCard,
                        dragSourcePile = dragSourcePile,
                        onCardClick = viewModel::onCardClick,
                        onCardDoubleClick = viewModel::onCardDoubleClick,
                        onPileClick = viewModel::onPileClick,
                        onDragStart = viewModel::onDragStart,
                        onDragEnd = viewModel::onDragEnd,
                        onDrop = viewModel::onDrop
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Tableau piles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            state.tableau.forEach { pile ->
                PileView(
                    pile = pile,
                    gameState = state,
                    selectedCard = selectedCard,
                    draggedCard = draggedCard,
                    dragSourcePile = dragSourcePile,
                    onCardClick = viewModel::onCardClick,
                    onCardDoubleClick = viewModel::onCardDoubleClick,
                    onPileClick = viewModel::onPileClick,
                    onDragStart = viewModel::onDragStart,
                    onDragEnd = viewModel::onDragEnd,
                    onDrop = viewModel::onDrop
                )
            }
        }
    }
}

private fun formatTime(duration: Duration): String {
    val totalSeconds = duration.inWholeSeconds
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}

package org.arhan.solitaire.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.time.Duration

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val backgroundGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF1B5E20), // Dark green
            Color(0xFF2E7D32), // Forest green
            Color(0xFF388E3C), // Medium green
            Color(0xFF43A047), // Light green
            Color(0xFF388E3C), // Medium green
            Color(0xFF2E7D32), // Forest green
            Color(0xFF1B5E20)  // Dark green
        ),
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset(1000f, 1000f)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(brush = backgroundGradient)
            .padding(16.dp)
    ) {
        // Top bar with controls and status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Game controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = { viewModel.newGame() }) {
                    Text("New Game")
                }
                Button(
                    onClick = { viewModel.undo() },
                    enabled = viewModel.uiState.canUndo
                ) {
                    Text("Undo")
                }
            }

            // Game status
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Moves: ${viewModel.uiState.moveCount}",
                    color = Color.White
                )
                Text(
                    "Time: ${formatDuration(viewModel.uiState.elapsedTime)}",
                    color = Color.White
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Game area
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Stock and waste area (top-left)
            StockAndWastePiles(
                stockPile = viewModel.uiState.gameState.stock,
                wastePile = viewModel.uiState.gameState.waste,
                onStockClick = viewModel::onStockClick,
                onWasteCardDoubleClick = viewModel::onCardDoubleClick,
                cardAnimationState = viewModel.cardAnimationState
            )

            // Foundation area (top-right)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                viewModel.uiState.gameState.foundation.forEach { pile ->
                    FoundationPile(
                        pile = pile,
                        onCardDoubleClick = viewModel::onCardDoubleClick
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Tableau area (bottom)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            viewModel.uiState.gameState.tableau.forEach { pile ->
                TableauPile(
                    pile = pile,
                    onCardDoubleClick = viewModel::onCardDoubleClick
                )
            }
        }
    }
}

private fun formatDuration(duration: Duration): String {
    val totalSeconds = duration.inWholeSeconds
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

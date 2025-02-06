package org.arhan.solitaire.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.time.Duration

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp)
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
                Text("Moves: ${viewModel.uiState.moveCount}")
                Text("Time: ${formatDuration(viewModel.uiState.elapsedTime)}")
            }
        }

        Spacer(Modifier.height(16.dp))

        // Game area
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Stock and waste area (top-left)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StockPile(
                    pile = viewModel.uiState.gameState.stock,
                    onStockClick = viewModel::onStockClick
                )
                WastePile(
                    pile = viewModel.uiState.gameState.waste,
                    onCardDoubleClick = viewModel::onCardDoubleClick
                )
            }

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

package org.arhan.solitaire

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.arhan.solitaire.ui.GameScreen
import org.arhan.solitaire.ui.GameViewModel

fun main() = application {
    val windowState = rememberWindowState()
    val viewModel = remember { GameViewModel() }

    LaunchedEffect(Unit) {
        viewModel.startNewGame()
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Solitaire",
        state = windowState
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                GameScreen(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

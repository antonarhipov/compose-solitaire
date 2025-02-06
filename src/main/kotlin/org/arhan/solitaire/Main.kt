package org.arhan.solitaire

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.arhan.solitaire.ui.GameScreen
import org.arhan.solitaire.ui.GameViewModel

fun main() = application {
    val viewModel = remember { GameViewModel() }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.onDestroy()
        }
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Solitaire",
        state = rememberWindowState()
    ) {
        GameScreen(viewModel = viewModel)
    }
}

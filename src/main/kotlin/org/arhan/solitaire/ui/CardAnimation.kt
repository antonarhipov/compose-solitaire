package org.arhan.solitaire.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

@Composable
fun AnimatedCard(
    card: org.arhan.solitaire.model.Card,
    isFlipping: Boolean,
    targetOffset: androidx.compose.ui.geometry.Offset,
    onAnimationComplete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isFlipped by remember { mutableStateOf(false) }

    val flipRotation by animateFloatAsState(
        targetValue = if (isFlipping) 180f else 0f,
        animationSpec = tween(
            durationMillis = 250,
            easing = FastOutSlowInEasing
        ),
        finishedListener = { isFlipped = isFlipping }
    )

    val scale by animateFloatAsState(
        targetValue = if (flipRotation in 45f..135f) 0.9f else 1f,
        animationSpec = tween(
            durationMillis = 100,
            easing = LinearEasing
        )
    )

    val offset by animateOffsetAsState(
        targetValue = targetOffset,
        animationSpec = tween(
            durationMillis = 400,
            delayMillis = 100,
            easing = FastOutSlowInEasing
        ),
        finishedListener = { onAnimationComplete() }
    )

    // Change face-up state halfway through the flip
    val isFaceUp = if (flipRotation < 90f) !isFlipping else isFlipping

    CardView(
        card = card.copy(faceUp = isFaceUp),
        modifier = modifier
            .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
            .graphicsLayer {
                rotationY = flipRotation
                scaleX = scale
                scaleY = scale
                cameraDistance = 12 * density
                // Flip the card when it's facing away from the camera
                if (flipRotation > 90f) {
                    rotationY = flipRotation + 180f
                }
            }
    )
}

@Composable
fun rememberCardAnimationState(): CardAnimationState {
    return remember { CardAnimationState() }
}

class CardAnimationState {
    var isAnimating by mutableStateOf(false)
    var animatingCard by mutableStateOf<org.arhan.solitaire.model.Card?>(null)
    var targetOffset by mutableStateOf(androidx.compose.ui.geometry.Offset.Zero)

    fun startAnimation(card: org.arhan.solitaire.model.Card, target: androidx.compose.ui.geometry.Offset) {
        isAnimating = true
        animatingCard = card
        targetOffset = target
    }

    fun endAnimation() {
        isAnimating = false
        animatingCard = null
        targetOffset = androidx.compose.ui.geometry.Offset.Zero
    }
}

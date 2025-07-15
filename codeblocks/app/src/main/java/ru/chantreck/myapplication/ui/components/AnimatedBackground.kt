package ru.chantreck.myapplication.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

@Composable
fun AnimatedBackground() {
    val particles = remember { List(50) { Particle() } }

    particles.forEach { particle ->
        val infiniteTransition = rememberInfiniteTransition()

        val xAnim by infiniteTransition.animateFloat(
            initialValue = particle.startX,
            targetValue = particle.endX,
            animationSpec = infiniteRepeatable(
                animation = tween(particle.duration, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        val yAnim by infiniteTransition.animateFloat(
            initialValue = particle.startY,
            targetValue = particle.endY,
            animationSpec = infiniteRepeatable(
                animation = tween(particle.duration, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        particle.currentX = xAnim
        particle.currentY = yAnim
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            drawCircle(
                color = Color(0xFF3B82F6).copy(alpha = 0.2f),
                radius = particle.size,
                center = Offset(particle.currentX * size.width, particle.currentY * size.height)
            )
        }
    }
}

private class Particle {
    val startX = Random.nextFloat()
    val startY = Random.nextFloat()
    val endX = Random.nextFloat()
    val endY = Random.nextFloat()
    val size = Random.nextFloat() * 50f + 20f
    val duration = Random.nextInt(3000, 8000)
    var currentX = startX
    var currentY = startY
}
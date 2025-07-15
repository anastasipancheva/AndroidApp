package ru.chantreck.myapplication.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.delay
import ru.chantreck.myapplication.ui.theme.AppColors
import ru.chantreck.myapplication.ui.theme.Dimens

@Composable fun SplashScreen(onSplashFinished: () -> Unit) {
    LaunchedEffect(key1 = true) {
        delay(3000)
        onSplashFinished()
    }

    Box(modifier = Modifier.fillMaxSize()
        .background(brush = Brush.verticalGradient(colors = listOf(
            Color(0xFF2d1b69), Color(0xFF1a1a2e), Color(0xFF16213e)
        ))),
        contentAlignment = Alignment.Center
    ) {
        AnimatedBlobs()

        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedLogo()
            Spacer(Modifier.height(Dimens.Padding32))
            AnimatedText()
            Spacer(Modifier.height(Dimens.PaddingMedium))
            AnimatedSubtitle()
        }
    }
}

@Composable fun AnimatedBlobs() {
    val infiniteTransition = rememberInfiniteTransition(label = "blobs")

    val scale1 by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ), label = "scale1"
    )

    val scale2 by infiniteTransition.animateFloat(
        initialValue = 1.2f, targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ), label = "scale2"
    )

    //первый блоб (левый верхний)
    Box(modifier = Modifier.size(Dimens.Max400 * scale1)
        .offset(x = Dimens.Min150, y = Dimens.Min50)
        .clip(CircleShape)
        .background(brush = Brush.radialGradient(colors = listOf(
            Color(0xFF7b1fa2).copy(alpha = 0.6f),
            Color(0xFF4a148c).copy(alpha = 0.3f),
            Color.Transparent
        )))
        .blur(Dimens.IconSize)
    )

    //второй блоб (правый нижний)
    Box(modifier = Modifier.size(Dimens.Max350 * scale2)
        .offset(x = Dimens.Max200, y = Dimens.Max)
        .clip(CircleShape)
        .background(brush = Brush.radialGradient(colors = listOf(
            Color(0xFF3f51b5).copy(alpha = 0.4f),
            Color(0xFF1a237e).copy(alpha = 0.2f),
            Color.Transparent
        )))
        .blur(Dimens.BlocHeight)
    )

    //третий блоб (который в центре)
    Box(modifier = Modifier.size(Dimens.Max300 * scale1)
        .offset(x = Dimens.Size50, y = Dimens.Size50)
        .clip(CircleShape)
        .background(brush = Brush.radialGradient(colors = listOf(
            Color(0xFFe91e63).copy(alpha = 0.3f),
            Color(0xFFad1457).copy(alpha = 0.15f),
            Color.Transparent
        )))
        .blur(Dimens.Size70)
    )
}

@Composable fun AnimatedLogo() {
    val infiniteTransition = rememberInfiniteTransition(label = "logo")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f, targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ), label = "logoScale"
    )

    var isVisible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(1000),
        label = "logoAlpha"
    )

    LaunchedEffect(key1 = true) {
        delay(300)
        isVisible = true
    }

    Box(modifier = Modifier.size(Dimens.Max100)
        .scale(scale)
        .alpha(alpha)
        .clip(CircleShape)
        .background(brush = Brush.radialGradient(colors = listOf(
            Color(0xFF9c27b0), Color(0xFF673ab7)
        ))),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "</>",
            color = Color.White,
            fontSize = Dimens.TextTitle,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable fun AnimatedText() {
    val infiniteTransition = rememberInfiniteTransition(label = "text")

    val gradientPosition by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "gradientPosition"
    )

    val text = "Girlssss Code"
    var visibleCharCount by remember { mutableStateOf(0) }

    //печатающийся эффект
    LaunchedEffect(key1 = true) {
        for (i in 1..text.length) {
            delay(100)
            visibleCharCount = i
        }
    }

    val visibleText = text.take(visibleCharCount)

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ), label = "textScale"
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFEC4899), Color(0xFFD946EF),
            Color(0xFF8B5CF6), Color(0xFFEC4899)
        ),
        start = Offset(0f, 0f),
        end = Offset(gradientPosition * 2, 0f)
    )

    Text(text = visibleText,
        color = Color.White,
        fontSize = Dimens.Text48,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier.scale(scale)
    )
}

@Composable fun AnimatedSubtitle() {
    var isVisible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(1000),
        label = "subtitleAlpha"
    )

    LaunchedEffect(key1 = true) {
        delay(1200)
        isVisible = true
    }

    Text(text = "Make your own program and nails",
        style = MaterialTheme.typography.bodyLarge,
        color = Color.White.copy(alpha = 0.7f),
        textAlign = TextAlign.Center,
        modifier = Modifier.alpha(alpha)
    )
}

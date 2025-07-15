package ru.chantreck.myapplication.ui.theme

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFEC4899),
    secondary = Color(0xFF8B5CF6),
    tertiary = Color(0xFF06B6D4),
    background = Color(0xFF0F172A),
    surface = Color(0xFF1E293B),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFFE2E8F0),
    onSurface = Color(0xFFE2E8F0),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFEC4899),
    secondary = Color(0xFF8B5CF6),
    tertiary = Color(0xFF06B6D4),
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
)

@Composable
fun Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}

object AppColors {
    val First = Color(0xFFEC4899)
    val Second = Color(0xFF8B5CF6)
    val Back = Color(0xFF0F172A)
    val Surface = Color.White.copy(alpha = 0.05f)
    val SurfaceVar = Color(0xFF9CA3AF)
    val BlockAssignment = Color(0xFF90CAF9)
    val BlockCondition = Color(0xFFFFE082)
    val BlockElse = Color(0xFFA5D6A7)
    val BlockVariable = Color(0xFFB3E5FC)
    val BlockLoop = Color(0xFFCE93D8)
    val BlockWhile = Color(0xFFB0BEC5)
    val BlockPrint = Color(0xFFAED581)
    val BlockControl = Color(0xFFE0E0E0)
    val BlockArray = Color(0xFF80DEEA)
    val BlockSort = Color(0xFFFFAB91)
    val BlockArrayAccess = Color(0xFFF48FB1)
}

object AppGradients {
    val First = Brush.linearGradient(
        colors = listOf(AppColors.First, AppColors.Second)
    )

    val Back = Brush.radialGradient(
        colors = listOf(
            AppColors.Back,
            AppColors.Back.copy(alpha = 0.9f),
            AppColors.Back
        )
    )

    val Disabled = Brush.linearGradient(
        colors = listOf(Color.Gray, Color.Gray)
    )
}

@Composable
fun ThemeAnimatedBack() {
    val infiniteTransition = rememberInfiniteTransition(label = "background")

    val blob1Offset by infiniteTransition.animateValue(
        initialValue = Offset(0f, 0f),
        targetValue = Offset(30f, -50f),
        typeConverter = Offset.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(7000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blob1"
    )

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(AppGradients.Back)
    ) {
        drawCircle(
            color = AppColors.First.copy(alpha = 0.08f),
            radius = Dimens.Max150.toPx(),
            center = Offset(size.width - Dimens.Max100.toPx(), Dimens.Max100.toPx()) + blob1Offset
        )

        drawCircle(
            color = AppColors.Second.copy(alpha = 0.08f),
            radius = Dimens.Max150.toPx(),
            center = Offset(Dimens.Max100.toPx(), size.height - Dimens.Max100.toPx())
        )
    }
}

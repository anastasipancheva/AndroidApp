package ru.chantreck.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import ru.chantreck.myapplication.ui.theme.Dimens

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val gradient = Brush.linearGradient(
        listOf(
            Color(0xFFEC4899),
            Color(0xFF8B5CF6)
        )
    )
    Box(
        modifier = modifier
            .height(Dimens.ButtonHeight)
            .background(gradient, RoundedCornerShape(Dimens.CornerRadius10))
    ) {
        Button(
            onClick = onClick,
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
            modifier = Modifier
        ) {
            Text(
                text = text,
                fontSize = Dimens.TextMedium
            )
        }
    }
}

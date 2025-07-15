package ru.chantreck.myapplication.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background

@Composable
fun VariableBlock(variables: List<String>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Green.copy(alpha = 0.2f))
            .padding(8.dp)
    ) {
        Text(
            text = "Объявить: ${variables.joinToString()}",
            color = Color.DarkGray
        )//надо добавить возможность объявлять переменные
    }
}
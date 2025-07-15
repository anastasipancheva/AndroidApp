package ru.chantreck.myapplication.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.chantreck.myapplication.model.ArithmeticOperator
import androidx.compose.foundation.background

@Composable
fun ArithmeticBlock(operator: ArithmeticOperator, operands: List<String>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Magenta.copy(alpha = 0.2f))
            .padding(8.dp)
    ) {
        Text(
            text = "${operands.joinToString()} ${operator.name}",
            color = Color.DarkGray
        )
    }
}//ничего не работает это заглушка
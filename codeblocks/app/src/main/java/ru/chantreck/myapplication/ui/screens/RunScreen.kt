package ru.chantreck.myapplication.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import ru.chantreck.myapplication.model.CodeBlockData
import ru.chantreck.myapplication.model.VariableData
import ru.chantreck.myapplication.ui.theme.Dimens

// TODO пошаговое выполнение??
@Composable fun RunScreen(
    blocks: List<CodeBlockData>, output: String,
    variables: Map<String, VariableData>,
    onRunCode: () -> Unit,
    onStepExecution: () -> Unit, //потом
    onResetExecution: () -> Unit,
    onBackToEditor: () -> Unit,
    isExecuting: Boolean,
    currentStep: Int,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Box(modifier = modifier.fillMaxSize().background(colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize().padding(Dimens.PaddingLarge),
            verticalArrangement = Arrangement.spacedBy(Dimens.Padding20)
        ) {
            Text("Code Execution",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = Dimens.Text28,
                    fontWeight = FontWeight.Bold
                ),
                color = colorScheme.onBackground
            )

            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmallMedium)
            ) {
                //кнопка запуска!
                Button(onClick = { onRunCode() },
                    modifier = Modifier.weight(1f).height(Dimens.ButtonHeight),
                    enabled = blocks.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(Dimens.PaddingSmall),
                    contentPadding = PaddingValues(Dimens.Padding0)
                ) {
                    Box(modifier = Modifier.fillMaxSize()
                        .background(if (blocks.isNotEmpty()) {
                            Brush.horizontalGradient(colors = listOf(
                                Color(0xFF4caf50), Color(0xFF66bb6a)
                            ))
                        } else {
                            Brush.horizontalGradient(colors = listOf(
                                Color(0xFF6a6a7a).copy(alpha = 0.6f),
                                Color(0xFF5a5a6a).copy(alpha = 0.6f)
                            ))
                        }),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(Dimens.Padding20)
                            )
                            Spacer(Modifier.width(Dimens.PaddingSmall))
                            Text("Run",
                                style = MaterialTheme.typography.titleMedium
                                    .copy(fontWeight = FontWeight.SemiBold),
                                color = Color.White
                            )
                        }
                    }
                }

                //кнопка сброса!
                Button(onClick = { onResetExecution() },
                    modifier = Modifier.weight(1f).height(Dimens.ButtonHeight),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(Dimens.PaddingSmall),
                    contentPadding = PaddingValues(Dimens.Padding0)
                ) {
                    Box(modifier = Modifier.fillMaxSize()
                        .background(Brush.horizontalGradient(colors = listOf(
                            Color(0xFFe91e63), Color(0xFFf06292)
                        ))),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(Dimens.Padding20)
                            )
                            Spacer(Modifier.width(Dimens.PaddingSmall))
                            Text("Reset",
                                style = MaterialTheme.typography.titleMedium
                                    .copy(fontWeight = FontWeight.SemiBold),
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
            ) {
                //с блоками кода
                Card(modifier = Modifier.fillMaxWidth().weight(1f),
                    shape = RoundedCornerShape(Dimens.PaddingMedium),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = Dimens.PaddingSmall)
                ) {
                    Column(modifier = Modifier.fillMaxSize().padding(Dimens.Padding20)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(Dimens.PaddingSmall)
                                .clip(RoundedCornerShape(Dimens.PaddingSmallest))
                                .background(Brush.horizontalGradient(colors = listOf(
                                    Color(0xFF9c27b0), Color(0xFFe91e63)
                                )))
                            )
                            Spacer(Modifier.width(Dimens.PaddingSmallMedium))
                            Text("Code Blocks",
                                style = MaterialTheme.typography.titleLarge
                                    .copy(fontWeight = FontWeight.Bold),
                                color = colorScheme.onSurface
                            )
                        }

                        Spacer(Modifier.height(Dimens.PaddingMedium))

                        if (blocks.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No blocks to execute",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)) {
                                val sortedBlocks = blocks.sortedBy { it.mutableOffsetY }
                                itemsIndexed(sortedBlocks) { index, block ->
                                    val isCurrentStep = isExecuting && index == currentStep

                                    Card(modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(Dimens.PaddingSmallMedium),
                                        colors = CardDefaults.cardColors(
                                            containerColor = when {
                                                isCurrentStep -> colorScheme.primaryContainer
                                                index < currentStep && isExecuting ->
                                                    Color(0xFF4caf50).copy(alpha = 0.2f)
                                                else -> colorScheme.surfaceVariant
                                            }
                                        ),
                                        border = if (isCurrentStep) BorderStroke(Dimens.Padding2, Color(0xFF9c27b0))
                                        else null
                                    ) {
                                        Row(modifier = Modifier.fillMaxWidth().padding(Dimens.PaddingSmallMedium),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(modifier = Modifier.size(Dimens.PaddingLarge)
                                                .clip(RoundedCornerShape(Dimens.PaddingSmallMedium))
                                                .background(when {
                                                    isCurrentStep -> Brush.horizontalGradient(
                                                        colors = listOf(
                                                            Color(0xFF9c27b0),
                                                            Color(0xFFe91e63)
                                                        )
                                                    )
                                                    index < currentStep && isExecuting ->
                                                        Brush.horizontalGradient(
                                                            colors = listOf(
                                                                Color(0xFF4caf50),
                                                                Color(0xFF66bb6a)
                                                            )
                                                        )
                                                    else -> Brush.horizontalGradient(
                                                        colors = listOf(
                                                            Color(0xFF6a6a7a),
                                                            Color(0xFF5a5a6a)
                                                        )
                                                    )
                                                }),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("${index + 1}",
                                                    style = MaterialTheme.typography.bodySmall
                                                        .copy(fontWeight = FontWeight.Bold),
                                                    color = Color.White,
                                                    fontSize = Dimens.TextSmall
                                                )
                                            }

                                            Spacer(Modifier.width(Dimens.PaddingSmallMedium))

                                            Text(block.text,
                                                style = MaterialTheme.typography.bodyMedium
                                                    .copy(fontWeight = FontWeight.Medium),
                                                color = colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                //вывод
                Card(modifier = Modifier.fillMaxWidth().weight(1f),
                    shape = RoundedCornerShape(Dimens.PaddingMedium),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = Dimens.PaddingSmall)
                ) {
                    Column(modifier = Modifier.fillMaxSize().padding(Dimens.Padding20)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Output",
                                style = MaterialTheme.typography.titleLarge
                                    .copy(fontWeight = FontWeight.Bold),
                                color = colorScheme.onSurface
                            )
                        }

                        Spacer(Modifier.height(Dimens.PaddingMedium))

                        val scrollState = rememberScrollState()
                        Box(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
                            Text(output.ifEmpty { "No output((" },
                                style = MaterialTheme.typography.bodyMedium
                                    .copy(fontFamily = FontFamily.Monospace),
                                color = colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

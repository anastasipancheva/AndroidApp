package ru.chantreck.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.draw.blur
import ru.chantreck.myapplication.ui.theme.Dimens

@Composable
fun WelcomeScreen(
    onContinue: (String) -> Unit
) {
    var projectName by remember { mutableStateOf("My project") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2d1b69),
                        Color(0xFF1a1a2e),
                        Color(0xFF16213e)
                    )
                )
            )
    ) {

        Box(
            modifier = Modifier
                .size(Dimens.Max400)
                .offset(x = Dimens.Min150, y = Dimens.Min50)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF7b1fa2).copy(alpha = 0.6f),
                            Color(0xFF4a148c).copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    )
                )
                .blur(Dimens.Size80)

        )

        Box(
            modifier = Modifier
                .size(Dimens.Max350)
                .offset(x = Dimens.Max200, y = Dimens.Max)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(

                        colors = listOf(
                            Color(0xFF3f51b5).copy(alpha = 0.4f),
                            Color(0xFF1a237e).copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    )
                )
                .blur(Dimens.BlocHeight)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.Padding32),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(Dimens.Size80)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF9c27b0),
                                Color(0xFF673ab7)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "</>",
                    color = Color.White,
                    fontSize = Dimens.TextLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(Dimens.Padding32))

            Text(
                text = "Girlssss Code",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = Dimens.TextTitle
            )

            Spacer(modifier = Modifier.height(Dimens.PaddingSmall))

            Text(
                text = "Make your own program and nails",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimens.ButtonHeight))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.PaddingMedium),
                shape = RoundedCornerShape(Dimens.PaddingMedium),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2a2a3e).copy(alpha = 0.8f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = Dimens.PaddingSmall)
            ) {
                Column(
                    modifier = Modifier.padding(Dimens.PaddingLarge),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "New project",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(Dimens.PaddingLarge))

                    OutlinedTextField(
                        value = projectName,
                        onValueChange = { projectName = it },
                        label = {
                            Text(
                                "Name",
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF9c27b0),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            cursorColor = Color(0xFF9c27b0),
                            focusedContainerColor = Color(0xFF3a3a4e).copy(alpha = 0.5f),
                            unfocusedContainerColor = Color(0xFF3a3a4e).copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(Dimens.PaddingSmall),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences
                        )
                    )
                    Spacer(modifier = Modifier.height(Dimens.PaddingLarge))

                    Button(
                        onClick = { onContinue(projectName.trim()) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (projectName.trim().isNotBlank()) {
                                Color.Transparent
                            } else {
                                Color(0xFF6a6a7a).copy(alpha = 0.6f)
                            },
                            contentColor = if (projectName.trim().isNotBlank()) {
                                Color.White
                            } else {
                                Color.White.copy(alpha = 0.5f)
                            },
                            disabledContainerColor = Color(0xFF6a6a7a).copy(alpha = 0.6f),
                            disabledContentColor = Color.White.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(Dimens.PaddingSmall),
                        enabled = projectName.trim().isNotBlank()
                    ) {
                        Box(
                            modifier = if (projectName.trim().isNotBlank()) {
                                Modifier
                                    .fillMaxWidth()
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                Color(0xFF9c27b0),
                                                Color(0xFFe91e63)
                                            )
                                        ),
                                        shape = RoundedCornerShape(Dimens.PaddingSmall)
                                    )
                                    .padding(vertical = Dimens.PaddingSmallMedium)
                            }
                            else {
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = Dimens.PaddingSmallMedium)
                            },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Let's go",
                                fontWeight = FontWeight.Medium,
                                fontSize = Dimens.TextMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

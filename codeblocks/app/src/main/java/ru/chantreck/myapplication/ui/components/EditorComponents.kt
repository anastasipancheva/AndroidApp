package ru.chantreck.myapplication.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import ru.chantreck.myapplication.model.CodeBlockData
import ru.chantreck.myapplication.model.VariableData
import ru.chantreck.myapplication.model.Tab
import ru.chantreck.myapplication.ui.theme.*
import androidx.compose.ui.platform.LocalDensity

data class VariableDefinition(
    val name: String,
    val type: String,
    val value: String
)

fun getNextBlockPosition(blocks: List<CodeBlockData>): Pair<Float, Float> {
    val fixedX = 100f

    if (blocks.isEmpty()) {
        return Pair(fixedX, 100f)
    }

    val lowestBlock = blocks.maxByOrNull { it.mutableOffsetY }

    return if (lowestBlock != null) {
        Pair(fixedX, lowestBlock.mutableOffsetY + 160f)
    } else {
        Pair(fixedX, 100f)
    }
}

//нажатие на кнопку
private fun handleBlockClick(
    type: String,
    color: Color,
    onBlockCreate: (CodeBlockData) -> Unit,
    onEqualsClick: () -> Unit,
    onWhileClick: () -> Unit,
    onArrayClick: () -> Unit,
    showIfDialog: () -> Unit,
    showPrintDialog: () -> Unit,
    showMultiVarDialog: () -> Unit,
    showSortDialog: () -> Unit,
    showArraySetDialog: () -> Unit,
    showArrayGetDialog: () -> Unit,
    showArrayDialog: () -> Unit,
    showWhileDialog: () -> Unit,
    variables: Map<String, VariableData>,
    onShowError: (String) -> Unit,
    blocks: List<CodeBlockData>
) {
    val arrayVariables = variables.filter { it.value.isArray() }
    val (nextX, nextY) = getNextBlockPosition(blocks)

    try {
        when (type) {
            "var" -> {
                showMultiVarDialog()
            }
            "assign" -> {
                onEqualsClick()
            }
            "if" -> {
                showIfDialog()
            }
            "else" -> {
                val hasIfBlock = blocks.any { it.text.startsWith("if (") }
                if (!hasIfBlock) {
                    onShowError("Cannot create else without if, you know??")
                    return
                }

                onBlockCreate(
                    CodeBlockData(
                        text = "else",
                        color = color,
                        offsetX = nextX,
                        offsetY = nextY
                    )
                )
                onBlockCreate(
                    CodeBlockData(
                        text = "begin",
                        color = AppColors.BlockControl,
                        offsetX = nextX,
                        offsetY = nextY + 160f
                    )
                )
                onBlockCreate(
                    CodeBlockData(
                        text = "end",
                        color = AppColors.BlockControl,
                        offsetX = nextX,
                        offsetY = nextY + 320f
                    )
                )
            }
            "while" -> {
                showWhileDialog()
            }
            "print" -> {
                showPrintDialog()
            }
            "array" -> {
                showArrayDialog()
            }
            "sort" -> {
                if (arrayVariables.isEmpty()) {
                    onShowError("You have not created an array!(")
                } else {
                    showSortDialog()
                }
            }
            "set[]" -> {
                if (arrayVariables.isEmpty()) {
                    onShowError("You have not created an array!(")
                } else {
                    showArraySetDialog()
                }
            }
            "get[]" -> {
                if (arrayVariables.isEmpty()) {
                    onShowError("You have not created an array!(")
                } else {
                    showArrayGetDialog()
                }
            }
            "begin" -> {
                onBlockCreate(
                    CodeBlockData(
                        text = "begin",
                        color = color,
                        offsetX = nextX,
                        offsetY = nextY
                    )
                )
            }
            "end" -> {
                onBlockCreate(
                    CodeBlockData(
                        text = "end",
                        color = color,
                        offsetX = nextX,
                        offsetY = nextY
                    )
                )
            }
            else -> {
                onBlockCreate(
                    CodeBlockData(
                        text = type,
                        color = color,
                        offsetX = nextX,
                        offsetY = nextY
                    )
                )
            }
        }
    } catch (e: Exception) {
        println("Error in handleBlockClick((: ${e.message}")
        e.printStackTrace()
        onShowError("Error: ${e.message}")
    }
}

//панель с блоками
@Composable
fun BlockToolbar(
    onBlockCreate: (CodeBlockData) -> Unit,
    onEqualsClick: () -> Unit,
    onWhileClick: () -> Unit,
    onArrayClick: () -> Unit,
    showIfDialog: () -> Unit,
    showPrintDialog: () -> Unit,
    showMultiVarDialog: () -> Unit,
    showSortDialog: () -> Unit,
    showArraySetDialog: () -> Unit,
    showArrayGetDialog: () -> Unit,
    showArrayDialog: () -> Unit,
    showWhileDialog: () -> Unit,
    variables: Map<String, VariableData>,
    onShowError: (String) -> Unit,
    blocks: List<CodeBlockData>,
    onUpdateVariables: (Map<String, VariableData>) -> Unit,
    onArrayCreated: () -> Unit,
    showVarDialog: Boolean,
    onDismissAssignmentDialog: () -> Unit
) {
    var showAssignDialog by remember { mutableStateOf(false) }
    var showArrayDialog by remember { mutableStateOf(false) }
    var showArrayWithElementsDialog by remember { mutableStateOf(false) }
    var showMultiVarDialog by remember { mutableStateOf(false) }
    var showIfDialog by remember { mutableStateOf(false) }
    var showWhileDialog by remember { mutableStateOf(false) }
    var showPrintDialog by remember { mutableStateOf(false) }
    var showGetElementDialog by remember { mutableStateOf(false) }
    var showSetElementDialog by remember { mutableStateOf(false) }
    var showBubbleSortDialog by remember { mutableStateOf(false) }
    var showSortDialog by remember { mutableStateOf(false) }
    var showArraySetDialog by remember { mutableStateOf(false) }
    var showArrayGetDialog by remember { mutableStateOf(false) }

    val blockTypes = listOf(
        "var" to AppColors.BlockVariable,
        "assign" to AppColors.BlockAssignment,
        "if" to AppColors.BlockCondition,
        "else" to AppColors.BlockElse,
        "while" to AppColors.BlockWhile,
        "print" to AppColors.BlockPrint,
        "array" to AppColors.BlockArray,
        "sort" to AppColors.BlockSort,
        "set[]" to AppColors.BlockArrayAccess,
        "get[]" to AppColors.BlockArrayAccess,
        "begin" to AppColors.BlockControl,
        "end" to AppColors.BlockControl
    )

    Card(modifier = Modifier.padding(Dimens.PaddingSmallest)) {
        Column(
            modifier = Modifier.padding(vertical = Dimens.PaddingSmall),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Code Blocks",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(Dimens.PaddingSmall))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall),
                contentPadding = PaddingValues(horizontal = Dimens.PaddingSmall)
            ) {
                items(blockTypes.take(6)) { type ->
                    BlockTemplate(
                        text = type.first,
                        color = type.second,
                        onClick = {
                            handleBlockClick(
                                type = type.first,
                                color = type.second,
                                onBlockCreate = onBlockCreate,
                                onEqualsClick = onEqualsClick,
                                onWhileClick = onWhileClick,
                                onArrayClick = onArrayClick,
                                showIfDialog = { showIfDialog = true },
                                showPrintDialog = { showPrintDialog = true },
                                showMultiVarDialog = { showMultiVarDialog = true },
                                showSortDialog = { showSortDialog = true },
                                showArraySetDialog = { showArraySetDialog = true },
                                showArrayGetDialog = { showArrayGetDialog = true },
                                showArrayDialog = { showArrayDialog = true },
                                showWhileDialog = { showWhileDialog = true },
                                variables = variables,
                                onShowError = onShowError,
                                blocks = blocks
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.PaddingSmall))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall),
                contentPadding = PaddingValues(horizontal = Dimens.PaddingSmall)
            ) {
                items(blockTypes.drop(6)) { type ->
                    BlockTemplate(
                        text = type.first,
                        color = type.second,
                        onClick = {
                            handleBlockClick(
                                type = type.first,
                                color = type.second,
                                onBlockCreate = onBlockCreate,
                                onEqualsClick = onEqualsClick,
                                onWhileClick = onWhileClick,
                                onArrayClick = onArrayClick,
                                showIfDialog = { showIfDialog = true },
                                showPrintDialog = { showPrintDialog = true },
                                showMultiVarDialog = { showMultiVarDialog = true },
                                showSortDialog = { showSortDialog = true },
                                showArraySetDialog = { showArraySetDialog = true },
                                showArrayGetDialog = { showArrayGetDialog = true },
                                showArrayDialog = { showArrayDialog = true },
                                showWhileDialog = { showWhileDialog = true },
                                variables = variables,
                                onShowError = onShowError,
                                blocks = blocks
                            )
                        }
                    )
                }
            }
        }
    }

    //вызовы диалогов всяких
    if (showIfDialog) {
        IfDialog(
            onConfirm = { condition ->
                val (nextX, nextY) = getNextBlockPosition(blocks)
                onBlockCreate(
                    CodeBlockData(
                        text = "if ($condition)",
                        color = AppColors.BlockCondition,
                        offsetX = nextX,
                        offsetY = nextY
                    )
                )
                onBlockCreate(
                    CodeBlockData(
                        text = "begin",
                        color = AppColors.BlockControl,
                        offsetX = nextX,
                        offsetY = nextY + 160f
                    )
                )
                onBlockCreate(
                    CodeBlockData(
                        text = "end",
                        color = AppColors.BlockControl,
                        offsetX = nextX,
                        offsetY = nextY + 320f
                    )
                )
                showIfDialog = false
            },
            onDismiss = { showIfDialog = false }
        )
    }

    if (showWhileDialog) {
        WhileDialog(
            onConfirm = { condition ->
                val (nextX, nextY) = getNextBlockPosition(blocks)
                onBlockCreate(
                    CodeBlockData(
                        text = "while ($condition)",
                        color = AppColors.BlockWhile,
                        offsetX = nextX,
                        offsetY = nextY
                    )
                )
                onBlockCreate(
                    CodeBlockData(
                        text = "begin",
                        color = AppColors.BlockControl,
                        offsetX = nextX,
                        offsetY = nextY + 160f
                    )
                )
                onBlockCreate(
                    CodeBlockData(
                        text = "end",
                        color = AppColors.BlockControl,
                        offsetX = nextX,
                        offsetY = nextY + 320f
                    )
                )
                showWhileDialog = false
            },
            onDismiss = { showWhileDialog = false }
        )
    }

    if (showPrintDialog) {
        PrintDialog(
            variables = variables,
            onConfirm = { expression ->
                val isNumber = expression.toDoubleOrNull() != null
                val isVariable = variables.containsKey(expression)
                val isQuotedString = expression.startsWith("\"") && expression.endsWith("\"")
                val containsOperators = expression.contains("+") ||
                        expression.contains("-") ||
                        expression.contains("*") ||
                        expression.contains("/")

                if (!isNumber && !isVariable && !isQuotedString && !containsOperators) {
                    onShowError("Text must be in quotes or be a valid variable name")
                    return@PrintDialog
                }

                val (nextX, nextY) = getNextBlockPosition(blocks)
                val newBlock = CodeBlockData(
                    text = "print($expression)",
                    color = AppColors.BlockPrint,
                    offsetX = nextX,
                    offsetY = nextY
                )
                onBlockCreate(newBlock)
                showPrintDialog = false
            },
            onDismiss = { showPrintDialog = false }
        )
    }

    if (showMultiVarDialog) {
        MultiVarDialog(
            onConfirm = { variablesList ->
                val (nextX, nextY) = getNextBlockPosition(blocks)
                val newVars = variables.toMutableMap()

                variablesList.forEachIndexed { index, varData ->
                    onBlockCreate(
                        CodeBlockData(
                            text = "${varData.type} ${varData.name} = ${varData.value}",
                            color = AppColors.BlockVariable,
                            offsetX = nextX,
                            offsetY = nextY + (index * 80f)
                        )
                    )
                    newVars[varData.name] = VariableData(varData.name, varData.type, varData.value)
                }

                onUpdateVariables(newVars)
                showMultiVarDialog = false
            },
            onDismiss = { showMultiVarDialog = false }
        )
    }

    if (showSortDialog) {
        SortDialog(
            variables = variables,
            onConfirm = { arrayName ->
                val (nextX, nextY) = getNextBlockPosition(blocks)
                onBlockCreate(
                    CodeBlockData(
                        text = "bubbleSort($arrayName)",
                        color = AppColors.BlockSort,
                        offsetX = nextX,
                        offsetY = nextY
                    )
                )
                showSortDialog = false
            },
            onDismiss = { showSortDialog = false }
        )
    }

    if (showArraySetDialog) {
        ArrSetDialog(
            variables = variables,
            onConfirm = { arrayName, index, value ->
                val (nextX, nextY) = getNextBlockPosition(blocks)
                onBlockCreate(
                    CodeBlockData(
                        text = "$arrayName[$index] = $value",
                        color = AppColors.BlockArrayAccess,
                        offsetX = nextX,
                        offsetY = nextY
                    )
                )
                showArraySetDialog = false
            },
            onDismiss = { showArraySetDialog = false }
        )
    }

    if (showArrayGetDialog) {
        ArrGetDialog(
            variables = variables,
            onConfirm = { arrayName, index, variableName ->
                val (nextX, nextY) = getNextBlockPosition(blocks)
                onBlockCreate(
                    CodeBlockData(
                        text = "int $variableName = $arrayName[$index]",
                        color = AppColors.BlockArrayAccess,
                        offsetX = nextX,
                        offsetY = nextY
                    )
                )
                showArrayGetDialog = false
            },
            onDismiss = { showArrayGetDialog = false }
        )
    }

    if (showArrayDialog) {
        ArrDialog(
            onConfirm = { arrayName, elements ->
                val (nextX, nextY) = getNextBlockPosition(blocks)
                onBlockCreate(
                    CodeBlockData(
                        text = "int $arrayName[${elements.size}] = {${elements.joinToString(", ")}}",
                        color = AppColors.BlockArray,
                        offsetX = nextX,
                        offsetY = nextY
                    )
                )
                val arrayVar = VariableData(arrayName, "array")
                arrayVar.initializeArray(elements.toMutableList())
                onUpdateVariables(variables.plus(arrayName to arrayVar))
                onArrayCreated()
                showArrayDialog = false
            },
            onDismiss = { showArrayDialog = false }
        )
    }

    if (showVarDialog) {
        SetVarDialog(
            variables = variables,
            onConfirm = { varName, value, type ->
                val (nextX, nextY) = getNextBlockPosition(blocks)

                if (!variables.containsKey(varName)) {
                    onUpdateVariables(variables.plus(varName to VariableData(varName, type)))
                }

                onBlockCreate(
                    CodeBlockData(
                        text = "$varName = $value",
                        color = AppColors.BlockAssignment,
                        offsetX = nextX,
                        offsetY = nextY
                    )
                )
                onDismissAssignmentDialog()
            },
            onDismiss = onDismissAssignmentDialog
        )
    }
}

//done
@Composable
fun DraggBlock(
    block: CodeBlockData,
    workspaceSize: androidx.compose.ui.unit.IntSize,
    onPositionChange: (Float, Float) -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    onConnect: (CodeBlockData, CodeBlockData) -> Unit,
    codeBlocks: List<CodeBlockData>
) {
    val d = LocalDensity.current
    val blockWidthPx = with(d) { Dimens.BlocWidth.toPx() }
    val blockHeightPx = with(d) { Dimens.BlocHeight.toPx() }

    Card(
        shape = RoundedCornerShape(Dimens.CornerRadius16),
        colors = CardDefaults.cardColors(
            containerColor = block.color.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.PaddingSmall),
        modifier = Modifier
            .size(Dimens.BlocWidth, Dimens.BlocHeight)
            .offset {
                IntOffset(
                    block.mutableOffsetX.toInt(),
                    block.mutableOffsetY.toInt()
                )
            }
            .pointerInput(block.id) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val newX = block.mutableOffsetX + dragAmount.x
                    val newY = block.mutableOffsetY + dragAmount.y

                    val boundedX = newX.coerceIn(
                        0f,
                        workspaceSize.width - blockWidthPx
                    )
                    val boundedY = newY.coerceIn(
                        0f,
                        workspaceSize.height - blockHeightPx
                    )

                    onPositionChange(boundedX, boundedY)
                }
            }
            .pointerInput(block.id) {
                detectTapGestures(
                    onDoubleTap = { onClick() }
                )
            }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = block.text,
                color = Color.White,
                fontSize = Dimens.TextSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = Dimens.PaddingSmallest),
                maxLines = 2
            )
        }
    }
}

@Composable
fun TopBar(
    isDarkTheme: Boolean,
    onSwitchTheme: () -> Unit,
    projectName: String,
    onExportToTxt: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) {
                Color.White.copy(alpha = 0.05f)
            } else {
                Color.White
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.PaddingSmallest)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.CornerRadius16),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = projectName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
            ) {
                IconButton(
                    onClick = onExportToTxt,
                    modifier = Modifier
                        .background(
                            if (isDarkTheme) {
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF059669), Color(0xFF10B981))
                                )
                            } else {
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF34D399), Color(0xFF6EE7B7))
                                )
                            },
                            shape = RoundedCornerShape(Dimens.CornerRadius12)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = "Export to TXT",
                        tint = Color.White
                    )
                }

                IconButton(
                    onClick = onSwitchTheme,
                    modifier = Modifier
                        .background(
                            if (isDarkTheme) {
                                Brush.linearGradient(
                                    colors = listOf(AppColors.First, AppColors.Second)
                                )
                            } else {
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFFFFB6C1), Color(0xFFDDA0DD))
                                )
                            },
                            shape = RoundedCornerShape(Dimens.CornerRadius12)
                        )
                ) {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "Switch Theme",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun BottomTabBar(
    currentTab: Tab,
    onTabSelected: (Tab) -> Unit,
    hasError: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.PaddingSmall)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.PaddingSmall),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TabBtn(
                text = "EDITOR",
                isSelected = currentTab == Tab.MAIN,
                onClick = { onTabSelected(Tab.MAIN) },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(Dimens.PaddingSmall))

            TabBtn(
                text = "RUN",
                isSelected = currentTab == Tab.RUN,
                onClick = { onTabSelected(Tab.RUN) },
                hasError = hasError,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun TabBtn(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    hasError: Boolean = false,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(Dimens.ButtonHeight)
            .background(
                if (isSelected) {
                    Brush.linearGradient(
                        colors = listOf(AppColors.First, AppColors.Second)
                    )
                } else {
                    Brush.linearGradient(
                        colors = listOf(Color.Transparent, Color.Transparent)
                    )
                },
                shape = RoundedCornerShape(Dimens.CornerRadius12)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = when {
                hasError -> Color.Red
                isSelected -> Color.White
                else -> MaterialTheme.colorScheme.onSurface
            }
        ),
        shape = RoundedCornerShape(Dimens.CornerRadius12)
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = Dimens.TextMedium,
            color = when {
                hasError -> Color.Red
                isSelected -> Color.White
                else -> MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

//прозрачная панель
@Composable
fun Card(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = AppColors.Surface
        ),
        border = BorderStroke(Dimens.Padding1, Color.White.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(Dimens.CornerRadius12)
    ) {
        content()
    }
}

@Composable
fun BlockTemplate(
    text: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var error by remember { mutableStateOf<String?>(null) }

    Card(
        onClick = {
            try {
                onClick()
            } catch (e: Exception) {
                error = e.message
            }
        },
        modifier = modifier.size(Dimens.Size90, Dimens.Size45),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(Dimens.CornerRadius12),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.Size6)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = Dimens.TextSmal,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            error?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = Dimens.TextSmaller,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

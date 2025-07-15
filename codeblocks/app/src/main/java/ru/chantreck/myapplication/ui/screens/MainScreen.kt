package ru.chantreck.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ru.chantreck.myapplication.model.CodeBlockData
import ru.chantreck.myapplication.model.VariableData
import ru.chantreck.myapplication.ui.components.*
import ru.chantreck.myapplication.ui.theme.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Brush
import ru.chantreck.myapplication.model.*

@Composable
fun MainScreen(
    blocks: MutableList<CodeBlockData>,
    onBlockCreate: (CodeBlockData) -> Unit,
    onBlockMove: (String, Float, Float) -> Unit,
    onBlockDrop: (CodeBlockData?) -> Unit,
    onDeleteBlock: (CodeBlockData) -> Unit,
    onEditBlock: (CodeBlockData?) -> Unit,
    onUpdateBlockText: (String, CodeBlockData) -> Unit,
    onConnectBlocks: (CodeBlockData, CodeBlockData) -> Unit,
    variables: MutableMap<String, VariableData>,
    onUpdateVariables: (Map<String, VariableData>) -> Unit,
    onShowError: (String) -> Unit,
    onExportToTxt: () -> Unit,
    onArrayCreated: () -> Unit,
    modifier: Modifier = Modifier
) {
    var editingBlock by remember { mutableStateOf<CodeBlockData?>(null) }
    var showAssignmentDialog by remember { mutableStateOf(false) }

    val colorScheme = MaterialTheme.colorScheme

    Box(modifier = modifier.fillMaxSize().background(colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            BlockToolbar(
                onBlockCreate = onBlockCreate,
                onEqualsClick = { showAssignmentDialog = true },
                onWhileClick = {
                    val (nextX, nextY) = getNextBlockPosition(blocks)
                    onBlockCreate(CodeBlockData(
                        text = "while (condition)",
                        color = AppColors.BlockWhile,
                        offsetX = nextX,
                        offsetY = nextY
                    ))
                    onBlockCreate(CodeBlockData(
                        text = "begin",
                        color = AppColors.BlockControl,
                        offsetX = nextX,
                        offsetY = nextY + 160f
                    ))
                    onBlockCreate(CodeBlockData(
                        text = "end",
                        color = AppColors.BlockControl,
                        offsetX = nextX,
                        offsetY = nextY + 320f
                    ))
                },
                onArrayClick = {
                    val (nextX, nextY) = getNextBlockPosition(blocks)
                    onBlockCreate(CodeBlockData(
                        text = "array",
                        color = AppColors.BlockArray,
                        offsetX = nextX,
                        offsetY = nextY
                    ))
                },
                showIfDialog = {
                    val (nextX, nextY) = getNextBlockPosition(blocks)
                    onBlockCreate(CodeBlockData(
                        text = "if (condition)",
                        color = AppColors.BlockCondition,
                        offsetX = nextX,
                        offsetY = nextY
                    ))
                    onBlockCreate(CodeBlockData(
                        text = "begin",
                        color = AppColors.BlockControl,
                        offsetX = nextX,
                        offsetY = nextY + 160f
                    ))
                    onBlockCreate(CodeBlockData(
                        text = "end",
                        color = AppColors.BlockControl,
                        offsetX = nextX,
                        offsetY = nextY + 320f
                    ))
                },
                showPrintDialog = {
                    val (nextX, nextY) = getNextBlockPosition(blocks)
                    onBlockCreate(CodeBlockData(
                        text = "print()",
                        color = AppColors.BlockPrint,
                        offsetX = nextX,
                        offsetY = nextY
                    ))
                },
                showMultiVarDialog = {
                    val (nextX, nextY) = getNextBlockPosition(blocks)
                    onBlockCreate(CodeBlockData(
                        text = "var",
                        color = AppColors.BlockVariable,
                        offsetX = nextX,
                        offsetY = nextY
                    ))
                },
                showSortDialog = {
                    val (nextX, nextY) = getNextBlockPosition(blocks)
                    onBlockCreate(CodeBlockData(
                        text = "sort",
                        color = AppColors.BlockSort,
                        offsetX = nextX,
                        offsetY = nextY
                    ))
                },
                showArraySetDialog = {
                    val (nextX, nextY) = getNextBlockPosition(blocks)
                    onBlockCreate(CodeBlockData(
                        text = "set[]",
                        color = AppColors.BlockArrayAccess,
                        offsetX = nextX,
                        offsetY = nextY
                    ))
                },
                showArrayGetDialog = {
                    val (nextX, nextY) = getNextBlockPosition(blocks)
                    onBlockCreate(CodeBlockData(
                        text = "get[]",
                        color = AppColors.BlockArrayAccess,
                        offsetX = nextX,
                        offsetY = nextY
                    ))
                },
                showArrayDialog = {
                    val (nextX, nextY) = getNextBlockPosition(blocks)
                    onBlockCreate(CodeBlockData(
                        text = "array",
                        color = AppColors.BlockArray,
                        offsetX = nextX,
                        offsetY = nextY
                    ))
                },
                showWhileDialog = {
                    val (nextX, nextY) = getNextBlockPosition(blocks)
                    onBlockCreate(CodeBlockData(
                        text = "while",
                        color = AppColors.BlockWhile,
                        offsetX = nextX,
                        offsetY = nextY
                    ))
                },
                variables = variables,
                onUpdateVariables = onUpdateVariables,
                onShowError = onShowError,
                onArrayCreated = onArrayCreated,
                blocks = blocks,
                showVarDialog = showAssignmentDialog,
                onDismissAssignmentDialog = { showAssignmentDialog = false }
            )

            Workspace(
                blocks = blocks,
                onBlockMove = onBlockMove,
                onDeleteBlock = onDeleteBlock,
                editingBlock = editingBlock,
                onEditBlock = { editingBlock = it },
                onUpdateBlockText = onUpdateBlockText,
                codeBlocks = blocks,
                onConnectBlocks = onConnectBlocks,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun Workspace(
    blocks: List<CodeBlockData>,
    onBlockMove: (String, Float, Float) -> Unit,
    onDeleteBlock: (CodeBlockData) -> Unit,
    editingBlock: CodeBlockData?,
    onEditBlock: (CodeBlockData?) -> Unit,
    onUpdateBlockText: (String, CodeBlockData) -> Unit,
    codeBlocks: MutableList<CodeBlockData>,
    onConnectBlocks: (CodeBlockData, CodeBlockData) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    val colorScheme = MaterialTheme.colorScheme

    val contentHeight = remember(blocks) {
        if (blocks.isEmpty()) return@remember 2000f

        val maxBlockY = blocks.maxOf { it.mutableOffsetY }
        val maxBlockHeight = Dimens.BlocHeight.value * density.density
        (maxBlockY + maxBlockHeight + 1000f).coerceAtLeast(2000f)
    }

    val contentHeightDp = with(density) { contentHeight.toDp() }

    Box(modifier = modifier.fillMaxWidth().verticalScroll(scrollState).background(colorScheme.background)) {
        Box(modifier = Modifier.fillMaxWidth().height(contentHeightDp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val gridSize = Dimens.Size50.toPx()
                val gridColor = colorScheme.outline.copy(alpha = 0.1f)

                var y = 0f
                while (y < size.height) {
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1f
                    )
                    y += gridSize
                }

                var x = 0f
                while (x < size.width) {
                    drawLine(
                        color = gridColor,
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = 1f
                    )
                    x += gridSize
                }
            }

            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val workspaceWidth = constraints.maxWidth
                val workspaceHeight = constraints.maxHeight

                blocks.forEach { block ->
                    DraggBlock(
                        block = block,
                        workspaceSize = IntSize(width = workspaceWidth, height = workspaceHeight),
                        onPositionChange = { x, y -> onBlockMove(block.id, x, y) },
                        onDelete = { onDeleteBlock(block) },
                        onClick = {
                            val freshBlock = codeBlocks.find { it.id == block.id }
                            if (freshBlock != null) {
                                onEditBlock(freshBlock)
                            }
                        },
                        onConnect = { from, to -> onConnectBlocks(from, to) },
                        codeBlocks = codeBlocks
                    )
                }
            }
        }

        editingBlock?.let { currentEditingBlock ->
            val current = codeBlocks.find { it.id == currentEditingBlock.id }

            if (current == null) {
                onEditBlock(null)
            } else {
                EditBlockDialog(
                    currentText = currentEditingBlock.text,
                    block = currentEditingBlock,
                    onConfirm = { newText, block ->
                        onUpdateBlockText(newText, block)
                        onEditBlock(null)
                    },
                    onDelete = {
                        onEditBlock(null)
                        onDeleteBlock(current)
                    },
                    onDismiss = { onEditBlock(null) }
                )
            }
        }
    }
}

private fun getNextBlockPosition(blocks: List<CodeBlockData>): Pair<Float, Float> {
    if (blocks.isEmpty()) return Pair(100f, 100f)

    val lastBlock = blocks.maxByOrNull { it.mutableOffsetY }
    return if (lastBlock != null) Pair(lastBlock.mutableOffsetX, lastBlock.mutableOffsetY + 100f)
    else Pair(100f, 100f)
}

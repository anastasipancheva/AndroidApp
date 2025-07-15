package ru.chantreck.myapplication.model

import androidx.compose.ui.geometry.Offset
import java.util.UUID

sealed class Block(
    val id: String = UUID.randomUUID().toString(),
    val type: BlockType,
    var position: Offset = Offset.Zero,
    var isValid: Boolean = true,
    var executionState: ExecutionState = ExecutionState.IDLE
) {
    data class VariableDeclaration(
        val variables: List<String>
    ) : Block(UUID.randomUUID().toString(), BlockType.VARIABLE)

    data class Assignment(
        val target: String,
        val expression: String
    ) : Block(UUID.randomUUID().toString(), BlockType.ASSIGNMENT)

    data class Arithmetic(
        val operator: ArithmeticOperator,
        val operands: List<String>
    ) : Block(UUID.randomUUID().toString(), BlockType.ARITHMETIC)
}

enum class BlockType { VARIABLE, ASSIGNMENT, ARITHMETIC }
enum class ArithmeticOperator { ADD, SUB, MUL, DIV, MOD }
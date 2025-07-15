package ru.chantreck.myapplication.model

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import java.util.UUID

data class CodeBlockData(
    val id: String = UUID.randomUUID().toString(),
    var text: String,
    val color: Color,
    var offsetX: Float = 0f,
    var offsetY: Float = 0f,
    val connectedTo: MutableList<String> = mutableListOf(),
    val connectors: List<Connector> = listOf(
        Connector(position = ConnectorPosition.TOP),
        Connector(position = ConnectorPosition.BOTTOM)
    ),
    val isLoopStart: Boolean = false
) {
    var mutableOffsetX by mutableStateOf(offsetX)
    var mutableOffsetY by mutableStateOf(offsetY)
}

data class Connector(
    val position: ConnectorPosition,
    var connectedBlockId: String? = null
)

enum class ConnectorPosition {
    TOP, BOTTOM
}

data class VariableData(
    val name: String,
    val type: String,
    var value: String = when(type) {
        "int" -> "0"
        "float" -> "0.0"
        "boolean" -> "false"
        "array" -> "[]"
        else -> ""
    },
    var arrayValues: MutableList<Int> = mutableListOf()
) {
    fun isArray(): Boolean = type == "array"

    fun getArraySize(): Int = arrayValues.size

    fun setArrayValue(index: Int, newValue: Int) {
        if (index in 0 until arrayValues.size) {
            arrayValues[index] = newValue
            updateValueString()
        }
    }

    fun addToArray(newValue: Int) {
        arrayValues.add(newValue)
        updateValueString()
    }

    fun updateValueString() {
        value = arrayValues.toString()
    }

    fun initializeArray(values: List<Int>) {
        arrayValues.clear()
        arrayValues.addAll(values)
        updateValueString()
    }
}

data class Project(
    val name: String,
    val id: String,
    val createdAt: Long = System.currentTimeMillis()
)

enum class AppScreen {
    WELCOME, MAIN
}

enum class Tab {
    MAIN, RUN
}
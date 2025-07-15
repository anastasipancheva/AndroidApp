package ru.chantreck.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import ru.chantreck.myapplication.model.Block
import ru.chantreck.myapplication.model.BlockType
import ru.chantreck.myapplication.model.ArithmeticOperator
import androidx.compose.runtime.mutableStateListOf

class BlockViewModel : ViewModel() {
    private val _blocks = mutableStateListOf<Block>()
    val blocks: List<Block> get() = _blocks

    fun addBlock(block: Block) {
        _blocks.add(block)
    }

    fun moveBlock(fromIndex: Int, toIndex: Int) {
        if (fromIndex in _blocks.indices && toIndex in _blocks.indices) {
            val item = _blocks.removeAt(fromIndex)
            _blocks.add(toIndex, item)
        }
    }


    internal fun createNewBlock(type: BlockType): Block {
        return when (type) {
            BlockType.VARIABLE -> Block.VariableDeclaration(emptyList())
            BlockType.ASSIGNMENT -> Block.Assignment("", "")
            BlockType.ARITHMETIC -> Block.Arithmetic(ArithmeticOperator.ADD, emptyList())
        }
    }

    fun executeProgram() {
        //результаты выполнения
    }
}
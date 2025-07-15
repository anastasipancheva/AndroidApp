package ru.chantreck.myapplication.interpreter

import android.content.Context
import android.util.Log
import ru.chantreck.myapplication.R
import ru.chantreck.myapplication.model.*

const val TYPE_INT = "int"
const val TYPE_FLOAT = "float"
const val TYPE_BOOL = "boolean"
const val TYPE_STRING = "string"
const val TYPE_ARRAY = "array"

private const val TAG = "Interpreter"  //для отладки


private const val MAX_ITERATIONS = 1000

data class ForLoopData(
    val variableName: String,
    val initialValue: Int,
    val comparisonOperator: String,
    val comparisonValue: Int,
    val increment: String
)


class TypeMismatchException(
    private val context: android.content.Context,
    val varName: String,
    val expectedType: String,
    val actualValue: String
) : Exception(context.getString(R.string.error_type_mismatch, varName, expectedType, actualValue))

class SimpleInterpreter(private val context: android.content.Context) {

    private val maxIterations = MAX_ITERATIONS

    private var debugMode = false  //можно убрать
    private var lastError: String? = null

    private val typeCache = mutableMapOf<String, String>()

    //для отладки
    private var tmpDebugCounter = 0


    //TODO падает
    private fun checkType(value: String, expectedType: String): Boolean {
        typeCache[value]?.let { cachedType ->
            if (cachedType == expectedType) return true
        }

        val result = when (expectedType) {
            TYPE_INT -> {
                try {
                    val num = value.toInt()
                    //проверка ведущих нулей???
                    val isValid = !(value.length > 1 && value[0] == '0')
                    if (isValid) typeCache[value] = TYPE_INT
                    isValid
                } catch (e: Exception) {
                    if (debugMode) Log.e(TAG, "Failed to parse int: $value", e)
                    false
                }
            }
            TYPE_FLOAT -> {
                val isValid = value.toFloatOrNull() != null
                if (isValid) typeCache[value] = TYPE_FLOAT
                isValid
            }
            TYPE_BOOL -> value == "true" || value == "false"
            TYPE_STRING -> true
            else -> {
                Log.w(TAG, "Unknown type: $expectedType")
                false
            }
        }

        //TODO нормальную очистку
        if (typeCache.size > 100) typeCache.clear()

        return result
    }


    private fun areTypesCompatible(value: String, existingType: String): Boolean {
        try {
            typeCache[value]?.let { cachedType ->
                if (cachedType == existingType) return true
            }

            val result = when (existingType) {
                TYPE_INT -> {
                    val isInt = value.toIntOrNull() != null
                    val noLeadingZeros = !(value.length > 1 && value[0] == '0')
                    isInt && noLeadingZeros
                }
                TYPE_FLOAT -> {
                    value.toFloatOrNull() != null || value.toIntOrNull() != null
                }
                TYPE_BOOL -> value == "true" || value == "false"
                TYPE_STRING -> true
                else -> {
                    lastError = "Неизвестный тип: $existingType"
                    false
                }
            }

            //кэшируем результат если он положительный
            if (result) typeCache[value] = existingType

            return result
        } catch (e: Exception) {
            Log.e(TAG, "Type check failed", e)
            return false
        }
    }

    // FIXME работает с циклами??!проверить
    private fun findBlockStructures(blocks: List<CodeBlockData>): List<CodeBlockData> {

        if (blocks.isEmpty()) return emptyList()

        tmpDebugCounter++ //отладка

        //тупо сортировала
        val sortedBlocks = blocks.sortedWith(compareBy<CodeBlockData> { it.mutableOffsetY }.thenBy { it.mutableOffsetX })
        val result = mutableListOf<CodeBlockData>()

        result.add(sortedBlocks[0])

        var i = 1
        while (i < sortedBlocks.size) {
            val block = sortedBlocks[i]

            if (debugMode && tmpDebugCounter % 10 == 0) {
                Log.d(TAG, "Processing block: ${block.text}")
            }

            when {
                block.text.startsWith("if (") -> {
                    result.add(block)
                    val beginIndex = findNextBegin(sortedBlocks, i + 1)
                    val endIndex = findMatchingEnd(sortedBlocks, beginIndex)

                    if (beginIndex != -1 && endIndex != -1) {
                        for (j in beginIndex + 1 until endIndex) {
                            result.add(sortedBlocks[j])
                        }
                        i = endIndex + 1
                    } else {
                        Log.w(TAG, "Invalid if block structure")
                        i++
                    }
                }

                block.text.startsWith("while (") -> {
                    result.add(block)
                    val beginIndex = findNextBegin(sortedBlocks, i + 1)
                    val endIndex = findMatchingEnd(sortedBlocks, beginIndex)

                    if (beginIndex != -1 && endIndex != -1) {
                        for (j in beginIndex + 1 until endIndex) {
                            result.add(sortedBlocks[j])
                        }
                        i = endIndex + 1
                    } else {
                        Log.e(TAG, "While block structure broken")
                        i++
                    }
                }

                block.text.startsWith("for (") -> {
                    result.add(block)
                    val beginIndex = findNextBegin(sortedBlocks, i + 1)
                    val endIndex = findMatchingEnd(sortedBlocks, beginIndex)

                    if (beginIndex != -1 && endIndex != -1) {
                        val blocksToAdd = sortedBlocks.subList(beginIndex + 1, endIndex)
                        result.addAll(blocksToAdd)
                        i = endIndex + 1
                    } else {
                        Log.w(TAG, "For loop structure invalid")
                        i++
                    }
                }

                block.text == "begin" || block.text == "end" -> {
                    result.add(block)
                    i++
                }

                else -> {
                    result.add(block)
                    i++
                }
            }
        }

        if (tmpDebugCounter > 1000) tmpDebugCounter = 0

        return result
    }

    //ищет следующий begin блок
    private fun findNextBegin(blocks: List<CodeBlockData>, startIndex: Int): Int {
        for (i in startIndex until blocks.size) {
            if (blocks[i].text == "begin") return i
        }
        return -1 //не нашли(
    }

    private fun findMatchingEnd(blocks: List<CodeBlockData>, beginIndex: Int): Int {
        if (beginIndex == -1) return -1

        var depth = 1  //для вложенных

        for (i in beginIndex + 1 until blocks.size) {
            when (blocks[i].text) {
                "begin" -> depth++
                "end" -> {
                    depth--
                    if (depth == 0) return i
                }
            }
        }
        return -1
    }

    // TODO переписать без регулярок???
    private fun evaluateCondition(condition: String, variables: Map<String, VariableData>): Boolean {
        try {
            val parts = condition.split(Regex("(==|!=|<|>|<=|>=)"))
            if (parts.size != 2) {
                Log.e(TAG, "Invalid condition format: $condition")
                return false
            }

            val operator = Regex("(==|!=|<|>|<=|>=)").find(condition)?.value
            if (operator == null) {
                Log.e(TAG, "No operator found in condition: $condition")
                return false
            }

            //TODO сделать нормально
            val left = evaluateExpression(parts[0].trim(), variables).toIntOrNull() ?: 0
            val right = evaluateExpression(parts[1].trim(), variables).toIntOrNull() ?: 0


            if (debugMode) {
                tmpDebugCounter++
                if (tmpDebugCounter % 50 == 0) {
                    Log.d(TAG, "Comparing $left $operator $right")
                }
            }

            return when (operator) {
                "==" -> left == right
                "!=" -> left != right
                "<" -> left < right
                ">" -> left > right
                "<=" -> left <= right
                ">=" -> left >= right
                else -> false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to evaluate condition: $condition", e)
            return false
        }
    }

    private fun parseForLoopData(text: String): ForLoopData? {
        try {
            val content = text.substring(4, text.length - 1)
            val parts = content.split(":")

            if (parts.size != 5) {
                Log.e(TAG, "Invalid for loop format: expected 5 parts, got ${parts.size}")
                return null
            }

            return ForLoopData(
                variableName = parts[0].trim(),
                initialValue = parts[1].trim().toInt(),
                comparisonOperator = parts[2].trim(),
                comparisonValue = parts[3].trim().toInt(),
                increment = parts[4].trim()
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse for loop: $text", e)
            return null
        }
    }

    private fun evaluateForCondition(data: ForLoopData, variables: Map<String, VariableData>): Boolean {
        try {
            val currentValue = variables[data.variableName]?.value?.toIntOrNull()
            if (currentValue == null) {
                Log.e(TAG, "Invalid loop variable value: ${variables[data.variableName]?.value}")
                return false
            }

            return when (data.comparisonOperator) {
                "<" -> currentValue < data.comparisonValue
                "<=" -> currentValue <= data.comparisonValue
                ">" -> currentValue > data.comparisonValue
                ">=" -> currentValue >= data.comparisonValue
                "==" -> currentValue == data.comparisonValue
                "!=" -> currentValue != data.comparisonValue
                else -> {
                    Log.e(TAG, "Unknown operator: ${data.comparisonOperator}")
                    false
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "For condition evaluation failed", e)
            return false
        }
    }

    //главная функция
    fun execute(blocks: List<CodeBlockData>, initialVariables: Map<String, VariableData>): String {

        val variables = initialVariables.toMutableMap()
        val output = mutableListOf<String>()
        var errorOccurred = false
        var currentIndex = 0

        val structuredBlocks = findBlockStructures(blocks)

        if (debugMode) {
            Log.d(TAG, "Executing blocks: ${structuredBlocks.map { it.text }}")
        }

        var iterationsCount = 0

        var tmpBlock: CodeBlockData
        var tmpResult: String

        while (currentIndex < structuredBlocks.size && !errorOccurred) {
            if (iterationsCount++ > maxIterations * 2) {
                output.add("Программа выполняется слишком долго!бесконечный цикл?")
                break
            }

            tmpBlock = structuredBlocks[currentIndex]
            try {
                when {
                    tmpBlock.text.contains(" = ") -> {
                        val parts = tmpBlock.text.split(" = ")
                        if (parts.size == 2) {
                            val fullVarName = parts[0].trim()
                            val value = parts[1].trim()
                            val varName = fullVarName.split(" ").last()

                            tmpResult = evaluateExpression(value, variables)
                            val variable = variables[varName]

                            if (variable != null) {
                                if (!areTypesCompatible(tmpResult, variable.type)) {
                                    throw TypeMismatchException(
                                        context,
                                        varName,
                                        variable.type,
                                        tmpResult
                                    )
                                }
                                variable.value = tmpResult
                            } else {
                                val type = when {
                                    tmpResult.toIntOrNull() != null &&
                                            !(tmpResult.length > 1 && tmpResult.startsWith("0")) -> TYPE_INT
                                    tmpResult.toFloatOrNull() != null -> TYPE_FLOAT
                                    tmpResult == "true" || tmpResult == "false" -> TYPE_BOOL
                                    else -> TYPE_STRING
                                }
                                variables[varName] = VariableData(varName, type, tmpResult)
                            }
                        }
                    }

                    tmpBlock.text.startsWith("print(") && tmpBlock.text.endsWith(")") -> {
                        val expression = tmpBlock.text.substring(6, tmpBlock.text.length - 1)
                        tmpResult = evaluateExpression(expression, variables)
                        output.add(tmpResult)
                    }

                    tmpBlock.text.startsWith("int ") && tmpBlock.text.contains("[") && tmpBlock.text.contains("{") -> {
                        val arrayDecl = tmpBlock.text.substringAfter("int ")
                        val arrayName = arrayDecl.substringBefore("[")
                        val elementsStr = tmpBlock.text.substringAfter("{").substringBefore("}")

                        // можно через map?
                        val elements = mutableListOf<Int>()
                        for (element in elementsStr.split(",")) {
                            try {
                                elements.add(element.trim().toInt())
                            } catch (e: Exception) {
                                Log.e(TAG, "Invalid array element: ${element.trim()}")
                                elements.add(0)
                            }
                        }

                        val arrayVar = VariableData(arrayName, TYPE_ARRAY)
                        arrayVar.initializeArray(elements)
                        variables[arrayName] = arrayVar
                    }

                    tmpBlock.text.startsWith("int ") && tmpBlock.text.contains("[") -> {
                        val arrayDecl = tmpBlock.text.substringAfter("int ")
                        val arrayName = arrayDecl.substringBefore("[")
                        val size = arrayDecl.substringAfter("[").substringBefore("]").toIntOrNull() ?: 0

                        if (size <= 0) {
                            Log.w(TAG, "Invalid array size: $size")
                        }

                        val elements = mutableListOf<Int>()
                        for (i in 0 until size) {
                            elements.add(0)
                        }

                        val arrayVar = VariableData(arrayName, TYPE_ARRAY)
                        arrayVar.initializeArray(elements)
                        variables[arrayName] = arrayVar
                    }

                    tmpBlock.text.startsWith("bubbleSort(") -> {
                        val arrayName = tmpBlock.text.substring(11, tmpBlock.text.length - 1)
                        variables[arrayName]?.let { arrayVar ->
                            if (arrayVar.isArray()) {
                                bubbleSort(arrayVar.arrayValues, arrayName)
                                arrayVar.updateValueString()
                                output.add(bubbleSort(arrayVar.arrayValues, arrayName))
                            } else {
                                Log.e(TAG, "$arrayName is not an array")
                            }
                        }
                    }

                    tmpBlock.text.contains("[") && tmpBlock.text.contains("] = ") -> {
                        val arrayName = tmpBlock.text.substringBefore("[")
                        val index = tmpBlock.text.substringAfter("[").substringBefore("]").toIntOrNull() ?: 0
                        val value = tmpBlock.text.substringAfter(" = ").toIntOrNull() ?: 0

                        variables[arrayName]?.let { arrayVar ->
                            if (arrayVar.isArray()) {
                                if (index < arrayVar.getArraySize()) {
                                    arrayVar.arrayValues[index] = value
                                    arrayVar.updateValueString()
                                } else {
                                    Log.e(TAG, "Array index out of bounds: $index >= ${arrayVar.getArraySize()}")
                                }
                            } else {
                                Log.e(TAG, "$arrayName is not an array")
                            }
                        }
                    }

                    tmpBlock.text.startsWith("if (") -> {
                        val condition = tmpBlock.text.substring(4, tmpBlock.text.length - 1)
                        val beginIndex = findNextBegin(blocks, currentIndex + 1)
                        val endIndex = findMatchingEnd(blocks, beginIndex)

                        if (beginIndex != -1 && endIndex != -1) {
                            val conditionResult = evaluateCondition(condition, variables)

                            if (conditionResult) {
                                val innerBlocks = blocks.subList(beginIndex + 1, endIndex)
                                output.addAll(execute(innerBlocks, variables).split("\n"))

                                var nextIndex = endIndex + 1
                                if (nextIndex < blocks.size && blocks[nextIndex].text == "else") {
                                    val elseBeginIndex = findNextBegin(blocks, nextIndex + 1)
                                    val elseEndIndex = findMatchingEnd(blocks, elseBeginIndex)
                                    if (elseBeginIndex != -1 && elseEndIndex != -1) {
                                        currentIndex = elseEndIndex + 1
                                        continue
                                    }
                                }
                            } else {
                                //условие не выполнилось, ищем else
                                var nextIndex = endIndex + 1
                                if (nextIndex < blocks.size && blocks[nextIndex].text == "else") {
                                    val elseBeginIndex = findNextBegin(blocks, nextIndex + 1)
                                    val elseEndIndex = findMatchingEnd(blocks, elseBeginIndex)
                                    if (elseBeginIndex != -1 && elseEndIndex != -1) {
                                        val elseBlocks = blocks.subList(elseBeginIndex + 1, elseEndIndex)
                                        output.addAll(execute(elseBlocks, variables).split("\n"))
                                        currentIndex = elseEndIndex + 1
                                        continue
                                    }
                                }
                            }
                            currentIndex = endIndex + 1
                            continue
                        }
                    }

                    tmpBlock.text.startsWith("while (") -> {
                        val condition = tmpBlock.text.substring(7, tmpBlock.text.length - 1)
                        val beginIndex = findNextBegin(blocks, currentIndex + 1)
                        val endIndex = findMatchingEnd(blocks, beginIndex)

                        if (beginIndex != -1 && endIndex != -1) {
                            var iterations = 0
                            while (evaluateCondition(condition, variables) && iterations < maxIterations) {
                                val innerBlocks = blocks.subList(beginIndex + 1, endIndex)
                                val innerResult = execute(innerBlocks, variables)
                                output.addAll(innerResult.split("\n"))

                                val updatedVariables = variables.toMutableMap()
                                for ((name, variable) in variables) {
                                    if (variable.value != updatedVariables[name]?.value) {
                                        variable.value = updatedVariables[name]?.value ?: variable.value
                                    }
                                }

                                iterations++
                            }

                            if (iterations >= maxIterations) {
                                output.add("Внимание: цикл while прерван после $maxIterations итераций")
                            }

                            currentIndex = endIndex + 1
                            continue
                        }
                    }

                    tmpBlock.text.startsWith("for(") -> {
                        val forData = parseForLoopData(tmpBlock.text)
                        if (forData != null) {
                            val beginIndex = findNextBegin(blocks, currentIndex + 1)
                            val endIndex = findMatchingEnd(blocks, beginIndex)

                            if (beginIndex != -1 && endIndex != -1) {
                                val loopVariables = variables.toMutableMap()

                                val newVar = VariableData(forData.variableName, TYPE_INT, forData.initialValue.toString())
                                loopVariables[forData.variableName] = newVar
                                variables[forData.variableName] = newVar

                                var iterations = 0
                                while (evaluateForCondition(forData, loopVariables) && iterations < maxIterations) {
                                    val innerBlocks = blocks.subList(beginIndex + 1, endIndex)
                                    val innerResult = execute(innerBlocks, loopVariables)
                                    output.addAll(innerResult.split("\n"))

                                    for ((name, variable) in loopVariables) {
                                        variables[name] = variable
                                    }

                                    val varName = forData.variableName
                                    val currentValue = loopVariables[varName]?.value?.toIntOrNull() ?: 0
                                    val newValue = when (forData.increment) {
                                        "++" -> (currentValue + 1).toString()
                                        "--" -> (currentValue - 1).toString()
                                        else -> {
                                            if (forData.increment.startsWith("+")) {
                                                (currentValue + forData.increment.substring(1).toInt()).toString()
                                            } else if (forData.increment.startsWith("-")) {
                                                (currentValue - forData.increment.substring(1).toInt()).toString()
                                            } else {
                                                currentValue.toString()
                                            }
                                        }
                                    }
                                    loopVariables[varName]?.value = newValue
                                    variables[varName]?.value = newValue

                                    iterations++
                                }

                                if (iterations >= maxIterations) {
                                    output.add("Внимание: цикл for прерван после $maxIterations итераций")
                                }

                                currentIndex = endIndex + 1
                                continue
                            }
                        }
                        currentIndex++
                    }

                    tmpBlock.text == "begin" || tmpBlock.text == "end" -> {
                        currentIndex++
                        continue
                    }

                    tmpBlock.text == "else" -> {
                        val beginIndex = findNextBegin(blocks, currentIndex + 1)
                        val endIndex = findMatchingEnd(blocks, beginIndex)
                        if (beginIndex != -1 && endIndex != -1) {
                            currentIndex = endIndex + 1
                            continue
                        }
                        currentIndex++
                        continue
                    }
                }
            } catch (e: TypeMismatchException) {
                output.add("Ошибка: ${e.message}")
                errorOccurred = true
                Log.e(TAG, "Type mismatch error", e)
            } catch (e: Exception) {
                output.add("Что-то пошло не так: ${e.message}")
                errorOccurred = true
                Log.e(TAG, "Execution error", e)
            }
            currentIndex++

            if (currentIndex % 100 == 0) {
                typeCache.clear()
                if (tmpDebugCounter > 1000) tmpDebugCounter = 0
            }
        }

        return if (output.isNotEmpty()) {
            output.joinToString("\n")
        } else {
            "Выполнение завершено успешно!"
        }
    }

    private fun infixToPostfix(tokens: List<String>): List<String> {
        val output = mutableListOf<String>()
        val stack = ArrayDeque<String>()

        //приоритеты операторов
        val precedence = mapOf(
            "+" to 1,
            "-" to 1,
            "*" to 2,
            "/" to 2,
            "%" to 2,
            "(" to 0,
            ")" to 0
        )

        for (token in tokens) {
            when {
                token == "(" -> stack.addFirst(token)
                token == ")" -> {
                    while (stack.isNotEmpty() && stack.first() != "(") {
                        output.add(stack.removeFirst())
                    }
                    if (stack.isNotEmpty()) stack.removeFirst()
                }
                precedence.containsKey(token) -> {
                    while (stack.isNotEmpty() && precedence[stack.first()]!! >= precedence[token]!!) {
                        output.add(stack.removeFirst())
                    }
                    stack.addFirst(token)
                }
                else -> output.add(token)
            }
        }

        while (stack.isNotEmpty()) output.add(stack.removeFirst())
        return output
    }

    private fun evaluatePostfix(postfix: List<String>, variables: Map<String, VariableData>): Any? {
        val stack = ArrayDeque<Any>()

        for (token in postfix) {
            try {
                when {
                    //число
                    token.matches(Regex("-?\\d+(\\.\\d+)?")) -> {
                        stack.addFirst(
                            if (token.contains('.')) token.toFloat()
                            else token.toInt()
                        )
                    }
                    //строка
                    token.startsWith("\"") && token.endsWith("\"") -> {
                        stack.addFirst(token.substring(1, token.length - 1))
                    }
                    //булево
                    token == "true" -> stack.addFirst(true)
                    token == "false" -> stack.addFirst(false)
                    //массив
                    token.matches(Regex("[a-zA-Z_][a-zA-Z0-9_]*\\[\\d+]")) -> {
                        val arrayName = token.substringBefore("[")
                        val indexStr = token.substringAfter("[").substringBefore("]")
                        val index = indexStr.toIntOrNull() ?: 0
                        val array = variables[arrayName]
                        if (array != null && array.isArray() && index < array.getArraySize()) {
                            stack.addFirst(array.arrayValues[index])
                        } else {
                            Log.e(TAG, "Invalid array access: $token")
                            return null
                        }
                    }
                    //var
                    token.matches(Regex("[a-zA-Z_][a-zA-Z0-9_]*")) -> {
                        val value = variables[token]?.value
                        value?.let {
                            stack.addFirst(
                                it.toIntOrNull() ?: it.toFloatOrNull() ?: it
                            )
                        } ?: run {
                            Log.e(TAG, "Variable not found: $token")
                            return null
                        }
                    }
                    //оператор
                    else -> {
                        if (stack.size < 2) {
                            Log.e(TAG, "Not enough operands for $token")
                            return null
                        }

                        val b = stack.removeFirst()
                        val a = stack.removeFirst()

                        val result = when (token) {
                            "+" -> when {
                                a is Number && b is Number -> a.toFloat() + b.toFloat()
                                else -> "$a$b"
                            }
                            "-" -> (a as Number).toFloat() - (b as Number).toFloat()
                            "*" -> (a as Number).toFloat() * (b as Number).toFloat()
                            "/" -> {
                                if ((b as Number).toFloat() != 0f) {
                                    (a as Number).toFloat() / b.toFloat()
                                } else {
                                    Log.e(TAG, "Division by zero!")
                                    return null
                                }
                            }
                            "%" -> {
                                if ((b as Number).toInt() != 0) {
                                    (a as Number).toInt() % b.toInt()
                                } else {
                                    Log.e(TAG, "Modulo by zero!")
                                    return null
                                }
                            }
                            else -> {
                                Log.e(TAG, "Unknown operator: $token")
                                return null
                            }
                        }
                        stack.addFirst(result)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error evaluating token: $token", e)
                return null
            }
        }
        return if (stack.size == 1) stack.first() else null
    }

    private fun evaluateExpression(expression: String, variables: Map<String, VariableData>): String {
        try {
            val tokens = tokenize(expression)
            if (tokens.isEmpty()) return "0"

            val postfix = infixToPostfix(tokens)
            val result = evaluatePostfix(postfix, variables)

            return when (result) {
                is Float -> {
                    if (result == result.toInt().toFloat()) {
                        result.toInt().toString()
                    } else {
                        result.toString()
                    }
                }
                null -> throw TypeMismatchException(
                    context,
                    expression,
                    "valid value",
                    "null or invalid expression"
                )
                else -> result.toString()
            }
        } catch (e: TypeMismatchException) {
            throw TypeMismatchException(
                context,
                e.varName,
                e.expectedType,
                e.actualValue
            )
        } catch (e: Exception) {
            Log.e(TAG, "Expression evaluation failed: $expression", e)
            throw e
        }
    }

    private fun tokenize(expression: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0

        while (i < expression.length) {
            when {
                expression[i].isDigit() || expression[i] == '.' ||
                        (expression[i] == '-' && (i == 0 || listOf('(', '+', '-', '*', '/', '%').contains(expression[i - 1]))) -> {
                    val number = StringBuilder()

                    if (expression[i] == '-') {
                        number.append('-')
                        i++
                    }

                    var hasDot = false
                    while (i < expression.length && (expression[i].isDigit() || expression[i] == '.')) {
                        if (expression[i] == '.') {
                            if (hasDot) break
                            hasDot = true
                        }
                        number.append(expression[i])
                        i++
                    }

                    tokens.add(number.toString())
                }

                //строка в кавычках
                expression[i] == '"' -> {
                    val str = StringBuilder()
                    i++
                    while (i < expression.length && expression[i] != '"') {
                        str.append(expression[i])
                        i++
                    }
                    i++
                    tokens.add("\"${str}\"")
                }

                expression[i] in listOf('+', '-', '*', '/', '%', '(', ')') -> {
                    tokens.add(expression[i].toString())
                    i++
                }

                expression[i].isLetter() -> {
                    val variable = StringBuilder()
                    while (i < expression.length && (expression[i].isLetterOrDigit() || expression[i] == '_' || expression[i] == '[' || expression[i] == ']')) {
                        variable.append(expression[i])
                        i++
                    }
                    tokens.add(variable.toString())
                }

                //пробелы пропускаем
                expression[i].isWhitespace() -> i++

                else -> {
                    Log.e(TAG, "Invalid character in expression: ${expression[i]}")
                    return emptyList()
                }
            }
        }

        return tokens
    }

    private fun bubbleSort(array: MutableList<Int>, arrayName: String): String {
        val n = array.size
        var swapped: Boolean

        for (i in 0 until n - 1) {
            swapped = false
            for (j in 0 until n - i - 1) {
                if (array[j] > array[j + 1]) {
                    val temp = array[j]
                    array[j] = array[j + 1]
                    array[j + 1] = temp
                    swapped = true
                }
            }
            if (!swapped) break
        }

        return "Массив $arrayName отсортирован: ${array}"
    }
}
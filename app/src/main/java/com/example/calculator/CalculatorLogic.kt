package com.example.calculator

enum class Operation {
    ADD, SUBTRACT, MULTIPLY, DIVIDE, NONE
}

fun calculate(first: Double, second: Double, operation: Operation): Double {
    return when (operation) {
        Operation.ADD -> first + second
        Operation.SUBTRACT -> first - second
        Operation.MULTIPLY -> first * second
        Operation.DIVIDE -> if (second != 0.0) first / second else Double.NaN
        Operation.NONE -> second
    }
}

fun formatResult(value: Double): String {
    if (value.isNaN()) return "Error"
    if (value.isInfinite()) return "Error"

    return if (value == value.toLong().toDouble() && kotlin.math.abs(value) < 1e15) {
        value.toLong().toString()
    } else {
        // Ограничиваем количество знаков, убираем лишние нули
        val formatted = "%.8f".format(value).trimEnd('0').trimEnd('.')
        formatted
    }
}

fun formatDisplayNumber(raw: String): String {
    // Разделяем на целую и дробную часть для форматирования с разделителями тысяч
    val isNegative = raw.startsWith("-")
    val cleanRaw = if (isNegative) raw.substring(1) else raw
    val parts = cleanRaw.split(".")
    val integerPart = parts[0].toLongOrNull()?.let {
        String.format("%,d", it)
    } ?: parts[0]

    val result = if (parts.size > 1) {
        "$integerPart.${parts[1]}"
    } else {
        integerPart
    }

    return if (isNegative) "-$result" else result
}

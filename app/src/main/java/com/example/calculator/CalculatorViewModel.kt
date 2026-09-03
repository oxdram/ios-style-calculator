package com.example.calculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class CalculatorViewModel : ViewModel() {

    var displayValue by mutableStateOf("0")
        private set

    var activeOperation by mutableStateOf(Operation.NONE)
        private set

    private var firstOperand: Double? = null
    private var pendingOperation: Operation = Operation.NONE
    private var shouldResetDisplay = false

    fun onDigit(digit: String) {
        if (shouldResetDisplay || displayValue == "0") {
            displayValue = digit
            shouldResetDisplay = false
        } else {
            if (displayValue.replace("-", "").replace(".", "").length < 12) {
                displayValue += digit
            }
        }
    }

    fun onDecimalPoint() {
        if (shouldResetDisplay) {
            displayValue = "0."
            shouldResetDisplay = false
            return
        }
        if (!displayValue.contains(".")) {
            displayValue += "."
        }
    }

    fun onOperation(operation: Operation) {
        val current = displayValue.toDoubleOrNull() ?: 0.0

        if (firstOperand != null && pendingOperation != Operation.NONE && !shouldResetDisplay) {
            val result = calculate(firstOperand!!, current, pendingOperation)
            displayValue = formatResult(result)
            firstOperand = result
        } else {
            firstOperand = current
        }

        pendingOperation = operation
        activeOperation = operation
        shouldResetDisplay = true
    }

    fun onEquals() {
        val current = displayValue.toDoubleOrNull() ?: 0.0
        if (firstOperand != null && pendingOperation != Operation.NONE) {
            val result = calculate(firstOperand!!, current, pendingOperation)
            displayValue = formatResult(result)
            firstOperand = null
            pendingOperation = Operation.NONE
            activeOperation = Operation.NONE
            shouldResetDisplay = true
        }
    }

    fun onClear() {
        displayValue = "0"
        firstOperand = null
        pendingOperation = Operation.NONE
        activeOperation = Operation.NONE
        shouldResetDisplay = false
    }

    fun onToggleSign() {
        displayValue = if (displayValue.startsWith("-")) {
            displayValue.substring(1)
        } else if (displayValue != "0") {
            "-$displayValue"
        } else {
            displayValue
        }
    }

    fun onPercent() {
        val current = displayValue.toDoubleOrNull() ?: 0.0
        displayValue = formatResult(current / 100.0)
    }

    fun isAllClear(): Boolean = displayValue == "0" && firstOperand == null
}

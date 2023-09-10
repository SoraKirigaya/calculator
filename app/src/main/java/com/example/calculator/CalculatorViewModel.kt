package com.example.calculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.text.NumberFormat
import java.util.Locale

class CalculatorViewModel : ViewModel() {
    var state by mutableStateOf(CalculatorState())
        private set

    private var number1: String = ""
    private var number2: String = ""
    private var didCalculation = false

    fun onAction(action: CalculatorActions) {
        when (action) {
            is CalculatorActions.Number -> enterNumber(action.number)
            is CalculatorActions.Decimal -> enterDecimal()
            is CalculatorActions.Clear -> performClear()
            is CalculatorActions.Operation -> enterOperation(action.operation)
            is CalculatorActions.Calculate -> performCalculation()
            is CalculatorActions.Delete -> performDeletion()
        }
    }

    private fun performClear() {
        number1 = ""
        number2 = ""
        state = CalculatorState()
    }

    private fun formatNumber(number: Double): String {
        val numberFormat = NumberFormat.getNumberInstance(Locale.US)
        return numberFormat.format(number)
    }

    private fun performCalculation() {
        val numberCalc1 = number1.toDoubleOrNull()
        val numberCalc2 = number2.toDoubleOrNull()
        if (numberCalc1 != null && numberCalc2 != null) {
            val total = when (state.operation) {
                is CalculatorOperation.Add -> numberCalc1 + numberCalc2
                is CalculatorOperation.Subtract -> numberCalc1 - numberCalc2
                is CalculatorOperation.Multiply -> numberCalc1 * numberCalc2
                is CalculatorOperation.Divide -> numberCalc1 / numberCalc2
                null -> return
            }
            state = if (total == total.toInt().toDouble()) {
                state.copy(result = formatNumber(total).take(15), operation = null)

            } else {
                state.copy(result = formatNumber(total).take(15), operation = null)
            }
            number1 = total.toString()
            number2 = ""
            didCalculation = true
        }
    }

    private fun performDeletion() {
        if (didCalculation) {
            return
        } else {
            if (number2.isNotBlank() && number1.isNotBlank()) {
                number2 = number2.dropLast(1)
                if(number2 == ""){
                    number2 = "0"
                }
                state = state.copy(result = number2)
            }
            if (number2.isBlank() && state.operation != null) {
                return
            } else if (number1.isNotBlank() && number2.isBlank()) {
                number1 = number1.dropLast(1)
                if(number1 == ""){
                    number1 = "0"
                }
                state = state.copy(result = number1)
            }
        }
    }

    private fun enterOperation(operation: CalculatorOperation) {
        if (number1.isNotBlank() && number2.isBlank()) {
            state = state.copy(operation = operation)
        } else if (number1.isNotBlank() && number2.isNotBlank()) {
            performCalculation()
            state = state.copy(operation = operation)
        }
    }

    private fun enterDecimal() {
        if (state.operation == null && !number1.contains(".") && number1.isNotBlank()) {
            number1 += "."
            state = state.copy(result = number1)
        }
        if (!number2.contains(".") && number2.isNotBlank()) {
            number2 += "."
            state = state.copy(result = number2)
        }
    }

    private fun enterNumber(number: Int) {
        //Check if user did calculation: if yes, user starts from  the first number
        if (didCalculation && state.operation == null) {
            resetNumber()
            didCalculation = false
        }
        if (state.operation == null) {
            if (number1.length >= MAX_NUM_LENGTH) {
                return
            }
            if (number1 == "0") {
                number1 = number.toString()
                state = state.copy(result = formatNumber(number1.toDouble()))
            } else {
                number1 += number
                state = state.copy(result = formatNumber(number1.toDouble()))
                didCalculation = false
            }
        } else if (state.operation != null) {
            if (number2.length >= MAX_NUM_LENGTH) {
                return
            }
            if (number2 == "0") {
                number2 = number.toString()
                state = state.copy(result = formatNumber(number2.toDouble()))
            } else {
                number2 += number
                state = state.copy(result = formatNumber(number2.toDouble()))
                didCalculation = false
            }
        }
    }

    private fun resetNumber() {
        number1 = ""
        number2 = ""
        state = state.copy(operation = null)
    }

    companion object {
        private const val MAX_NUM_LENGTH = 8
    }
}
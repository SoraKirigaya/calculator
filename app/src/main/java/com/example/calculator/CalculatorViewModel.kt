package com.example.calculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class CalculatorViewModel : ViewModel() {
    var state by mutableStateOf(CalculatorState())
        private set

    private var number1 : String= ""
    private var number2 : String= ""

    fun onAction(action: CalculatorActions) {
        when (action) {
            is CalculatorActions.Number -> enterNumber(action.number)
            is CalculatorActions.Decimal -> enterDecimal()
            is CalculatorActions.Clear -> state = CalculatorState()
            is CalculatorActions.Operation -> enterOperation(action.operation)
            is CalculatorActions.Calculate -> performCalculation()
            is CalculatorActions.Delete -> performDeletion()
        }
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
            state = if (total == total.toInt().toDouble()){
                state.copy(result = total.toInt().toString().take(15), operation = null)

            } else{
                state.copy(result = total.toString().take(15), operation = null)
            }
            number1 = total.toString()
            number2 = ""
        }
    }

    private fun performDeletion() {
        when {
            number2.isNotBlank() -> state = state.copy(
                result = number2.dropLast(1)
            )

            state.operation != null -> state = state.copy(
                operation = null
            )

            number1.isNotBlank() -> state = state.copy(
                result = number1.dropLast(1)
            )
        }
    }

    private fun enterOperation(operation: CalculatorOperation) {
        if (number1.isNotBlank() && number2.isBlank()) {
            state = state.copy(operation = operation)
        } else if(number1.isNotBlank() && number2.isNotBlank()) {
            performCalculation()
            state = state.copy(operation = operation)
        }
    }

    private fun enterDecimal() {
        if (state.operation == null && !number1.contains(".") && number1.isNotBlank()) {
            state = state.copy(result = number1 + ".")
            return
        }
        if (!number2.contains(".") && number2.isNotBlank()) {
            state = state.copy(result = number2 + ".")
        }
    }

    private fun enterNumber(number: Int) {
        if (state.operation == null) {
            if (number1.length >= MAX_NUM_LENGTH) {
                return
            }
            number1 += number
            state = state.copy(result = number1)
            return
        } else {
            if (number2.length >= MAX_NUM_LENGTH) {
                return
            }
        }
        number2 += number
        state = state.copy(result = number2)
    }

    companion object {
        private const val MAX_NUM_LENGTH = 8
    }
}
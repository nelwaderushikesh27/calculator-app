package com.example.calculator.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CalculatorViewModel : ViewModel() {

    private val _expression = MutableLiveData("0")
    val expression: LiveData<String> = _expression

    private val _result = MutableLiveData("0")
    val result: LiveData<String> = _result

    private var currentExpression = "0"
    private var shouldResetOnNextNumber = false

    fun onNumberClick(number: String) {
        if (shouldResetOnNextNumber) {
            currentExpression = number
            shouldResetOnNextNumber = false
        } else {
            if (currentExpression == "0") {
                currentExpression = number
            } else {
                currentExpression += number
            }
        }
        _expression.value = currentExpression
        calculateResult()
    }

    fun onOperatorClick(operator: String) {
        if (currentExpression.isNotEmpty()) {
            val lastChar = currentExpression.last()
            if (lastChar.isOperator()) {
                currentExpression = currentExpression.dropLast(1) + operator
            } else {
                currentExpression += operator
            }
            _expression.value = currentExpression
            shouldResetOnNextNumber = false
        }
    }

    fun onEqualsClick() {
        try {
            val result = evaluateExpression(currentExpression)
            _result.value = formatResult(result)
            currentExpression = formatResult(result)
            shouldResetOnNextNumber = true
        } catch (e: Exception) {
            _result.value = "Error"
        }
    }

    fun onClearClick() {
        currentExpression = "0"
        _expression.value = "0"
        _result.value = "0"
        shouldResetOnNextNumber = false
    }

    fun onBackspaceClick() {
        if (currentExpression.length > 1) {
            currentExpression = currentExpression.dropLast(1)
        } else {
            currentExpression = "0"
        }
        _expression.value = currentExpression
        calculateResult()
    }

    fun onPercentClick() {
        try {
            val number = currentExpression.toDouble()
            val percentResult = number / 100
            currentExpression = formatResult(percentResult)
            _expression.value = currentExpression
            _result.value = currentExpression
        } catch (e: Exception) {
            _result.value = "Error"
        }
    }

    fun onDotClick() {
        if (shouldResetOnNextNumber) {
            currentExpression = "0."
            shouldResetOnNextNumber = false
        } else if (!currentExpression.contains(".")) {
            currentExpression += "."
        }
        _expression.value = currentExpression
    }

    private fun calculateResult() {
        try {
            val result = evaluateExpression(currentExpression)
            _result.value = formatResult(result)
        } catch (e: Exception) {
            // Don't show error while typing
        }
    }

    private fun evaluateExpression(expression: String): Double {
        return object : Any() {
            var pos = -1
            var ch = 0

            fun nextChar() {
                ch = if (++pos < expression.length) expression[pos].code else -1
            }

            fun eat(charToEat: Int): Boolean {
                while (ch == ' '.code) nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    when {
                        eat('+'.code) -> x += parseTerm()
                        eat('-'.code) -> x -= parseTerm()
                        else -> return x
                    }
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    when {
                        eat('×'.code) -> x *= parseFactor()
                        eat('÷'.code) -> x /= parseFactor()
                        else -> return x
                    }
                }
            }

            fun parseFactor(): Double {
                if (eat('+'.code)) return parseFactor()
                if (eat('-'.code)) return -parseFactor()

                var x: Double
                val startPos = pos
                if (eat('('.code)) {
                    x = parseExpression()
                    eat(')'.code)
                } else if (ch in '0'.code..'9'.code || ch == '.'.code) {
                    while (ch in '0'.code..'9'.code || ch == '.'.code) nextChar()
                    x = expression.substring(startPos, pos).toDouble()
                } else {
                    throw RuntimeException("Unexpected: ${ch.toChar()}")
                }

                return x
            }

            fun evaluate(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < expression.length) throw RuntimeException("Unexpected: ${ch.toChar()}")
                return x
            }
        }.evaluate()
    }

    private fun formatResult(result: Double): String {
        return if (result == result.toLong().toDouble()) {
            result.toLong().toString()
        } else {
            result.toString()
        }
    }

    private fun Char.isOperator(): Boolean {
        return this == '+' || this == '-' || this == '×' || this == '÷'
    }
}

package com.example.calculator.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.calculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: CalculatorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[CalculatorViewModel::class.java]

        setupClickListeners()
        observeData()
    }

    private fun setupClickListeners() {
        val numberButtons = listOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3,
            binding.btn4, binding.btn5, binding.btn6, binding.btn7,
            binding.btn8, binding.btn9
        )

        numberButtons.forEach { button ->
            button.setOnClickListener {
                viewModel.onNumberClick(button.text.toString())
            }
        }

        binding.btnPlus.setOnClickListener { viewModel.onOperatorClick("+") }
        binding.btnMinus.setOnClickListener { viewModel.onOperatorClick("-") }
        binding.btnMultiply.setOnClickListener { viewModel.onOperatorClick("×") }
        binding.btnDivide.setOnClickListener { viewModel.onOperatorClick("÷") }
        binding.btnEquals.setOnClickListener { viewModel.onEqualsClick() }
        binding.btnClear.setOnClickListener { viewModel.onClearClick() }
        binding.btnBackspace.setOnClickListener { viewModel.onBackspaceClick() }
        binding.btnPercent.setOnClickListener { viewModel.onPercentClick() }
        binding.btnDot.setOnClickListener { viewModel.onDotClick() }
    }

    private fun observeData() {
        viewModel.expression.observe(this) { expression ->
            binding.tvExpression.text = expression
        }

        viewModel.result.observe(this) { result ->
            binding.tvResult.text = result
        }
    }
}

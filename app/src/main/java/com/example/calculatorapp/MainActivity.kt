package com.example.calculatorapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.*

class MainActivity : AppCompatActivity() {

    // Variables to store calculator state
    private lateinit var tvDisplay: TextView
    private lateinit var tvResult: TextView
    private var currentInput = ""
    private var firstNumber = 0.0
    private var operator = ""
    private var isNewInput = false
    private var powerBase = 0.0
    private var waitingForPower = false
    private var permN = 0.0
    private var waitingForPermR = false
    private var combN = 0.0
    private var waitingForCombR = false

    // ─── LIFECYCLE METHODS ───────────────────────────────────────────────────

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("Lifecycle", "onCreate called")

        // Link display screens
        tvDisplay = findViewById(R.id.tvDisplay)
        tvResult = findViewById(R.id.tvResult)

        setupButtons()
    }

    override fun onStart() {
        super.onStart()
        Log.d("Lifecycle", "onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Lifecycle", "onResume called")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Lifecycle", "onPause called")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Lifecycle", "onStop called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Lifecycle", "onDestroy called")
    }

    // ─── BUTTON SETUP ────────────────────────────────────────────────────────

    private fun setupButtons() {

        // Number buttons
        val numberButtons = mapOf(
            R.id.btn0 to "0", R.id.btn1 to "1", R.id.btn2 to "2",
            R.id.btn3 to "3", R.id.btn4 to "4", R.id.btn5 to "5",
            R.id.btn6 to "6", R.id.btn7 to "7", R.id.btn8 to "8",
            R.id.btn9 to "9"
        )
        for ((id, num) in numberButtons) {
            findViewById<Button>(id).setOnClickListener { appendNumber(num) }
        }

        // Decimal point
        findViewById<Button>(R.id.btnDot).setOnClickListener {
            if (!currentInput.contains(".")) appendNumber(".")
        }

        // Basic operators
        findViewById<Button>(R.id.btnAdd).setOnClickListener { setOperator("+") }
        findViewById<Button>(R.id.btnSubtract).setOnClickListener { setOperator("-") }
        findViewById<Button>(R.id.btnMultiply).setOnClickListener { setOperator("×") }
        findViewById<Button>(R.id.btnDivide).setOnClickListener { setOperator("÷") }

        // Clear, sign, percent, equals
        findViewById<Button>(R.id.btnClear).setOnClickListener { clearAll() }
        findViewById<Button>(R.id.btnSign).setOnClickListener { toggleSign() }
        findViewById<Button>(R.id.btnPercent).setOnClickListener { applyPercent() }
        findViewById<Button>(R.id.btnEquals).setOnClickListener { calculateResult() }

        // Scientific buttons
        findViewById<Button>(R.id.btnSin).setOnClickListener { applyScientific("sin") }
        findViewById<Button>(R.id.btnCos).setOnClickListener { applyScientific("cos") }
        findViewById<Button>(R.id.btnTan).setOnClickListener { applyScientific("tan") }
        findViewById<Button>(R.id.btnSinh).setOnClickListener { applyScientific("sinh") }
        findViewById<Button>(R.id.btnCosh).setOnClickListener { applyScientific("cosh") }
        findViewById<Button>(R.id.btnTanh).setOnClickListener { applyScientific("tanh") }
        findViewById<Button>(R.id.btnLog).setOnClickListener { applyScientific("log") }
        findViewById<Button>(R.id.btnLn).setOnClickListener { applyScientific("ln") }
        findViewById<Button>(R.id.btnSqrt).setOnClickListener { applyScientific("sqrt") }
        findViewById<Button>(R.id.btnFactorial).setOnClickListener { applyScientific("factorial") }
        findViewById<Button>(R.id.btnAbs).setOnClickListener { applyScientific("abs") }
        findViewById<Button>(R.id.btnInverse).setOnClickListener { applyScientific("inverse") }
        findViewById<Button>(R.id.btnPi).setOnClickListener {
            currentInput = Math.PI.toString()
            tvDisplay.text = "π"
            tvResult.text = ""
        }

        // Power
        findViewById<Button>(R.id.btnPow).setOnClickListener {
            val num = currentInput.toDoubleOrNull()
            if (num != null) {
                powerBase = num
                waitingForPower = true
                tvResult.text = "$currentInput ^"
                currentInput = ""
            }
        }

        // Permutation nPr
        findViewById<Button>(R.id.btnPerm).setOnClickListener {
            val num = currentInput.toDoubleOrNull()
            if (num != null) {
                permN = num
                waitingForPermR = true
                tvResult.text = "${currentInput.toInt()} P"
                currentInput = ""
            }
        }

        // Combination nCr
        findViewById<Button>(R.id.btnComb).setOnClickListener {
            val num = currentInput.toDoubleOrNull()
            if (num != null) {
                combN = num
                waitingForCombR = true
                tvResult.text = "${currentInput.toInt()} C"
                currentInput = ""
            }
        }
    }

    // ─── CALCULATOR LOGIC ────────────────────────────────────────────────────

    private fun appendNumber(num: String) {
        if (isNewInput) {
            currentInput = ""
            isNewInput = false
        }
        currentInput += num
        tvDisplay.text = currentInput
    }

    private fun setOperator(op: String) {
        val num = currentInput.toDoubleOrNull() ?: return
        firstNumber = num
        operator = op
        isNewInput = true
        tvResult.text = "$currentInput $op"
    }

    private fun clearAll() {
        currentInput = ""
        firstNumber = 0.0
        operator = ""
        isNewInput = false
        waitingForPower = false
        waitingForPermR = false
        waitingForCombR = false
        tvDisplay.text = "0"
        tvResult.text = ""
    }

    private fun toggleSign() {
        val num = currentInput.toDoubleOrNull() ?: return
        currentInput = formatResult(-num)
        tvDisplay.text = currentInput
    }

    private fun applyPercent() {
        val num = currentInput.toDoubleOrNull() ?: return
        currentInput = formatResult(num / 100)
        tvDisplay.text = currentInput
    }

    private fun calculateResult() {
        val secondNumber = currentInput.toDoubleOrNull() ?: return

        // Handle power
        if (waitingForPower) {
            val result = powerBase.pow(secondNumber)
            tvDisplay.text = formatResult(result)
            tvResult.text = "$powerBase ^ $secondNumber ="
            currentInput = formatResult(result)
            waitingForPower = false
            isNewInput = true
            return
        }

        // Handle permutation
        if (waitingForPermR) {
            val n = permN.toInt()
            val r = secondNumber.toInt()
            val result = permutation(n, r)
            tvDisplay.text = result.toString()
            tvResult.text = "${n}P${r} ="
            currentInput = result.toString()
            waitingForPermR = false
            isNewInput = true
            return
        }

        // Handle combination
        if (waitingForCombR) {
            val n = combN.toInt()
            val r = secondNumber.toInt()
            val result = combination(n, r)
            tvDisplay.text = result.toString()
            tvResult.text = "${n}C${r} ="
            currentInput = result.toString()
            waitingForCombR = false
            isNewInput = true
            return
        }

        // Basic operations
        if (operator.isEmpty()) return
        val result = when (operator) {
            "+" -> firstNumber + secondNumber
            "-" -> firstNumber - secondNumber
            "×" -> firstNumber * secondNumber
            "÷" -> if (secondNumber != 0.0) firstNumber / secondNumber else Double.NaN
            else -> return
        }

        tvResult.text = "$firstNumber $operator $secondNumber ="
        tvDisplay.text = if (result.isNaN()) "Error" else formatResult(result)
        currentInput = if (result.isNaN()) "" else formatResult(result)
        operator = ""
        isNewInput = true
    }

    private fun applyScientific(func: String) {
        val num = currentInput.toDoubleOrNull() ?: return
        val result = when (func) {
            "sin"      -> sin(Math.toRadians(num))
            "cos"      -> cos(Math.toRadians(num))
            "tan"      -> tan(Math.toRadians(num))
            "sinh"     -> sinh(num)
            "cosh"     -> cosh(num)
            "tanh"     -> tanh(num)
            "log"      -> if (num > 0) log10(num) else Double.NaN
            "ln"       -> if (num > 0) ln(num) else Double.NaN
            "sqrt"     -> if (num >= 0) sqrt(num) else Double.NaN
            "factorial"-> factorial(num.toInt()).toDouble()
            "abs"      -> abs(num)
            "inverse"  -> if (num != 0.0) 1 / num else Double.NaN
            else       -> return
        }
        tvResult.text = "$func($num) ="
        tvDisplay.text = if (result.isNaN()) "Error" else formatResult(result)
        currentInput = if (result.isNaN()) "" else formatResult(result)
        isNewInput = true
    }

    // ─── HELPER FUNCTIONS ────────────────────────────────────────────────────

    private fun factorial(n: Int): Long {
        if (n < 0) return -1
        var result = 1L
        for (i in 2..n) result *= i
        return result
    }

    private fun permutation(n: Int, r: Int): Long {
        if (r > n) return 0
        return factorial(n) / factorial(n - r)
    }

    private fun combination(n: Int, r: Int): Long {
        if (r > n) return 0
        return factorial(n) / (factorial(r) * factorial(n - r))
    }

    private fun formatResult(value: Double): String {
        return if (value == value.toLong().toDouble())
            value.toLong().toString()
        else
            value.toString()
    }
}
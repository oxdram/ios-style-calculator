package com.example.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.gestures.detectTapGestures
import com.example.calculator.ui.theme.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorTheme {
                Surface(color = CalcBackground) {
                    CalculatorScreen()
                }
            }
        }
    }
}

@Composable
fun CalculatorScreen(viewModel: CalculatorViewModel = viewModels<CalculatorViewModel>().let {
    // Fallback for preview / simple instantiation
    androidx.lifecycle.viewmodel.compose.viewModel()
}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CalcBackground)
            .padding(horizontal = 12.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        // Display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.BottomEnd
        ) {
            Text(
                text = formatDisplayNumber(viewModel.displayValue),
                color = TextWhite,
                fontSize = if (viewModel.displayValue.length > 8) 64.sp else 96.sp,
                fontWeight = FontWeight.Light,
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        val clearLabel = if (viewModel.isAllClear()) "AC" else "C"

        // Row 1
        ButtonRow {
            CalcButton(
                text = clearLabel,
                type = ButtonType.FUNCTION,
                modifier = Modifier.weight(1f)
            ) { viewModel.onClear() }

            CalcButton(
                text = "+/-",
                type = ButtonType.FUNCTION,
                modifier = Modifier.weight(1f)
            ) { viewModel.onToggleSign() }

            CalcButton(
                text = "%",
                type = ButtonType.FUNCTION,
                modifier = Modifier.weight(1f)
            ) { viewModel.onPercent() }

            CalcButton(
                text = "÷",
                type = ButtonType.OPERATOR,
                isActive = viewModel.activeOperation == Operation.DIVIDE,
                modifier = Modifier.weight(1f)
            ) { viewModel.onOperation(Operation.DIVIDE) }
        }

        ButtonRow {
            CalcButton("7", ButtonType.NUMBER, modifier = Modifier.weight(1f)) { viewModel.onDigit("7") }
            CalcButton("8", ButtonType.NUMBER, modifier = Modifier.weight(1f)) { viewModel.onDigit("8") }
            CalcButton("9", ButtonType.NUMBER, modifier = Modifier.weight(1f)) { viewModel.onDigit("9") }
            CalcButton(
                text = "×",
                type = ButtonType.OPERATOR,
                isActive = viewModel.activeOperation == Operation.MULTIPLY,
                modifier = Modifier.weight(1f)
            ) { viewModel.onOperation(Operation.MULTIPLY) }
        }

        ButtonRow {
            CalcButton("4", ButtonType.NUMBER, modifier = Modifier.weight(1f)) { viewModel.onDigit("4") }
            CalcButton("5", ButtonType.NUMBER, modifier = Modifier.weight(1f)) { viewModel.onDigit("5") }
            CalcButton("6", ButtonType.NUMBER, modifier = Modifier.weight(1f)) { viewModel.onDigit("6") }
            CalcButton(
                text = "−",
                type = ButtonType.OPERATOR,
                isActive = viewModel.activeOperation == Operation.SUBTRACT,
                modifier = Modifier.weight(1f)
            ) { viewModel.onOperation(Operation.SUBTRACT) }
        }

        ButtonRow {
            CalcButton("1", ButtonType.NUMBER, modifier = Modifier.weight(1f)) { viewModel.onDigit("1") }
            CalcButton("2", ButtonType.NUMBER, modifier = Modifier.weight(1f)) { viewModel.onDigit("2") }
            CalcButton("3", ButtonType.NUMBER, modifier = Modifier.weight(1f)) { viewModel.onDigit("3") }
            CalcButton(
                text = "+",
                type = ButtonType.OPERATOR,
                isActive = viewModel.activeOperation == Operation.ADD,
                modifier = Modifier.weight(1f)
            ) { viewModel.onOperation(Operation.ADD) }
        }

        ButtonRow {
            CalcButton(
                text = "0",
                type = ButtonType.NUMBER,
                modifier = Modifier.weight(2f),
                wide = true
            ) { viewModel.onDigit("0") }
            CalcButton(".", ButtonType.NUMBER, modifier = Modifier.weight(1f)) { viewModel.onDecimalPoint() }
            CalcButton(
                text = "=",
                type = ButtonType.OPERATOR,
                modifier = Modifier.weight(1f)
            ) { viewModel.onEquals() }
        }
    }
}

@Composable
fun ButtonRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        content = content
    )
}

enum class ButtonType { NUMBER, FUNCTION, OPERATOR }

@Composable
fun CalcButton(
    text: String,
    type: ButtonType,
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    wide: Boolean = false,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "buttonScale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = when {
            type == ButtonType.OPERATOR && isActive -> OperatorButtonActive
            type == ButtonType.OPERATOR -> if (isPressed) OperatorButtonPressed else OperatorButton
            type == ButtonType.FUNCTION -> if (isPressed) FunctionButtonPressed else FunctionButton
            else -> if (isPressed) NumberButtonPressed else NumberButton
        },
        animationSpec = tween(durationMillis = 120),
        label = "buttonColor"
    )

    val textColor = when {
        type == ButtonType.OPERATOR && isActive -> OperatorButton
        type == ButtonType.FUNCTION -> TextBlack
        else -> TextWhite
    }

    Box(
        modifier = modifier
            .height(72.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(backgroundColor)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onClick() }
                )
            },
        contentAlignment = if (wide) Alignment.CenterStart else Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 32.sp,
            fontWeight = FontWeight.Normal,
            modifier = if (wide) Modifier.padding(start = 28.dp) else Modifier
        )
    }
}

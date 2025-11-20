package dev.ktekik.sahaf.fts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.ktekik.sahaf.navigation.FtsNavigationViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Preview
@Composable
fun USZipcodeEntryScreen() {
    val viewModel: FtsNavigationViewModel = koinInject()

    val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()
    var zipcode by remember { mutableStateOf("") }
    var isContinueButtonVisible by remember { mutableStateOf(false) }
    val maxLength = 5

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(top = 16.dp),
    ) {

        // Main content area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Title
            Text(
                text = "Enter Zipcode",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Instruction text
            Text(
                text = "Please enter your zipcode to see books near you:",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Zipcode input boxes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(maxLength) { index ->
                    ZipcodeInputBox(
                        digit = zipcode.getOrNull(index)?.toString() ?: "",
                        isActive = index == zipcode.length,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Custom numeric keypad
        NumericKeypad(
            onNumberClick = { number ->
                if (zipcode.length < maxLength) {
                    zipcode += number
                }
                isContinueButtonVisible = zipcode.length == maxLength
            },
            onBackspaceClick = {
                if (zipcode.isNotEmpty()) {
                    zipcode = zipcode.dropLast(1)
                }
                isContinueButtonVisible = false
            },
            onContinueClick = {
                state.profile?.let {
                    viewModel.registerProfile(it.copy(zipcode = zipcode))
                }
            },
            isContinueButtonVisible
        )
    }
}

@Composable
fun ZipcodeInputBox(
    digit: String,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(64.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (digit.isNotEmpty()) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surface
                }
            )
            .then(
                if (isActive && digit.isEmpty()) {
                    Modifier.border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    )
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (digit.isNotEmpty()) {
            Text(
                text = digit,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun NumericKeypad(
    onNumberClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    onContinueClick: () -> Unit,
    continueButtonVisible: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp)
    ) {
        // Top handle line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .padding(vertical = 8.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Number pad grid (1-9)
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(3) { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    repeat(3) { col ->
                        val number = (row * 3 + col + 1).toString()
                        KeypadButton(
                            text = number,
                            onClick = { onNumberClick(number) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Bottom row with 0 and backspace
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (continueButtonVisible) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(72.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(onClick = onContinueClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✓",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 28.sp
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                // Zero button
                KeypadButton(
                    text = "0",
                    onClick = { onNumberClick("0") },
                    modifier = Modifier.weight(1f)
                )

                // Backspace button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(72.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onBackspaceClick),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⌫",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 28.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun KeypadButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keypadTextColor = Color.White

    Box(
        modifier = modifier
            .height(72.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = keypadTextColor,
            fontSize = 28.sp
        )
    }
}

//@Composable
//fun BackArrowIcon(
//    onClick: () -> Unit,
//    tint: Color,
//    modifier: Modifier = Modifier
//) {
//    androidx.compose.foundation.Canvas(
//        modifier = modifier
//            .clickable(onClick = onClick)
//    ) {
//        val path = Path().apply {
//            moveTo(size.width * 0.7f, size.height * 0.2f)
//            lineTo(size.width * 0.3f, size.height * 0.5f)
//            lineTo(size.width * 0.7f, size.height * 0.8f)
//        }
//        drawPath(
//            path = path,
//            color = tint,
//            style = Stroke(width = 3.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
//        )
//    }
//}


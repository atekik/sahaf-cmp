package dev.ktekik.signin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.ktekik.utils.LocalResources
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun GoogleButtonUI(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showProgressBar: Boolean = false,
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 12.dp,
            pressedElevation = 16.dp,
            focusedElevation = 10.dp
        ),
        shape = MaterialTheme.shapes.extraLarge,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
    ) {
        Spacer(modifier = Modifier.width(16.dp))

        Image(
            modifier = Modifier.size(32.dp),
            painter = painterResource(LocalResources.current.drawables.googleIcon),
            contentDescription = "Google Icon"
        )
        Spacer(modifier = Modifier.width(16.dp))
        if (showProgressBar) {
            CircularProgressIndicator(
                color = LocalContentColor.current,
            )
        } else {
            Text(
                text = "Sign Up With Google",
                color = LocalContentColor.current,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
    }
}
package dev.ktekik.signin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.White,
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Image(
            modifier = Modifier.size(24.dp),
            painter = painterResource(LocalResources.current.drawables.googleIcon),
            contentDescription = "Google Icon"
        )
        Spacer(modifier = Modifier.width(8.dp))
        if (showProgressBar) {
            CircularProgressIndicator()
        } else {
            Text("Continue with Google")
        }
    }
}
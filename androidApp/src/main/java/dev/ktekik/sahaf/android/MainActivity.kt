package dev.ktekik.sahaf.android

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.FirebaseApp
import dev.ktekik.sahaf.navigation.NavHost
import dev.ktekik.sahaf.theming.SahafTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this.application)

        if (isDarkTheme()) {
            enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.dark(
                    Color.TRANSPARENT,
                ),
                navigationBarStyle = SystemBarStyle.dark(
                    Color.TRANSPARENT,
                )
            )
        } else {
            enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.light(
                    Color.TRANSPARENT,
                    Color.TRANSPARENT
                ),
                navigationBarStyle = SystemBarStyle.light(
                    Color.TRANSPARENT,
                    Color.TRANSPARENT
                )
            )
        }


        setContent {
            SahafTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost()
                }
            }
        }
    }

    fun isDarkTheme(): Boolean {
        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
}



@Composable
fun GreetingView(text: String) {
    Text(text = text)
}

@Preview
@Composable
fun DefaultPreview() {
    SahafTheme {
        GreetingView("Hello, Android!")
    }
}

package dev.enokk.schedato

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.enokk.schedato.ui.theme.SchedatoTheme
import dev.enokk.schedato.ui.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SchedatoTheme {
                AppNavigation()
            }
        }
    }
}

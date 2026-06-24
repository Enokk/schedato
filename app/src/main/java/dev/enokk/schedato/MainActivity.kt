package dev.enokk.schedato

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.enokk.schedato.model.AppTheme
import dev.enokk.schedato.ui.navigation.AppNavigation
import dev.enokk.schedato.ui.theme.SchedatoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as SchedatoApplication
        enableEdgeToEdge()
        setContent {
            val appTheme by app.userPreferencesRepository.appTheme
                .collectAsStateWithLifecycle(initialValue = AppTheme.SYSTEM)
            SchedatoTheme(appTheme = appTheme) {
                AppNavigation()
            }
        }
    }
}

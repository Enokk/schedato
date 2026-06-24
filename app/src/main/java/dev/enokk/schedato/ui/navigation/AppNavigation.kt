package dev.enokk.schedato.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.enokk.schedato.SchedatoApplication
import dev.enokk.schedato.ui.screens.home.HomeScreen
import dev.enokk.schedato.ui.screens.home.HomeViewModel
import dev.enokk.schedato.ui.screens.settings.SettingsScreen
import dev.enokk.schedato.ui.screens.settings.SettingsViewModel

object Routes {
    const val HOME = "home"
    const val SETTINGS = "settings"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val app = LocalContext.current.applicationContext as SchedatoApplication

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            val viewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.factory(app.characterRepository)
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            HomeScreen(
                uiState = uiState,
                onNewCharacterClick = viewModel::onNewCharacterClick,
                onNewCharacterConfirm = viewModel::onNewCharacterConfirm,
                onDialogDismiss = viewModel::onDialogDismiss,
                onDeleteRequested = viewModel::onDeleteRequested,
                onDeleteConfirmed = viewModel::onDeleteConfirmed,
                onDeleteDismissed = viewModel::onDeleteDismissed,
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }
        composable(Routes.SETTINGS) {
            val viewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModel.factory(app.userPreferencesRepository)
            )
            val appTheme by viewModel.appTheme.collectAsStateWithLifecycle()
            SettingsScreen(
                appTheme = appTheme,
                onThemeChange = viewModel::setTheme,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

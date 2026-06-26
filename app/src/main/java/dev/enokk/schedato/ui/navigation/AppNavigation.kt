package dev.enokk.schedato.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.enokk.schedato.SchedatoApplication
import dev.enokk.schedato.ui.screens.characterdetail.CharacterDetailScreen
import dev.enokk.schedato.ui.screens.characterdetail.CharacterDetailViewModel
import dev.enokk.schedato.ui.screens.home.HomeScreen
import dev.enokk.schedato.ui.screens.home.HomeViewModel
import dev.enokk.schedato.ui.screens.settings.SettingsScreen
import dev.enokk.schedato.ui.screens.settings.SettingsViewModel

object Routes {
    const val HOME = "home"
    const val SETTINGS = "settings"
    const val CHARACTER_DETAIL = "character/{characterId}"
    const val CHARACTER_CREATE = "character/new"

    fun characterDetail(id: String) = "character/$id"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val app = LocalContext.current.applicationContext as SchedatoApplication

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            val viewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.factory(app.characterRepository)
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            HomeScreen(
                uiState = uiState,
                onNewCharacterClick = { navController.navigate(Routes.CHARACTER_CREATE) },
                onCharacterClick = { character -> navController.navigate(Routes.characterDetail(character.id)) },
                onDeleteRequested = viewModel::onDeleteRequested,
                onDeleteConfirmed = viewModel::onDeleteConfirmed,
                onDeleteDismissed = viewModel::onDeleteDismissed,
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }
        composable(Routes.SETTINGS) {
            val viewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModel.factory(app.userPreferencesRepository, app.localeRepository)
            )
            val appTheme by viewModel.appTheme.collectAsStateWithLifecycle()
            val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
            SettingsScreen(
                appTheme = appTheme,
                onThemeChange = viewModel::setTheme,
                appLanguage = appLanguage,
                onLanguageChange = viewModel::setLanguage,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Routes.CHARACTER_DETAIL,
            arguments = listOf(navArgument("characterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val rawId = backStackEntry.arguments?.getString("characterId") ?: "new"
            val characterId = if (rawId == "new") null else rawId
            val viewModel: CharacterDetailViewModel = viewModel(
                factory = CharacterDetailViewModel.factory(app.characterRepository, characterId)
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            CharacterDetailScreen(
                uiState = uiState,
                onNameChange = viewModel::onNameChange,
                onRaceChange = viewModel::onRaceChange,
                onClassChange = viewModel::onClassChange,
                onLevelChange = viewModel::onLevelChange,
                onSave = viewModel::save,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

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
import dev.enokk.schedato.model.AppClass
import dev.enokk.schedato.model.AppRace
import dev.enokk.schedato.ui.screens.characterdetail.CharacterDetailScreen
import dev.enokk.schedato.ui.screens.characterdetail.CharacterDetailViewModel
import dev.enokk.schedato.ui.screens.classpicker.ClassPickerScreen
import dev.enokk.schedato.ui.screens.classpicker.ClassPickerViewModel
import dev.enokk.schedato.ui.screens.home.HomeScreen
import dev.enokk.schedato.ui.screens.home.HomeViewModel
import dev.enokk.schedato.ui.screens.racepicker.RacePickerScreen
import dev.enokk.schedato.ui.screens.racepicker.RacePickerViewModel
import dev.enokk.schedato.ui.screens.settings.SettingsScreen
import dev.enokk.schedato.ui.screens.settings.SettingsViewModel

object Routes {
    const val HOME = "home"
    const val SETTINGS = "settings"
    const val RACE_PICKER = "race_picker"
    const val CLASS_PICKER = "class_picker/{raceName}"
    const val CHARACTER_CREATE = "character/new/{raceName}/{className}"
    const val CHARACTER_DETAIL = "character/{characterId}"

    fun classPickerRoute(raceName: String) = "class_picker/$raceName"
    fun characterCreate(raceName: String, className: String) = "character/new/$raceName/$className"
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
                onNewCharacterClick = { navController.navigate(Routes.RACE_PICKER) },
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
        composable(Routes.RACE_PICKER) {
            val viewModel: RacePickerViewModel = viewModel(
                factory = RacePickerViewModel.factory()
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            RacePickerScreen(
                uiState = uiState,
                onGroupClick = viewModel::onGroupClick,
                onSubRaceSelected = viewModel::onSubRaceSelected,
                onSubRaceDialogDismiss = viewModel::onSubRaceDialogDismiss,
                onNext = { race ->
                    navController.navigate(Routes.classPickerRoute(race.name))
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Routes.CLASS_PICKER,
            arguments = listOf(navArgument("raceName") { type = NavType.StringType })
        ) { backStackEntry ->
            val raceName = backStackEntry.arguments?.getString("raceName") ?: ""
            val viewModel: ClassPickerViewModel = viewModel(
                factory = ClassPickerViewModel.factory()
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            ClassPickerScreen(
                uiState = uiState,
                onClassSelected = viewModel::onClassSelected,
                onNext = { appClass ->
                    navController.navigate(Routes.characterCreate(raceName, appClass.name))
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Routes.CHARACTER_CREATE,
            arguments = listOf(
                navArgument("raceName") { type = NavType.StringType },
                navArgument("className") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val raceName = backStackEntry.arguments?.getString("raceName") ?: ""
            val className = backStackEntry.arguments?.getString("className") ?: ""
            val initialRace = AppRace.entries.find { it.name == raceName }
            val initialClass = AppClass.entries.find { it.name == className }
            val viewModel: CharacterDetailViewModel = viewModel(
                factory = CharacterDetailViewModel.factory(
                    repository = app.characterRepository,
                    characterId = null,
                    initialRace = initialRace,
                    initialClass = initialClass
                )
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            CharacterDetailScreen(
                uiState = uiState,
                onNameChange = viewModel::onNameChange,
                onRaceChange = viewModel::onRaceChange,
                onClassChange = viewModel::onClassChange,
                onLevelChange = viewModel::onLevelChange,
                onSave = viewModel::save,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack(Routes.HOME, inclusive = false) }
            )
        }
        composable(
            route = Routes.CHARACTER_DETAIL,
            arguments = listOf(navArgument("characterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val characterId = backStackEntry.arguments?.getString("characterId") ?: return@composable
            val viewModel: CharacterDetailViewModel = viewModel(
                factory = CharacterDetailViewModel.factory(
                    repository = app.characterRepository,
                    characterId = characterId
                )
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

package dev.enokk.schedato.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.enokk.schedato.data.repository.UserPreferencesRepository
import dev.enokk.schedato.model.AppTheme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: UserPreferencesRepository) : ViewModel() {

    val appTheme: StateFlow<AppTheme> = repository.appTheme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppTheme.SYSTEM
        )

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch { repository.setAppTheme(theme) }
    }

    companion object {
        fun factory(repository: UserPreferencesRepository) = viewModelFactory {
            initializer { SettingsViewModel(repository) }
        }
    }
}

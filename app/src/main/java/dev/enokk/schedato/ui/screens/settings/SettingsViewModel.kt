package dev.enokk.schedato.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.enokk.schedato.data.repository.LocaleRepository
import dev.enokk.schedato.data.repository.UserPreferencesRepository
import dev.enokk.schedato.model.AppLanguage
import dev.enokk.schedato.model.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesRepository: UserPreferencesRepository,
    private val localeRepository: LocaleRepository
) : ViewModel() {

    val appTheme: StateFlow<AppTheme> = preferencesRepository.appTheme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppTheme.SYSTEM
        )

    private val _appLanguage = MutableStateFlow(localeRepository.getAppLanguage())
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch { preferencesRepository.setAppTheme(theme) }
    }

    fun setLanguage(language: AppLanguage) {
        _appLanguage.value = language
        localeRepository.setAppLanguage(language)
    }

    companion object {
        fun factory(
            preferencesRepository: UserPreferencesRepository,
            localeRepository: LocaleRepository
        ) = viewModelFactory {
            initializer { SettingsViewModel(preferencesRepository, localeRepository) }
        }
    }
}

package dev.enokk.schedato.ui.screens.racepicker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.enokk.schedato.model.AppRace
import dev.enokk.schedato.model.RaceGroup
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class RacePickerUiState(
    val selectedRace: AppRace? = null,
    val subRaceDialog: RaceGroup? = null
)

class RacePickerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RacePickerUiState())
    val uiState: StateFlow<RacePickerUiState> = _uiState.asStateFlow()

    fun onGroupClick(group: RaceGroup) {
        if (group.races.size == 1) {
            _uiState.update { it.copy(selectedRace = group.races.first()) }
        } else {
            _uiState.update { it.copy(subRaceDialog = group) }
        }
    }

    fun onSubRaceSelected(race: AppRace) {
        _uiState.update { it.copy(selectedRace = race, subRaceDialog = null) }
    }

    fun onSubRaceDialogDismiss() {
        _uiState.update { it.copy(subRaceDialog = null) }
    }

    companion object {
        fun factory() = viewModelFactory {
            initializer { RacePickerViewModel() }
        }
    }
}

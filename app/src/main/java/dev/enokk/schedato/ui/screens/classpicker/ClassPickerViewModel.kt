package dev.enokk.schedato.ui.screens.classpicker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.enokk.schedato.model.AppClass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ClassPickerUiState(
    val selectedClass: AppClass? = null
)

class ClassPickerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ClassPickerUiState())
    val uiState: StateFlow<ClassPickerUiState> = _uiState.asStateFlow()

    fun onClassSelected(appClass: AppClass) = _uiState.update { it.copy(selectedClass = appClass) }

    companion object {
        fun factory() = viewModelFactory {
            initializer { ClassPickerViewModel() }
        }
    }
}

package dev.enokk.schedato.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.enokk.schedato.data.repository.CharacterRepository
import dev.enokk.schedato.model.Character
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class HomeUiState(
    val characters: List<Character> = emptyList(),
    val showNewCharacterDialog: Boolean = false,
    val characterPendingDelete: Character? = null
)

class HomeViewModel(private val repository: CharacterRepository) : ViewModel() {

    private val _showDialog = MutableStateFlow(false)
    private val _characterPendingDelete = MutableStateFlow<Character?>(null)

    val uiState = combine(
        repository.characters,
        _showDialog,
        _characterPendingDelete
    ) { characters, showDialog, pendingDelete ->
        HomeUiState(
            characters = characters,
            showNewCharacterDialog = showDialog,
            characterPendingDelete = pendingDelete
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )

    fun onNewCharacterClick() {
        _showDialog.value = true
    }

    fun onNewCharacterConfirm(name: String) {
        if (name.isBlank()) return
        _showDialog.value = false
        viewModelScope.launch {
            repository.addCharacter(
                Character(id = UUID.randomUUID().toString(), name = name.trim())
            )
        }
    }

    fun onDialogDismiss() {
        _showDialog.value = false
    }

    fun onDeleteRequested(character: Character) {
        _characterPendingDelete.value = character
    }

    fun onDeleteConfirmed() {
        val character = _characterPendingDelete.value ?: return
        _characterPendingDelete.value = null
        viewModelScope.launch { repository.deleteCharacter(character.id) }
    }

    fun onDeleteDismissed() {
        _characterPendingDelete.value = null
    }

    companion object {
        fun factory(repository: CharacterRepository) = viewModelFactory {
            initializer { HomeViewModel(repository) }
        }
    }
}

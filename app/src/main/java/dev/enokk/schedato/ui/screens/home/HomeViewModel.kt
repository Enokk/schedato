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

data class HomeUiState(
    val characters: List<Character> = emptyList(),
    val characterPendingDelete: Character? = null
)

class HomeViewModel(private val repository: CharacterRepository) : ViewModel() {

    private val _characterPendingDelete = MutableStateFlow<Character?>(null)

    val uiState = combine(
        repository.characters,
        _characterPendingDelete
    ) { characters, pendingDelete ->
        HomeUiState(characters = characters, characterPendingDelete = pendingDelete)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )

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

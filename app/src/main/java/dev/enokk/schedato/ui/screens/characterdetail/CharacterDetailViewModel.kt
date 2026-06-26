package dev.enokk.schedato.ui.screens.characterdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.enokk.schedato.data.repository.CharacterRepository
import dev.enokk.schedato.model.AppClass
import dev.enokk.schedato.model.AppRace
import dev.enokk.schedato.model.Character
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class CharacterDetailUiState(
    val originalName: String? = null,   // null = create mode; usato come titolo schermata
    val name: String = "",
    val race: AppRace? = null,
    val characterClass: AppClass? = null,
    val level: Int = 1,
    val isSaved: Boolean = false
)

class CharacterDetailViewModel(
    private val repository: CharacterRepository,
    private val characterId: String?        // null = create mode
) : ViewModel() {

    private val _uiState = MutableStateFlow(CharacterDetailUiState())
    val uiState: StateFlow<CharacterDetailUiState> = _uiState.asStateFlow()

    init {
        if (characterId != null) {
            viewModelScope.launch {
                repository.getCharacterById(characterId)?.let { character ->
                    _uiState.update {
                        it.copy(
                            originalName = character.name,
                            name = character.name,
                            race = AppRace.entries.find { it.name == character.race },
                            characterClass = AppClass.entries.find { it.name == character.characterClass },
                            level = character.level
                        )
                    }
                }
            }
        }
    }

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value) }
    fun onRaceChange(value: AppRace?) = _uiState.update { it.copy(race = value) }
    fun onClassChange(value: AppClass?) = _uiState.update { it.copy(characterClass = value) }
    fun onLevelChange(value: Int) = _uiState.update { it.copy(level = value.coerceIn(1, 20)) }

    fun save() {
        val state = _uiState.value
        if (state.name.isBlank()) return
        viewModelScope.launch {
            repository.saveCharacter(
                Character(
                    id = characterId ?: UUID.randomUUID().toString(),
                    name = state.name.trim(),
                    race = state.race?.name ?: "—",
                    characterClass = state.characterClass?.name ?: "—",
                    level = state.level
                )
            )
            _uiState.update { it.copy(isSaved = true) }
        }
    }

    companion object {
        fun factory(repository: CharacterRepository, characterId: String?) = viewModelFactory {
            initializer { CharacterDetailViewModel(repository, characterId) }
        }
    }
}

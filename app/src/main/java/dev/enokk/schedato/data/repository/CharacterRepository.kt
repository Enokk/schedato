package dev.enokk.schedato.data.repository

import dev.enokk.schedato.data.local.CharacterDao
import dev.enokk.schedato.data.local.toDomain
import dev.enokk.schedato.data.local.toEntity
import dev.enokk.schedato.model.Character
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CharacterRepository(private val dao: CharacterDao) {

    val characters: Flow<List<Character>> = dao.getAll().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun getCharacterById(id: String): Character? = dao.getById(id)?.toDomain()

    suspend fun saveCharacter(character: Character) = dao.upsert(character.toEntity())

    suspend fun deleteCharacter(id: String) = dao.deleteById(id)
}

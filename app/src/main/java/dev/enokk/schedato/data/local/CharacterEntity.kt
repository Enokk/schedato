package dev.enokk.schedato.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.enokk.schedato.model.Character

@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey val id: String,
    val name: String,
    val race: String,
    val characterClass: String,
    val level: Int
)

fun CharacterEntity.toDomain() = Character(
    id = id,
    name = name,
    race = race,
    characterClass = characterClass,
    level = level
)

fun Character.toEntity() = CharacterEntity(
    id = id,
    name = name,
    race = race,
    characterClass = characterClass,
    level = level
)

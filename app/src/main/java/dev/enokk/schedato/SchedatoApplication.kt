package dev.enokk.schedato

import android.app.Application
import dev.enokk.schedato.data.local.AppDatabase
import dev.enokk.schedato.data.repository.CharacterRepository
import dev.enokk.schedato.data.repository.LocaleRepository
import dev.enokk.schedato.data.repository.UserPreferencesRepository

class SchedatoApplication : Application() {

    private val database by lazy { AppDatabase.getInstance(this) }
    val characterRepository by lazy { CharacterRepository(database.characterDao()) }
    val userPreferencesRepository by lazy { UserPreferencesRepository(this) }
    val localeRepository by lazy { LocaleRepository(this) }
}

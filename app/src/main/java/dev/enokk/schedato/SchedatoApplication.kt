package dev.enokk.schedato

import android.app.Application
import dev.enokk.schedato.data.local.AppDatabase
import dev.enokk.schedato.data.repository.CharacterRepository

class SchedatoApplication : Application() {

    val database by lazy { AppDatabase.getInstance(this) }
    val characterRepository by lazy { CharacterRepository(database.characterDao()) }
}

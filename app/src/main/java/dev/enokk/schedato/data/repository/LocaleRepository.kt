package dev.enokk.schedato.data.repository

import android.app.LocaleManager
import android.content.Context
import android.os.LocaleList
import dev.enokk.schedato.model.AppLanguage

class LocaleRepository(context: Context) {

    private val localeManager = context.getSystemService(LocaleManager::class.java)

    fun getAppLanguage(): AppLanguage {
        val locales = localeManager.applicationLocales
        if (locales.isEmpty) return AppLanguage.SYSTEM
        val tag = locales[0]?.language ?: return AppLanguage.SYSTEM
        return AppLanguage.entries.firstOrNull { it.tag == tag } ?: AppLanguage.SYSTEM
    }

    fun setAppLanguage(language: AppLanguage) {
        localeManager.applicationLocales = if (language == AppLanguage.SYSTEM) {
            LocaleList.getEmptyLocaleList()
        } else {
            LocaleList.forLanguageTags(language.tag)
        }
    }
}

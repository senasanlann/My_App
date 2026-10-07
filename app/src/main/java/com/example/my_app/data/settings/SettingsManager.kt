package com.example.my_app.data.settings

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.my_app.model.SortOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.settingsDataStore by preferencesDataStore(name = "settings_prefs")

class SettingsManager(private val context: Context) {
    companion object {
        private val DARK_THEME = booleanPreferencesKey("dark_theme")
        private val DEFAULT_SORT = stringPreferencesKey("default_sort")
    }

    val darkThemeFlow: Flow<Boolean> = context.settingsDataStore.data.map { preferences ->
        preferences[DARK_THEME] ?: false
    }

    val defaultSortFlow: Flow<SortOption> = context.settingsDataStore.data.map { preferences ->
        val name = preferences[DEFAULT_SORT]
        SortOption.entries.find { it.name == name } ?: SortOption.NEWEST
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.settingsDataStore.edit { it[DARK_THEME] = enabled }
    }

    suspend fun setDefaultSort(option: SortOption) {
        context.settingsDataStore.edit { it[DEFAULT_SORT] = option.name }
    }
}

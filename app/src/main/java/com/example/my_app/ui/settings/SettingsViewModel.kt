package com.example.my_app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.my_app.data.settings.SettingsManager
import com.example.my_app.model.SortOption
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingsManager: SettingsManager) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsManager.darkThemeFlow,
        settingsManager.defaultSortFlow
    ) { darkTheme, defaultSort ->
        SettingsUiState(darkTheme = darkTheme, defaultSort = defaultSort)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun onDarkThemeChange(enabled: Boolean) {
        viewModelScope.launch { settingsManager.setDarkTheme(enabled) }
    }

    fun onDefaultSortChange(option: SortOption) {
        viewModelScope.launch { settingsManager.setDefaultSort(option) }
    }
}

class SettingsViewModelFactory(
    private val settingsManager: SettingsManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return SettingsViewModel(settingsManager) as T
    }
}

package com.craftxcode.notepad2do.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.craftxcode.notepad2do.data.model.AppTheme
import com.craftxcode.notepad2do.data.model.SortOrder
import com.craftxcode.notepad2do.data.model.ViewType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsManager(private val context: Context) {

    private object PreferencesKeys {
        val APP_THEME = stringPreferencesKey("app_theme")
        val VIEW_TYPE = stringPreferencesKey("view_type")
        val SORT_ORDER = stringPreferencesKey("sort_order")
        val PRIMARY_COLOR = stringPreferencesKey("primary_color")
    }

    val appThemeFlow: Flow<AppTheme> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val themeName = preferences[PreferencesKeys.APP_THEME] ?: AppTheme.SYSTEM.name
            try {
                AppTheme.valueOf(themeName)
            } catch (e: IllegalArgumentException) {
                AppTheme.SYSTEM
            }
        }

    val viewTypeFlow: Flow<ViewType> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val viewTypeName = preferences[PreferencesKeys.VIEW_TYPE] ?: ViewType.LIST.name
            try {
                ViewType.valueOf(viewTypeName)
            } catch (e: IllegalArgumentException) {
                ViewType.LIST
            }
        }

    val sortOrderFlow: Flow<SortOrder> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val sortOrderName = preferences[PreferencesKeys.SORT_ORDER] ?: SortOrder.DATE_MODIFIED.name
            try {
                SortOrder.valueOf(sortOrderName)
            } catch (e: IllegalArgumentException) {
                SortOrder.DATE_MODIFIED
            }
        }

    val primaryColorFlow: Flow<Long> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.PRIMARY_COLOR]?.toLongOrNull() ?: 0xFF6750A4L
        }

    suspend fun updateAppTheme(theme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.APP_THEME] = theme.name
        }
    }

    suspend fun updateViewType(viewType: ViewType) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.VIEW_TYPE] = viewType.name
        }
    }

    suspend fun updateSortOrder(sortOrder: SortOrder) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SORT_ORDER] = sortOrder.name
        }
    }

    suspend fun updatePrimaryColor(color: Long) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PRIMARY_COLOR] = color.toString()
        }
    }
}

package todolist.al.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "onboarding_pref"

val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

object OnboardingPreferences {
    private val ONBOARDING_KEY = booleanPreferencesKey("onboarding_completed")

    fun readOnboardingState(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[ONBOARDING_KEY] ?: false
        }
    }

    suspend fun saveOnboardingState(context: Context, completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_KEY] = completed
        }
    }
}

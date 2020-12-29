package com.homeworkshop.mymvvmtodo.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PreferencesManager"

//reprezentuje różne stany
enum class SortOrder{BY_NAME,BY_DATE}

//opakowanie do dwóch wartości preferecji
data class FilterPreferences(val sortOrder: SortOrder, val hideComleted: Boolean)

//Możemy całą tą logikę umieścić w ViewModel ale lepiej wyekspediować ją do osobnej klasy
@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext context: Context){

    //z kontekstu bierzemy bazę danych
    private val dataStore = context.createDataStore("user_preferences")

    //To Flow przechowuje wszystkie settings
    // zamiast stosować te Flow directly w ViewModel , przygotowujemy je tutaj
    //Tutaj zczytujemy wszystkie ustawienia z dataStore do zmiennej preferencesFlow którą będziemy używać w ViewModelu
    val preferencesFlow = dataStore.data
        .catch { exception ->
            if(exception is IOException){
                Log.e(TAG, "Error reading preferences ", exception )
                emit(emptyPreferences())
            }else{
                throw exception
            }
        }
        .map { preferences ->
            val sortOrder = SortOrder.valueOf(
                preferences[PreferencesKeys.SORT_ORDER] ?: SortOrder.BY_DATE.name
            )
            val hideCompleted = preferences[PreferencesKeys.HIDE_COMPLETED] ?: false
            //Nie możemy zwrócić dwóch wartości dlatego trzeba je zapakować
            FilterPreferences(sortOrder,hideCompleted)
        }
    //funkcja zapisująca ustawienia sortOrder do dataStore
    suspend fun updateSortOrder(sortOrder: SortOrder){
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SORT_ORDER] = sortOrder.name
        }
    }
    //funkcja zapisująca ustawienia hideCompleted do dataStore
    suspend fun updateHideCompleted(hideComleted: Boolean){
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.HIDE_COMPLETED] = hideComleted
        }
    }

    private object PreferencesKeys{
        val SORT_ORDER = preferencesKey<String>("sort_order")
        val HIDE_COMPLETED = preferencesKey<Boolean>("hide_completed")
    }
}
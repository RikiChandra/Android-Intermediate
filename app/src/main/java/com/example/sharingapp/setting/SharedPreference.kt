package com.example.sharingapp.setting

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SharedPreference private constructor(private val dataStore: DataStore<Preferences>) {

    private val KEY_TOKEN = stringPreferencesKey("token")
    private val KEY_NAME = stringPreferencesKey("name")
    private val KEY_STATE = booleanPreferencesKey("state")


    data class User(
        val name: String,
        val token: String,
    )


    fun ambilToken(): Flow<String>{
        return dataStore.data.map {
            it[KEY_TOKEN] ?: ""
        }
    }


    fun ambilInfoUser(): Flow<User>{
        return dataStore.data.map {
            pref -> User(
                pref[KEY_NAME] ?: "",
                pref[KEY_TOKEN] ?: "",
            )
        }
    }

    fun isLogged(): Flow<Boolean>{
        return dataStore.data.map {
            pref -> pref[KEY_STATE] ?: false
        }
    }

    suspend fun isSaved(token: String, name:String){
        dataStore.edit { pref ->
            pref[KEY_TOKEN] = token
            pref[KEY_NAME] = name
        }
    }

    suspend fun isState(){
        dataStore.edit { pref ->
            pref[KEY_STATE] = true
        }
    }

    suspend fun isLogout(){
        dataStore.edit {
            it[KEY_TOKEN] = ""
            it[KEY_STATE] = false
        }
    }


    companion object {
        @Volatile
        private var INSTANCE: SharedPreference? = null

        fun getInstance(dataStore: DataStore<Preferences>): SharedPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = SharedPreference(dataStore)
                INSTANCE = instance
                instance
            }

        }
    }
}

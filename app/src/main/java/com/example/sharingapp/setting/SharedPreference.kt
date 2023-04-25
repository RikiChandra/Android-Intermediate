package com.example.sharingapp.setting

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.example.sharingapp.R
import com.google.android.gms.maps.GoogleMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SharedPreference private constructor(private val dataStore: DataStore<Preferences>) {

    private val KEY_TOKEN = stringPreferencesKey("token")
    private val KEY_NAME = stringPreferencesKey("name")
    private val KEY_STATE = booleanPreferencesKey("state")
    private val KEY_MAP_TYPE = stringPreferencesKey("map_type")


    data class User(
        val name: String,
        val token: String,
    )

    enum class MapType(val value: Int) {
        DEFAULT(R.raw.maps_default),
        NIGHT(R.raw.maps_night),
        RETRO(R.raw.maps_retro)
    }


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

    suspend fun saveMapType(mapType: MapType) {
        dataStore.edit {
            it[KEY_MAP_TYPE] = mapType.name
        }
    }

    fun getMapType(): Flow<MapType> {
        return dataStore.data.map {
            it[KEY_MAP_TYPE]?.let { value ->
                MapType.valueOf(value)
            } ?: MapType.DEFAULT
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

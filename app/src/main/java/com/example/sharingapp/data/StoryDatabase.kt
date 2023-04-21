package com.example.sharingapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sharingapp.responses.Story


@Database(entities = [Story::class], version = 1, exportSchema = false)
abstract class StoryDatabase() : RoomDatabase() {

    companion object {
        const val DATABASE_NAME = "story_db"

        @Volatile
        private var instance: StoryDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): StoryDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    StoryDatabase::class.java, DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
        }

    }




}
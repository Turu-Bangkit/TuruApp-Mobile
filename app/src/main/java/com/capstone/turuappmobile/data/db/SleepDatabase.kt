package com.capstone.turuappmobile.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.capstone.turuappmobile.data.db.SleepClassifyEventDao


private const val DATABASE_NAME = "sleep_segments_database"

@Database(
    entities = [SleepClassifyEventEntity::class , SleepTimeEntity::class, SleepQualityEntity::class],
    version = 2,
    exportSchema = false
)
abstract class SleepDatabase : RoomDatabase() {

    abstract fun sleepClassifyEventDao() : SleepClassifyEventDao

    abstract fun sleepTimeDao() : SleepTimeDao
    abstract fun sleepQualityDao() : SleepQualityDao


    companion object {

        @Volatile
        private var INSTANCE : SleepDatabase? = null

        fun getDatabase(context : Context) : SleepDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context,
                    SleepDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}
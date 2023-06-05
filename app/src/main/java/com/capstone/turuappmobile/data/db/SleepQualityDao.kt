package com.capstone.turuappmobile.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepQualityDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(sleepQualityEntity: SleepQualityEntity)

    @Query("SELECT * FROM sleep_quality_table WHERE id_user = :userUID LIMIT 7")
    fun getQualityLimit(userUID : String): Flow<List<SleepQualityEntity>>
}
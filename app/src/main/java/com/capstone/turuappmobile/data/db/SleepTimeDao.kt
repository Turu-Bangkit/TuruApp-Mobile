package com.capstone.turuappmobile.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow
@Dao
interface SleepTimeDao {

    @Query( "SELECT * FROM sleep_time_table ORDER BY start_time DESC")
    fun getAll(): Flow<List<SleepTimeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sleepTimeEntity: SleepTimeEntity)

    @Query("UPDATE sleep_time_table SET end_time = :endTime WHERE start_time = (SELECT MAX(start_time) FROM sleep_time_table)")
    suspend fun update(endTime: Int)


}
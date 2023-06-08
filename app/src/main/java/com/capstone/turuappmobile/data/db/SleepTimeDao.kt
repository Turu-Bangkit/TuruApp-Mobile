package com.capstone.turuappmobile.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepTimeDao {

    @Query("SELECT * FROM sleep_time_table ORDER BY start_time DESC")
    fun getAll(): Flow<List<SleepTimeEntity>>

    @Query("SELECT * FROM sleep_time_table WHERE id = :userUID ORDER BY start_time DESC")
    fun getById(userUID: String): Flow<List<SleepTimeEntity>>

    @Query("SELECT * FROM sleep_time_table WHERE id = :userUID ORDER BY start_time DESC LIMIT 7")
    fun getByIdLimit(userUID: String): Flow<List<SleepTimeEntity>>


    @Query("SELECT * FROM sleep_time_table WHERE id = :userUID AND start_time BETWEEN :startDate AND :endDate ORDER BY start_time DESC")
    fun getByRangeDate(userUID: String, startDate : Int , endDate : Int): Flow<List<SleepTimeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sleepTimeEntity: SleepTimeEntity)

    @Query("UPDATE sleep_time_table SET end_time = :endTime WHERE start_time = (SELECT MAX(start_time) FROM sleep_time_table)")
    suspend fun update(endTime: Int)

    @Query("UPDATE sleep_time_table SET real_start_time = :realStartTime WHERE start_time = (SELECT MAX(start_time) FROM sleep_time_table) AND real_start_time IS NULL")
    suspend fun updateRealStartTime(realStartTime: Int)

    @Query("SELECT COUNT (*) FROM sleep_time_table WHERE real_start_time <= :newStartRules AND end_time >= :newEndRules AND id = :userUID")
    fun checkChallengePass(newStartRules: Int, newEndRules: Int, userUID: String): Flow<Int>

}
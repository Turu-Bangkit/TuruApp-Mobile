package com.capstone.turuappmobile.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "sleep_time_table")
data class SleepTimeEntity(
    @ColumnInfo(name = "id")
    val userUID: String,
    @PrimaryKey
    @ColumnInfo(name = "start_time")
    val startTime: Int,
    @ColumnInfo(name = "end_time")
    val endTime: Int? = null,
    @ColumnInfo(name = "real_start_time")
    val realStartTime: Int? = null,
    )
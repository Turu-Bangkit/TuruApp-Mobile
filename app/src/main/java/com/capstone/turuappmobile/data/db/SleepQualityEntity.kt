package com.capstone.turuappmobile.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sleep_quality_table")
data class SleepQualityEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "sleep_quality")
    val sleepQuality: Float,
    @ColumnInfo(name = "id_user")
    val userUID: String,
)
package com.capstone.turuappmobile.utils

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun convertEpochToDateTime(epochTime: Int): String {
    val zoneOffset = ZoneId.systemDefault().rules.getOffset(LocalDateTime.now())
    val dateTime = LocalDateTime.ofEpochSecond(epochTime.toLong(), 0, zoneOffset)
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    return dateTime.format(formatter)
}
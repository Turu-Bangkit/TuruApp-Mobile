package com.capstone.turuappmobile.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.capstone.turuappmobile.ui.animation.ShimmerAnimation
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.sqrt

fun convertEpochToDateTime(epochTime: Int): String {
    val zoneOffset = ZoneId.systemDefault().rules.getOffset(LocalDateTime.now())
    val dateTime = LocalDateTime.ofEpochSecond(epochTime.toLong(), 0, zoneOffset)
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
    return dateTime.format(formatter)
}

fun convertEpochToJustDateTime(epochTime: Int): String {
    val zoneOffset = ZoneId.systemDefault().rules.getOffset(LocalDateTime.now())
    val dateTime = LocalDateTime.ofEpochSecond(epochTime.toLong(), 0, zoneOffset)
    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("id", "ID"))
    return dateTime.format(formatter)
}

fun convertEpochToJustDateTimeNoYear(epochTime: Int): String {
    val zoneOffset = ZoneId.systemDefault().rules.getOffset(LocalDateTime.now())
    val dateTime = LocalDateTime.ofEpochSecond(epochTime.toLong(), 0, zoneOffset)
    val formatter = DateTimeFormatter.ofPattern("dd MMMM", Locale("id", "ID"))
    return dateTime.format(formatter)
}


fun convertEpochToHour(epochTime: Int): String {
    val zoneOffset = ZoneId.systemDefault().rules.getOffset(LocalDateTime.now())
    val dateTime = LocalDateTime.ofEpochSecond(epochTime.toLong(), 0, zoneOffset)
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    return dateTime.format(formatter)
}

fun convertEpochToHourMinute(epochTime: Int): String {
    val zoneOffset = ZoneId.systemDefault().rules.getOffset(LocalDateTime.now())
    val dateTime = LocalDateTime.ofEpochSecond(epochTime.toLong(), 0, zoneOffset)
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return dateTime.format(formatter)
}

fun convertTimeStringToSeconds(timeString: String): Int {
    val timeParts = timeString.split(":")
    val hours = timeParts[0].toInt()
    val minutes = timeParts[1].toInt()
    val seconds = timeParts[2].toInt()

    val secondsInHour = 3600
    val secondsInMinute = 60

    val totalSeconds = (hours * secondsInHour) + (minutes * secondsInMinute) + seconds

    return totalSeconds
}

fun calculateStandardDeviation(data: List<Float>, mean: Float): Float {

    val squaredDifferences = data.map { (it - mean) * (it - mean) }
    val sumOfSquaredDifferences = squaredDifferences.sum()

    val variance = sumOfSquaredDifferences / data.size

    return sqrt(variance)
}

fun calculateCoefficientOfVariation(data: List<Float>, mean: Float): Float {
    val standardDeviation = calculateStandardDeviation(data, mean)
    return standardDeviation / mean * 100
}

fun ImageView.loadImage(url: String?) {
    Glide.with(this.context)
        .load(url)
        .placeholder(ShimmerAnimation.runShimmerAnimation())
        .into(this)
}

val onedayinseconds = 86400
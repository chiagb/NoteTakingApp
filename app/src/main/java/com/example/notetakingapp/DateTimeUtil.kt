package com.example.notetakingapp

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

object DateTimeUtil {

    fun now(): Long {
        return System.currentTimeMillis()
    }

    fun formatDate(datetime: Long): String = SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault()).format(datetime)
    fun formatDateFile(datetime: Long): String = SimpleDateFormat("dd_MM_yyyy", Locale.getDefault()).format(datetime)
    fun formatDateFileWithTime(datetime: Long): String = SimpleDateFormat("dd_MM_yyyy_mm_ss", Locale.getDefault()).format(datetime)

    fun getHours(timeCreation: Long) : String =
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(timeCreation)


    fun getDate(timeCreation: Long) : String =
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(timeCreation)


}
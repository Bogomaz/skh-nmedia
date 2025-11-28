package ru.netology.nmedia.service

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateTimeService {
    // Конвертирует и форматирует UnixTime в строку с датой и временем публикации
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatUnixTime(unixDateTime: Int): String {
        val instant = Instant.ofEpochSecond(unixDateTime.toLong())
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy 'в' HH:mm", Locale("ru"))
            .withZone(ZoneId.systemDefault())
        return formatter.format(instant)
    }
}
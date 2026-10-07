package com.example.my_app.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    fun formatDate(timestamp: Long): String {
        val formatter = SimpleDateFormat("d MMM yyyy · HH:mm", Locale("tr"))
        return formatter.format(Date(timestamp))
    }

    /** "8 Ekim 2026" biçiminde, sadece gün/ay/yıl. */
    fun formatDateOnly(timestamp: Long): String {
        val formatter = SimpleDateFormat("d MMMM yyyy", Locale("tr"))
        return formatter.format(Date(timestamp))
    }

    /** "8 Eki" biçiminde, görev kartlarında kullanılan kısa tarih. */
    fun formatShortDate(timestamp: Long): String {
        val formatter = SimpleDateFormat("d MMM", Locale("tr"))
        return formatter.format(Date(timestamp))
    }

    /** "7 Ekim, Çarşamba" biçiminde, bugünün tarihi (ana ekran başlığı için). */
    fun formatTodayHeader(): String {
        val formatter = SimpleDateFormat("d MMMM, EEEE", Locale("tr"))
        return formatter.format(Date())
    }
}

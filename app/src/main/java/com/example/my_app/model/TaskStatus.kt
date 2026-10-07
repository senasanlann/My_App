package com.example.my_app.model

import androidx.compose.ui.graphics.Color
import com.example.my_app.ui.theme.StatusAmberBgDark
import com.example.my_app.ui.theme.StatusAmberBgLight
import com.example.my_app.ui.theme.StatusAmberTextDark
import com.example.my_app.ui.theme.StatusAmberTextLight
import com.example.my_app.ui.theme.StatusDoneBgDark
import com.example.my_app.ui.theme.StatusDoneBgLight
import com.example.my_app.ui.theme.StatusDoneTextDark
import com.example.my_app.ui.theme.StatusDoneTextLight
import com.example.my_app.ui.theme.StatusTodoBgDark
import com.example.my_app.ui.theme.StatusTodoBgLight
import com.example.my_app.ui.theme.StatusTodoTextDark
import com.example.my_app.ui.theme.StatusTodoTextLight

enum class TaskStatus(val displayName: String, val color: Color) {
    TODO(displayName = "Yapılacak", color = StatusTodoBgLight),
    IN_PROGRESS(displayName = "Devam ediyor", color = StatusAmberBgLight),
    DONE(displayName = "Tamamlandı", color = StatusDoneBgLight);

    fun containerColor(isDark: Boolean): Color = when (this) {
        TODO -> if (isDark) StatusTodoBgDark else StatusTodoBgLight
        IN_PROGRESS -> if (isDark) StatusAmberBgDark else StatusAmberBgLight
        DONE -> if (isDark) StatusDoneBgDark else StatusDoneBgLight
    }

    fun contentColor(isDark: Boolean): Color = when (this) {
        TODO -> if (isDark) StatusTodoTextDark else StatusTodoTextLight
        IN_PROGRESS -> if (isDark) StatusAmberTextDark else StatusAmberTextLight
        DONE -> if (isDark) StatusDoneTextDark else StatusDoneTextLight
    }
}
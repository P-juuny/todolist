package com.example.todolist.model

import java.time.LocalDate

data class DayInfo(
    val date: LocalDate,
    val isCurrentMonth: Boolean = true
)
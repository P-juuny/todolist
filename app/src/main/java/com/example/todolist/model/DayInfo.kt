package com.example.todolist.model

import java.time.LocalDate

data class DayInfo(
    val date: LocalDate,
    val normalTaskCount: Int = 0,
    val fixedTaskCount: Int = 0,
    val isCurrentMonth: Boolean = true
)
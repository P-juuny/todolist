package com.example.todolist.repository

import com.example.todolist.model.DayInfo
import com.example.todolist.util.calculator.CalendarDateCalculator
import java.time.YearMonth

class CalendarRepository {
    private val dateCalculator = CalendarDateCalculator()

    fun loadMonthData(yearMonth: YearMonth): List<DayInfo> {
        return dateCalculator.calculateMonthDates(yearMonth)
    }
}
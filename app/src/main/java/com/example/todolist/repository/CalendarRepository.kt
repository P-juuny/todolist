package com.example.todolist.repository

import android.content.Context
import com.example.todolist.model.DayInfo
import com.example.todolist.util.calculator.CalendarDateCalculator
import java.time.LocalDate
import java.time.YearMonth

class CalendarRepository(private val context: Context) {
    private val dateCalculator = CalendarDateCalculator()
    private val prefs = context.getSharedPreferences("calendar_prefs", Context.MODE_PRIVATE)

    fun loadMonthData(yearMonth: YearMonth): List<DayInfo> {
        return dateCalculator.calculateMonthDates(yearMonth)
    }

    fun getSavedMonth(): YearMonth? {
        val savedYear = prefs.getInt("saved_year", -1)
        val savedMonth = prefs.getInt("saved_month", -1)

        return if (savedYear != -1 && savedMonth != -1) {
            YearMonth.of(savedYear, savedMonth)
        } else null
    }

    fun saveMonth(yearMonth: YearMonth) {
        prefs.edit().apply {
            putInt("saved_year", yearMonth.year)
            putInt("saved_month", yearMonth.monthValue)
            apply()
        }
    }
}

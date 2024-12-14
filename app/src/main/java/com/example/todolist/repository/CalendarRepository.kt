package com.example.todolist.repository

import android.content.Context
import com.example.todolist.model.DayInfo
import com.example.todolist.util.calculator.CalendarDateCalculator
import java.time.LocalDate
import java.time.YearMonth

class CalendarRepository(private val context: Context) {
    private val dateCalculator = CalendarDateCalculator()
    private val prefs = context.getSharedPreferences("calendar_prefs", Context.MODE_PRIVATE)

    // 캐시 추가로 반복적인 저장소 접근 최소화
    private var cachedMonth: YearMonth? = null

    fun loadMonthData(yearMonth: YearMonth): List<DayInfo> {
        return dateCalculator.calculateMonthDates(yearMonth)
    }

    fun getSavedMonth(): YearMonth? {
        // 캐시된 값이 있으면 반환
        cachedMonth?.let { return it }

        val savedYear = prefs.getInt("saved_year", -1)
        val savedMonth = prefs.getInt("saved_month", -1)

        return if (savedYear != -1 && savedMonth != -1) {
            YearMonth.of(savedYear, savedMonth).also {
                cachedMonth = it
            }
        } else null
    }

    fun saveMonth(yearMonth: YearMonth) {
        // 캐시 업데이트
        cachedMonth = yearMonth

        prefs.edit().apply {
            putInt("saved_year", yearMonth.year)
            putInt("saved_month", yearMonth.monthValue)
            apply()
        }
    }

    fun shouldUpdateMonth(currentMonth: YearMonth): Boolean {
        val today = LocalDate.now()
        val savedMonth = getSavedMonth() ?: return true

        // 조건 체크 로직 단순화
        return savedMonth != YearMonth.from(today) ||
                (today.dayOfMonth == 1 && savedMonth.monthValue != today.monthValue)
    }
}
package com.example.todolist.util.calculator

import com.example.todolist.model.DayInfo
import java.time.YearMonth

class CalendarDateCalculator {
    fun calculateMonthDates(yearMonth: YearMonth): List<DayInfo> {
        val firstDay = yearMonth.atDay(1)
        val lastDay = yearMonth.atEndOfMonth()
        val firstDayOfWeek = firstDay.dayOfWeek.value

        val startDate = firstDay.minusDays((firstDayOfWeek - 1).toLong())
        val endDate = lastDay.plusDays((7 - lastDay.dayOfWeek.value).toLong())

        val dayInfoList = mutableListOf<DayInfo>()
        var currentDate = startDate

        while (!currentDate.isAfter(endDate)) {
            dayInfoList.add(
                DayInfo(
                    date = currentDate,
                    isCurrentMonth = currentDate.month == yearMonth.month
                )
            )
            currentDate = currentDate.plusDays(1)
        }

        return dayInfoList
    }
}
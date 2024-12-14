package com.example.todolist.util.calculator

import com.example.todolist.model.DayInfo
import java.time.YearMonth

class CalendarDateCalculator {
    fun calculateMonthDates(yearMonth: YearMonth): List<DayInfo> {
        val firstDay = yearMonth.atDay(1)
        val lastDay = yearMonth.atEndOfMonth()

        // 시작일과 종료일 계산을 더 명확하게
        val daysToSubtract = (firstDay.dayOfWeek.value - 1).toLong()
        val daysToAdd = (7 - lastDay.dayOfWeek.value).toLong()

        val startDate = firstDay.minusDays(daysToSubtract)
        val endDate = lastDay.plusDays(daysToAdd)

        // ArrayList로 초기 용량 설정하여 메모리 재할당 방지
        return ArrayList<DayInfo>((42)).apply {
            var currentDate = startDate
            while (!currentDate.isAfter(endDate)) {
                add(DayInfo(
                    date = currentDate,
                    isCurrentMonth = currentDate.month == yearMonth.month
                ))
                currentDate = currentDate.plusDays(1)
            }
        }
    }
}
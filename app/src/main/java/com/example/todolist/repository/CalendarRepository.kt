package com.example.todolist.repository

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import com.example.todolist.model.FixedTaskItem
import com.example.todolist.viewmodel.CalendarViewModel
import java.time.LocalDate
import java.time.YearMonth

class CalendarRepository {
    private val auth = Firebase.auth
    private val database = Firebase.database
    private val baseRef = database.getReference("Users/${auth.currentUser?.uid ?: ""}")

    fun loadMonthData(yearMonth: YearMonth, callback: (List<CalendarViewModel.DayInfo>) -> Unit) {
        val firstDay = yearMonth.atDay(1)
        val lastDay = yearMonth.atEndOfMonth()
        val firstDayOfWeek = firstDay.dayOfWeek.value

        val startDate = firstDay.minusDays((firstDayOfWeek - 1).toLong())
        val endDate = lastDay.plusDays((7 - lastDay.dayOfWeek.value).toLong())

        baseRef.get().addOnSuccessListener { snapshot ->
            val dayInfoList = mutableListOf<CalendarViewModel.DayInfo>()
            var currentDate = startDate

            while (!currentDate.isAfter(endDate)) {
                val dateKey = "${currentDate.year}-${currentDate.monthValue}-${currentDate.dayOfMonth}"

                val normalTaskCount = snapshot
                    .child("NormalTasks")
                    .child(dateKey)
                    .childrenCount.toInt()

                val fixedTaskCount = snapshot
                    .child("FixedTasks")
                    .children
                    .count { taskSnapshot ->
                        val task = taskSnapshot.getValue(FixedTaskItem::class.java)
                        when(currentDate.dayOfWeek.value) {
                            1 -> task?.monday
                            2 -> task?.tuesday
                            3 -> task?.wednesday
                            4 -> task?.thursday
                            5 -> task?.friday
                            6 -> task?.saturday
                            7 -> task?.sunday
                            else -> false
                        } ?: false
                    }

                dayInfoList.add(
                    CalendarViewModel.DayInfo(
                        date = currentDate,
                        normalTaskCount = normalTaskCount,
                        fixedTaskCount = fixedTaskCount,
                        isCurrentMonth = currentDate.month == yearMonth.month
                    )
                )

                currentDate = currentDate.plusDays(1)
            }
            callback(dayInfoList)
        }
    }
}
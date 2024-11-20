package com.example.todolist.repository

import android.util.Log
import com.example.todolist.model.DayInfo
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import java.time.YearMonth

class CalendarRepository {
    private val auth = Firebase.auth
    private val database = Firebase.database
    private val baseRef = database.getReference("Users/${auth.currentUser?.uid ?: ""}")

    fun loadMonthData(yearMonth: YearMonth, callback: (List<DayInfo>) -> Unit) {
        val firstDay = yearMonth.atDay(1)
        val lastDay = yearMonth.atEndOfMonth()
        val firstDayOfWeek = firstDay.dayOfWeek.value

        val startDate = firstDay.minusDays((firstDayOfWeek - 1).toLong())
        val endDate = lastDay.plusDays((7 - lastDay.dayOfWeek.value).toLong())

        baseRef.get().addOnSuccessListener { 
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
            callback(dayInfoList)
        }
        .addOnFailureListener { exception ->
            Log.e("CalendarRepository", "Error loading month data", exception)
            callback(emptyList())
        }
    }
}
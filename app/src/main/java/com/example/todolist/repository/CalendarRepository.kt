package com.example.todolist.repository

import android.util.Log
import com.example.todolist.model.DayInfo
import com.example.todolist.util.calculator.CalendarDateCalculator
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import java.time.YearMonth

class CalendarRepository {
    private val auth = Firebase.auth
    private val database = Firebase.database
    private val baseRef = database.getReference("Users/${auth.currentUser?.uid ?: ""}")
    private val dateCalculator = CalendarDateCalculator()

    fun loadMonthData(yearMonth: YearMonth, callback: (List<DayInfo>) -> Unit) {
        baseRef.get()
        .addOnSuccessListener {
            val dayInfoList = dateCalculator.calculateMonthDates(yearMonth)
            callback(dayInfoList)
        }
        .addOnFailureListener { exception ->
            Log.e("CalendarRepository", "Error loading month data", exception)
            callback(emptyList())
        }
    }
}
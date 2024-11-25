package com.example.todolist.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todolist.model.DayInfo
import com.example.todolist.repository.CalendarRepository
import java.time.LocalDate
import java.time.YearMonth

class CalendarViewModel : ViewModel() {
    private val _calendarItems = MutableLiveData<List<DayInfo>>()
    val calendarItems: LiveData<List<DayInfo>> get() = _calendarItems

    private val _currentMonth = MutableLiveData<YearMonth>()
    val currentMonth: LiveData<YearMonth> get() = _currentMonth

    private val _selectedDate = MutableLiveData<LocalDate>()
    val selectedDate: LiveData<LocalDate> get() = _selectedDate

    private val repository = CalendarRepository()
    private var savedContext: Context? = null

    fun initializeContext(context: Context) {
        savedContext = context.applicationContext
    }

    fun init() {
        val savedMonth = getSavedMonth() ?: YearMonth.now()
        setCurrentMonth(savedMonth)
    }

    private fun getSavedMonth(): YearMonth? {
        val prefs = savedContext?.getSharedPreferences("calendar_prefs", Context.MODE_PRIVATE)
        val savedYear = prefs?.getInt("saved_year", -1) ?: -1
        val savedMonth = prefs?.getInt("saved_month", -1) ?: -1

        return if (savedYear != -1 && savedMonth != -1) {
            YearMonth.of(savedYear, savedMonth)
        } else null
    }

    private fun saveMonth(yearMonth: YearMonth) {
        savedContext?.getSharedPreferences("calendar_prefs", Context.MODE_PRIVATE)?.edit()?.apply {
            putInt("saved_year", yearMonth.year)
            putInt("saved_month", yearMonth.monthValue)
            apply()
        }
    }

    fun setCurrentMonth(yearMonth: YearMonth) {
        val currentMonth = _currentMonth.value
        // 실제로 달이 변경될 때만 저장
        if (currentMonth?.year != yearMonth.year || currentMonth?.monthValue != yearMonth.monthValue) {
            _currentMonth.value = yearMonth
            saveMonth(yearMonth)
            _calendarItems.value = repository.loadMonthData(yearMonth)
        }
    }

    fun formatCurrentMonth(): String {
        val month = _currentMonth.value ?: YearMonth.now()
        return "${month.month.name.lowercase().capitalize()} ${month.year}"
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }
}
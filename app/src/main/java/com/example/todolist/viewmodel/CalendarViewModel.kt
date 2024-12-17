package com.example.todolist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todolist.model.DayInfo
import com.example.todolist.repository.CalendarRepository
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale

class CalendarViewModel : ViewModel() {
    private val _calendarItems = MutableLiveData<List<DayInfo>>()
    val calendarItems: LiveData<List<DayInfo>> get() = _calendarItems

    private val _currentMonth = MutableLiveData<YearMonth>()
    val currentMonth: LiveData<YearMonth> get() = _currentMonth

    private val _selectedDate = MutableLiveData<LocalDate>()
    val selectedDate: LiveData<LocalDate> get() = _selectedDate

    private val repository = CalendarRepository()
    private var lastCheckedDate = LocalDate.now()

    init {
        setCurrentMonth(YearMonth.now())  // 앱 시작시 한번만 초기화
    }

    fun setCurrentMonth(yearMonth: YearMonth) {
        _currentMonth.value = yearMonth
        _calendarItems.value = repository.loadMonthData(yearMonth)
    }

    fun formatCurrentMonth(): String {
        val month = _currentMonth.value ?: YearMonth.now()
        return "${month.month.name.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(
            Locale.getDefault()) else it.toString() }} ${month.year}"
    }

    fun checkDateChange() {
        val today = LocalDate.now()
        val currentMonth = _currentMonth.value ?: YearMonth.now()

        if (today != lastCheckedDate && shouldUpdateMonth(currentMonth)) {
            setCurrentMonth(YearMonth.now())
            lastCheckedDate = today
        }
    }

    private fun shouldUpdateMonth(currentMonth: YearMonth): Boolean {
        val today = LocalDate.now()
        return currentMonth != YearMonth.from(today) && today.dayOfMonth == 1
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }
}
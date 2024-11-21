package com.example.todolist.viewmodel

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

    init {
        setCurrentMonth(YearMonth.now())
    }

    fun setCurrentMonth(yearMonth: YearMonth) {
        _currentMonth.value = yearMonth
        loadMonth(yearMonth)
    }

    private fun loadMonth(yearMonth: YearMonth) {
        repository.loadMonthData(yearMonth) { dayInfoList ->
            _calendarItems.postValue(dayInfoList)
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
package com.example.todolist.viewmodel

import android.content.Context
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

    private lateinit var repository: CalendarRepository

    fun initializeContext(context: Context) {
        repository = CalendarRepository(context.applicationContext)
    }

    fun init() {
        val savedMonth = repository.getSavedMonth() ?: YearMonth.now()
        setCurrentMonth(savedMonth)

        // 월 전환 체크
        if (repository.shouldUpdateMonth(savedMonth)) {
            setCurrentMonth(YearMonth.now())
        }
    }

    fun setCurrentMonth(yearMonth: YearMonth) {
        val currentMonth = _currentMonth.value
        if (currentMonth?.year != yearMonth.year || currentMonth.monthValue != yearMonth.monthValue) {
            _currentMonth.value = yearMonth
            repository.saveMonth(yearMonth)
            _calendarItems.value = repository.loadMonthData(yearMonth)
        }
    }

    fun formatCurrentMonth(): String {
        val month = _currentMonth.value ?: YearMonth.now()
        return "${month.month.name.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(
            Locale.getDefault()) else it.toString() }} ${month.year}"
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }
}
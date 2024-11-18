package com.example.todolist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todolist.model.FixedTaskItem
import com.example.todolist.model.TaskItem
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import java.time.LocalDate
import java.time.YearMonth

class CalendarViewModel : ViewModel() {
    // UI 상태를 위한 데이터 클래스
    data class DayInfo(
        val date: LocalDate,
        val normalTaskCount: Int = 0,
        val fixedTaskCount: Int = 0,
        val isCurrentMonth: Boolean = true
    )

    private val _calendarItems = MutableLiveData<List<DayInfo>>()
    val calendarItems: LiveData<List<DayInfo>> get() = _calendarItems

    private val _currentMonth = MutableLiveData<YearMonth>()
    val currentMonth: LiveData<YearMonth> get() = _currentMonth

    private val _selectedDate = MutableLiveData<LocalDate>()
    val selectedDate: LiveData<LocalDate> get() = _selectedDate

    private val auth = Firebase.auth
    private val currentUser = auth.currentUser?.uid ?: ""
    private val database = Firebase.database
    private val baseRef = database.getReference("Users/$currentUser")

    init {
        setCurrentMonth(YearMonth.now())
    }

    fun setCurrentMonth(yearMonth: YearMonth) {
        _currentMonth.value = yearMonth
        loadMonth(yearMonth)
    }

    fun formatCurrentMonth(): String {
        val month = _currentMonth.value ?: YearMonth.now()
        return "${month.month.name.toLowerCase().capitalize()} ${month.year}"
    }

    private fun loadMonth(yearMonth: YearMonth) {
        val firstDay = yearMonth.atDay(1)
        val lastDay = yearMonth.atEndOfMonth()
        val firstDayOfWeek = firstDay.dayOfWeek.value

        // 이전 달의 날짜들을 포함하여 달력에 표시할 전체 날짜 범위 계산
        val startDate = firstDay.minusDays((firstDayOfWeek - 1).toLong())
        val endDate = lastDay.plusDays((7 - lastDay.dayOfWeek.value).toLong())

        // 각 날짜별 할 일 개수 로드
        baseRef.get().addOnSuccessListener { snapshot ->
            val dayInfoList = mutableListOf<DayInfo>()
            var currentDate = startDate

            while (!currentDate.isAfter(endDate)) {
                val dateKey = "${currentDate.year}-${currentDate.monthValue}-${currentDate.dayOfMonth}"

                // 일반 할 일 개수
                val normalTaskCount = snapshot
                    .child("NormalTasks")
                    .child(dateKey)
                    .childrenCount.toInt()

                // 고정 할 일 중 해당 요일에 해당하는 것들의 개수
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
                    DayInfo(
                        date = currentDate,
                        normalTaskCount = normalTaskCount,
                        fixedTaskCount = fixedTaskCount,
                        isCurrentMonth = currentDate.month == yearMonth.month
                    )
                )

                currentDate = currentDate.plusDays(1)
            }

            _calendarItems.value = dayInfoList
        }
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    // Firebase 경로 생성 헬퍼 함수
    fun getDateKey(date: LocalDate): String {
        return "${date.year}-${date.monthValue}-${date.dayOfMonth}"
    }
}
package com.example.todolist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todolist.model.CalendarItem
import com.example.todolist.model.FixedTaskItem
import com.example.todolist.model.TaskItem
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import java.time.LocalDate
import java.time.YearMonth

class CalendarViewModel : ViewModel() {
    private val _calendarItems = MutableLiveData<MutableList<CalendarItem>>()
    val calendarItems: LiveData<MutableList<CalendarItem>> get() = _calendarItems

    private val _currentMonth = MutableLiveData<YearMonth>()
    val currentMonth: LiveData<YearMonth> get() = _currentMonth

    private val _selectedDate = MutableLiveData<LocalDate>()
    val selectedDate: LiveData<LocalDate> get() = _selectedDate

    private val auth = Firebase.auth
    private val currentUser = auth.currentUser
    private val database = Firebase.database
    private val todoRef = database.getReference("Todo").child(currentUser?.uid ?: "")
    private val fixedTodoRef = database.getReference("FixedTodo").child(currentUser?.uid ?: "")

    init {
        setCurrentMonth(YearMonth.now())
    }

    fun setCurrentMonth(yearMonth: YearMonth) {
        _currentMonth.value = yearMonth
        loadMonth(yearMonth.year, yearMonth.monthValue)
    }

    fun formatCurrentMonth(): String {
        val month = _currentMonth.value ?: YearMonth.now()
        return "${month.month.name.toLowerCase().capitalize()} ${month.year}"
    }

    fun addTaskToDate(date: LocalDate, taskId: String) {
        val dateKey = "${date.year}-${date.monthValue}-${date.dayOfMonth}"
        todoRef.child(dateKey).child(taskId).setValue(true)
    }

    fun removeTaskFromDate(date: LocalDate, taskId: String) {
        val dateKey = "${date.year}-${date.monthValue}-${date.dayOfMonth}"
        todoRef.child(dateKey).child(taskId).removeValue()
    }

    fun loadMonth(year: Int, month: Int) {
        val yearMonth = YearMonth.of(year, month)
        val firstDay = yearMonth.atDay(1)
        val lastDay = yearMonth.atEndOfMonth()
        val firstDayOfWeek = firstDay.dayOfWeek.value

        todoRef.get().addOnSuccessListener { todoSnapshot ->
            fixedTodoRef.get().addOnSuccessListener { fixedSnapshot ->
                val newList = mutableListOf<CalendarItem>()

                // 이전 달의 날짜들 추가
                for (i in 1 until firstDayOfWeek) {
                    val prevDate = firstDay.minusDays(firstDayOfWeek - i.toLong())
                    newList.add(CalendarItem(
                        year = prevDate.year,
                        month = prevDate.monthValue,
                        day = prevDate.dayOfMonth,
                        dayOfWeek = prevDate.dayOfWeek.value,
                        tasks = listOf()
                    ))
                }

                // 현재 달의 날짜들
                var currentDate = firstDay
                while (!currentDate.isAfter(lastDay)) {
                    val dayTasks = mutableListOf<String>()

                    // 해당 날짜의 할일 가져오기
                    val dateKey = "${currentDate.year}-${currentDate.monthValue}-${currentDate.dayOfMonth}"
                    todoSnapshot.child(dateKey).children.forEach { taskSnapshot ->
                        taskSnapshot.key?.let { dayTasks.add(it) }
                    }

                    // 해당 요일의 고정 할일 찾기
                    fixedSnapshot.children.forEach { dataSnapshot ->
                        dataSnapshot.getValue(FixedTaskItem::class.java)?.let { task ->
                            val isTaskForSelectedDay = when(currentDate.dayOfWeek.value) {
                                1 -> task.monday
                                2 -> task.tuesday
                                3 -> task.wednesday
                                4 -> task.thursday
                                5 -> task.friday
                                6 -> task.saturday
                                7 -> task.sunday
                                else -> false
                            }
                            if (isTaskForSelectedDay) {
                                task.id?.let { dayTasks.add(it) }
                            }
                        }
                    }

                    newList.add(CalendarItem(
                        year = currentDate.year,
                        month = currentDate.monthValue,
                        day = currentDate.dayOfMonth,
                        dayOfWeek = currentDate.dayOfWeek.value,
                        tasks = dayTasks
                    ))

                    currentDate = currentDate.plusDays(1)
                }

                _calendarItems.value = newList
            }
        }
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        prepareTasksForDate(date)
    }

    // 특정 날짜의 할 일을 준비하는 함수
    fun prepareTasksForDate(date: LocalDate) {
        val dateKey = "${date.year}-${date.monthValue}-${date.dayOfMonth}"

        // Todo 데이터 준비
        todoRef.child(dateKey).get().addOnSuccessListener { snapshot ->
            val tasks = mutableListOf<TaskItem>()
            snapshot.children.forEach { taskSnapshot ->
                taskSnapshot.getValue(TaskItem::class.java)?.let { task ->
                    tasks.add(task)
                }
            }
        }

        // FixedTodo 데이터 준비 (선택된 요일에 해당하는 것만)
        fixedTodoRef.get().addOnSuccessListener { snapshot ->
            val fixedTasks = mutableListOf<FixedTaskItem>()
            snapshot.children.forEach { dataSnapshot ->
                dataSnapshot.getValue(FixedTaskItem::class.java)?.let { task ->
                    val isTaskForSelectedDay = when(date.dayOfWeek.value) {
                        1 -> task.monday
                        2 -> task.tuesday
                        3 -> task.wednesday
                        4 -> task.thursday
                        5 -> task.friday
                        6 -> task.saturday
                        7 -> task.sunday
                        else -> false
                    }
                    if (isTaskForSelectedDay) {
                        fixedTasks.add(task)
                    }
                }
            }
        }
    }
}
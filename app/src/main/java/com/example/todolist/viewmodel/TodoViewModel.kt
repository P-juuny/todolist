package com.example.todolist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todolist.model.TaskItem
import com.example.todolist.repository.TodoRepository
import java.time.LocalDate

// 달력에서 오늘 날짜를 누르면 selectedDataTasks, todayTasks livedata가 동시에 물려 하나만 바꿔줘도 영향을 미침
class TodoViewModel : ViewModel() {
    // 선택된 날짜의 할일들 관리
    private val _selectedDateTasks = MutableLiveData<MutableList<TaskItem>>()
    val selectedDateTasks: LiveData<MutableList<TaskItem>> get() = _selectedDateTasks

    // 오늘의 할일을 관리(스톱워치, 메인화면)
    private val _todayTasks = MutableLiveData<MutableList<TaskItem>>()
    val todayTasks: LiveData<MutableList<TaskItem>> get() = _todayTasks

    private val repository = TodoRepository()

    init {
        loadTodayTasks()
    }

    private fun loadTodayTasks() {
        val today = LocalDate.now()
        repository.observeTasksForDate(today, _todayTasks)
    }

    fun loadTasksForDate(date: LocalDate) {
        repository.observeTasksForDate(date, _selectedDateTasks)
    }

    fun addTodo(todoText: String, date: LocalDate) {
        repository.addTask(date, TaskItem(todoText))
    }

    fun deleteTodo(position: Int, date: LocalDate) {
        val task = _selectedDateTasks.value?.get(position)
        repository.deleteTask(date, task?.id)
    }

    fun updateTodoCheck(position: Int, isChecked: Boolean, date: LocalDate) {
        val currentList = _selectedDateTasks.value ?: return
        val task = currentList[position]
        task.isChecked = isChecked
        repository.updateTask(date, task.id, task)
    }
}
package com.example.todolist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todolist.model.FixedTaskItem
import com.example.todolist.repository.FixedTodoRepository

class FixedToDoViewModel : ViewModel() {
    private val _fixedtodoList = MutableLiveData<MutableList<FixedTaskItem>>()
    val fixedtodoList: LiveData<MutableList<FixedTaskItem>> get() = _fixedtodoList

    private val repository = FixedTodoRepository()

    init {
        repository.observeFixedTodos(_fixedtodoList)
    }

    fun addTodo(task: FixedTaskItem) {
        repository.postFixedTodo(task)
    }

    fun deleteTodo(position: Int) {
        val currentList = _fixedtodoList.value ?: return
        val task = currentList[position]
        repository.deleteFixedTodo(task.id)
    }

    fun updateTodoCheck(position: Int, isChecked: Boolean) {
        val currentList = _fixedtodoList.value ?: return
        val task = currentList[position]
        task.isChecked = isChecked
        repository.updateFixedTodo(task.id, task)
    }

    fun updateDate(Id: Int, position: Int, isChecked: Boolean) {
        val currentList = _fixedtodoList.value ?: return
        val task = currentList[position]

        when (Id) {
            0 -> task.monday = isChecked
            1 -> task.tuesday = isChecked
            2 -> task.wednesday = isChecked
            3 -> task.thursday = isChecked
            4 -> task.friday = isChecked
            5 -> task.saturday = isChecked
            6 -> task.sunday = isChecked
        }

        repository.updateFixedTodo(task.id, task)
    }
}
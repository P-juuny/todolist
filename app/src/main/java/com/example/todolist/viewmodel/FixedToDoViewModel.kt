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
        repository.addFixedTodo(task)
    }

    fun deleteTodo(position: Int) {
        val currentList = _fixedtodoList.value ?: return
        val task = currentList[position]
        repository.deleteFixedTodo(task.id)
    }

    fun updateDate(Id: Int, position: Int, isChecked: Boolean) {
        val currentList = _fixedtodoList.value ?: return
        val task = currentList[position]

        when (Id) {
            1 -> task.monday = isChecked
            2 -> task.tuesday = isChecked
            3 -> task.wednesday = isChecked
            4 -> task.thursday = isChecked
            5 -> task.friday = isChecked
            6 -> task.saturday = isChecked
            7 -> task.sunday = isChecked
        }

        repository.updateFixedTodo(task.id, task)
    }
}
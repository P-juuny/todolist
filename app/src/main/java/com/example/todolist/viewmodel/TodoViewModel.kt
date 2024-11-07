package com.example.todolist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todolist.model.TaskItem

class TodoViewModel : ViewModel() {
    private val _todoList = MutableLiveData<MutableList<TaskItem>>()
    val todoList: LiveData<MutableList<TaskItem>> get() = _todoList

    fun addTodo(task: TaskItem) {
        val currentList = _todoList.value ?: mutableListOf()
        currentList.add(task)
        _todoList.value = currentList
    }

    fun deleteTodo(position: Int) {
        val currentList = _todoList.value ?: return
        if (position >= 0 && position < currentList.size) {
            currentList.removeAt(position)
            _todoList.value = currentList
        }
        //else처리 해주기
    }

    fun updateTodoCheck(position: Int, isChecked: Boolean) {
        val currentList = _todoList.value ?: return
        if (position >= 0 && position < currentList.size) {
            currentList[position].isChecked = isChecked
            _todoList.value = currentList
        }
        //else처리 해주기
    }
}
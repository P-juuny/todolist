package com.example.todolist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todolist.model.TaskItem

class TodoViewModel : ViewModel() {
    //내부에서 사용하는 라이브 데이터 따라서 외부에서는 볼 수 없게 설정
    private val _todoList = MutableLiveData<MutableList<TaskItem>>()
    //외부에서 관찰 가능한 라이브 데이터 UI가 이 라이브데이터를 관찰
    val todoList: LiveData<MutableList<TaskItem>> get() = _todoList

    fun addTodo(task: TaskItem) {
        //_todoList는 MutableLiveData의 인스턴스로 바로 추가 불가
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
    }

    fun updateTodoCheck(position: Int, isChecked: Boolean) {
        val currentList = _todoList.value ?: return
        if (position >= 0 && position < currentList.size) {
            currentList[position].isChecked = isChecked
            _todoList.value = currentList
        }
    }
}
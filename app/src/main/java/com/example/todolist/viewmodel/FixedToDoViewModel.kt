package com.example.todolist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todolist.model.FixedTaskItem

class FixedToDoViewModel : ViewModel() {
    // 내부 데이터
    private val _fixedtodoList = MutableLiveData<MutableList<FixedTaskItem>>()
    // 외부 UI 데이터
    val fixedtodoList: LiveData<MutableList<FixedTaskItem>> get() = _fixedtodoList

    fun addTodo(task: FixedTaskItem) {
        // observer에는 객체의 변화를 알려줘야하기 때문에 currentList 생성
        val currentList = _fixedtodoList.value ?: mutableListOf()
        currentList.add(task)
        _fixedtodoList.value = currentList
    }

    fun deleteTodo(position: Int) {
        val currentList = _fixedtodoList.value ?: return
        if (position >= 0 && position < currentList.size) {
            currentList.removeAt(position)
            _fixedtodoList.value = currentList
        }
    }

    fun updateTodoCheck(position: Int, isChecked: Boolean) {
        val currentList = _fixedtodoList.value ?: return
        if (position >= 0 && position < currentList.size) {
            currentList[position].isChecked = isChecked
            _fixedtodoList.value = currentList
        }
    }

    fun updateDate(Id: Int, position: Int, isChecked: Boolean) {
        val currentList = _fixedtodoList.value ?: return
        if (position >= 0 && position < currentList.size) {
            val item = currentList[position]
            when(Id) {
                0 -> item.monday = isChecked
                1 -> item.tuesday = isChecked
                2 -> item.wednesday = isChecked
                3 -> item.thursday = isChecked
                4 -> item.friday = isChecked
                5 -> item.saturday = isChecked
                6 -> item.sunday = isChecked
            }
            _fixedtodoList.value = currentList
        }
    }
}
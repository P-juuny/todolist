package com.example.todolist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todolist.model.FixedTaskItem

class FixedToDoViewModel : ViewModel() {
    //내부에서 사용하는 라이브 데이터 따라서 외부에서는 볼 수 없게 설정
    private val _fixedtodoList = MutableLiveData<MutableList<FixedTaskItem>>()
    //외부에서 관찰 가능한 라이브 데이터 UI가 이 라이브데이터를 관찰
    val fixedtodoList: LiveData<MutableList<FixedTaskItem>> get() = _fixedtodoList

    fun addTodo(task: FixedTaskItem) {
        //_todoList는 MutableLiveData의 인스턴스로 바로 추가 불가
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
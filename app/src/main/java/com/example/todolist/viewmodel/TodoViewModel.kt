package com.example.todolist.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todolist.model.TaskItem
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.time.LocalDate

class TodoViewModel : ViewModel() {
    private val _todoList = MutableLiveData<MutableList<TaskItem>>()
    val todoList: LiveData<MutableList<TaskItem>> get() = _todoList

    private val _selectedDateTasks = MutableLiveData<MutableList<TaskItem>>() // 선택된 날짜의 할 일 목록 - 건수 추가
    val selectedDateTasks: LiveData<MutableList<TaskItem>> get() = _selectedDateTasks

    private val _todayTasks = MutableLiveData<MutableList<TaskItem>>()  // EntryFragment에 오늘 할 일만 보이기 위해서 추가한 로직 - 건수 추가
    val todayTasks: LiveData<MutableList<TaskItem>> get() = _todayTasks

    private val auth = Firebase.auth
    private val currentUser = auth.currentUser

    val database = Firebase.database
    val TodoRef = database.getReference("Users/${auth.currentUser?.uid}/ToDo")

    init {
        LoadData()
    }

    fun LoadData() {
        TodoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newList = mutableListOf<TaskItem>()

                for (taskNode in snapshot.children) {   // 직접 TaskItem들을 가져옴 - 건수 추가
                    taskNode.getValue(TaskItem::class.java)?.let {
                        it.id = taskNode.key // ID 설정을 보장
                        newList.add(it) // TaskItem을 리스트에 추가
                    }
                }
                _todoList.value = newList
                updateTodayTasks() // 데이터가 변경될 때 마다 오늘 할 일 업데이트 - 건수 추가
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Data load cancelled", error.toException())
            }

        })
    }

    fun addTodo(task: TaskItem) {
        val currentList = _todoList.value ?: mutableListOf()
        val taskRef = TodoRef.push()

        currentList.add(task)
        _todoList.value = currentList

        task.id = taskRef.key
        taskRef.setValue(task)
    }

    fun deleteTodo(position: Int) {
        val currentList = _todoList.value ?: return
        val task = currentList[position]

        currentList.removeAt(position)
        _todoList.value = currentList

        task.id?.let {
            TodoRef.child(it).removeValue()
        }
    }

    fun updateTodoCheck(position: Int, isChecked: Boolean) {
        val currentList = _todoList.value ?: return
        val task = currentList[position]
        currentList[position].isChecked = isChecked
        _todoList.value = currentList

        task.id?.let {
            TodoRef.child(it).setValue(task)
        }
    }

    fun updateSelectedDate(date: LocalDate) {   // 선택된 날짜의 할 일 목록 업데이트 - 건수 추가
        val filteredList = _todoList.value?.filter {
            it.year == date.year &&
                    it.month == date.monthValue &&
                    it.day == date.dayOfMonth
        }?.toMutableList() ?: mutableListOf()

        _selectedDateTasks.value = filteredList
    }

    fun addTodoWithDate(task: String, date: LocalDate) {    // 선택된 날짜에 할 일 추가 - 건수 추가
        val taskItem = TaskItem(
            task = task,
            year = date.year,
            month = date.monthValue,
            day = date.dayOfMonth
        )
        addTodo(taskItem)
    }

    private fun updateTodayTasks() {
        val today = LocalDate.now()
        val todayList = _todoList.value?.filter {
            it.year == today.year &&
                    it.month == today.monthValue &&
                    it.day == today.dayOfMonth
        }?.toMutableList() ?: mutableListOf()

        _todayTasks.value = todayList
    }
}
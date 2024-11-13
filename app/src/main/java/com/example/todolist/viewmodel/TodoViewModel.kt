package com.example.todolist.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todolist.model.TaskItem
import com.google.firebase.Firebase
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

    private val auth = Firebase.auth
    private val currentUser = auth.currentUser

    val database = Firebase.database
    val myRef = database.getReference("Todo").child(currentUser?.uid ?: "")

    init {
        LoadData()
    }

    fun LoadData() {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newList = mutableListOf<TaskItem>()

                for (dateNode in snapshot.children) {  // 날짜별 노드 - 건수 추가
                    for (taskNode in dateNode.children) {  // 할일 노드 - 건수 추가
                        taskNode.getValue(TaskItem::class.java)?.let { task ->
                            newList.add(task)
                        }
                    }
                }
                _todoList.value = newList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Data load cancelled", error.toException())
            }

        })
    }

    fun addTodo(task: TaskItem) {
        val currentList = _todoList.value ?: mutableListOf()
        val taskRef = myRef.push()

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
            myRef.child(it).removeValue()
        }
    }

    fun updateTodoCheck(position: Int, isChecked: Boolean) {
        val currentList = _todoList.value ?: return
        val task = currentList[position]
        currentList[position].isChecked = isChecked
        _todoList.value = currentList

        task.id?.let {
            myRef.child(it).setValue(task)
        }
    }

    fun updateSelectedDate(date: LocalDate) {   // 선택된 날짜의 할 일 목록 업데이트 - 건수 추가
        val filteredList = _todoList.value?.filter { task ->
            task.year == date.year &&
                    task.month == date.monthValue &&
                    task.day == date.dayOfMonth
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
}
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
    private val _selectedDateTasks = MutableLiveData<MutableList<TaskItem>>()
    val selectedDateTasks: LiveData<MutableList<TaskItem>> get() = _selectedDateTasks

    private val _todayTasks = MutableLiveData<MutableList<TaskItem>>()
    val todayTasks: LiveData<MutableList<TaskItem>> get() = _todayTasks

    private val auth = Firebase.auth
    private val database = Firebase.database
    private val baseRef by lazy {
        val userId = auth.currentUser?.uid ?: "defaultUser"
        database.getReference("Users/$userId/NormalTasks")
    }
    //val TodoRef = database.getReference("Users/${auth.currentUser?.uid}/ToDo")

    init {
        loadTodayTasks()
    }

    private fun getDateKey(date: LocalDate): String {
        return "${date.year}-${date.monthValue}-${date.dayOfMonth}"
    }

    private fun loadTodayTasks() {
        val today = LocalDate.now()
        val dateKey = getDateKey(today)

        baseRef.child(dateKey).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newList = mutableListOf<TaskItem>()

                snapshot.children.forEach { taskSnapshot ->
                    taskSnapshot.getValue(TaskItem::class.java)?.let { task ->
                        task.id = taskSnapshot.key
                        newList.add(task)
                    }
                }
                _todayTasks.value = newList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Data load cancelled", error.toException())
            }
        })
    }

    fun loadTasksForDate(date: LocalDate) {
        val dateKey = getDateKey(date)

        baseRef.child(dateKey).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newList = mutableListOf<TaskItem>()

                snapshot.children.forEach { taskSnapshot ->
                    taskSnapshot.getValue(TaskItem::class.java)?.let { task ->
                        task.id = taskSnapshot.key
                        newList.add(task)
                    }
                }
                _selectedDateTasks.value = newList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Data load cancelled", error.toException())
            }
        })
    }

    fun addTodo(task: String, date: LocalDate) {
        val dateKey = getDateKey(date)
        val taskRef = baseRef.child(dateKey).push()

        val taskItem = TaskItem(
            task = task,
            id = taskRef.key
        )

        taskRef.setValue(taskItem)
    }

    fun deleteTodo(position: Int, date: LocalDate) {
        val dateKey = getDateKey(date)
        val currentList = _selectedDateTasks.value ?: return
        val task = currentList[position]

        task.id?.let { taskId ->
            baseRef.child(dateKey).child(taskId).removeValue()
        }
    }

    fun updateTodoCheck(position: Int, isChecked: Boolean, date: LocalDate) {
        val dateKey = getDateKey(date)
        val currentList = _selectedDateTasks.value ?: return
        val task = currentList[position]

        task.id?.let { taskId ->
            task.isChecked = isChecked
            baseRef.child(dateKey).child(taskId).setValue(task)
        }
    }
}
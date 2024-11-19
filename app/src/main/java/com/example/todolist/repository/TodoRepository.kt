package com.example.todolist.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.example.todolist.model.TaskItem
import java.time.LocalDate

class TodoRepository {
    private val auth = Firebase.auth
    private val database = Firebase.database
    private val baseRef = database.getReference("Todo").child(auth.currentUser?.uid ?: "")

    private fun getDateKey(date: LocalDate): String {
        return "${date.year}-${date.monthValue}-${date.dayOfMonth}"
    }

    fun observeTasksForDate(date: LocalDate, tasks: MutableLiveData<MutableList<TaskItem>>) {
        val dateKey = getDateKey(date)
        baseRef.child(dateKey).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val taskList = mutableListOf<TaskItem>()
                snapshot.children.forEach { taskSnapshot ->
                    taskSnapshot.getValue(TaskItem::class.java)?.let { task ->
                        task.id = taskSnapshot.key
                        taskList.add(task)
                    }
                }
                tasks.postValue(taskList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Data load cancelled", error.toException())
            }
        })
    }

    fun addTask(date: LocalDate, task: TaskItem) {
        val dateKey = getDateKey(date)
        val taskRef = baseRef.child(dateKey).push()
        task.id = taskRef.key
        taskRef.setValue(task)
    }

    fun deleteTask(date: LocalDate, taskId: String?) {
        val dateKey = getDateKey(date)
        taskId?.let { baseRef.child(dateKey).child(it).removeValue() }
    }

    fun updateTask(date: LocalDate, taskId: String?, task: TaskItem) {
        val dateKey = getDateKey(date)
        taskId?.let { baseRef.child(dateKey).child(it).setValue(task) }
    }
}
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

class TodoViewModel : ViewModel() {
    private val _todoList = MutableLiveData<MutableList<TaskItem>>()
    val todoList: LiveData<MutableList<TaskItem>> get() = _todoList

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

                for (dataModel in snapshot.children) {
                    val item = dataModel.getValue(TaskItem::class.java)
                    item?.let {
                        newList.add(it)
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
}
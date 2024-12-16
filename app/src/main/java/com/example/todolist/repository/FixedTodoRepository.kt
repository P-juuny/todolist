package com.example.todolist.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.example.todolist.model.FixedTaskItem

class FixedTodoRepository {
    private val auth = Firebase.auth
    private val database = Firebase.database
    private val fixedRef = database.getReference("Users/${auth.currentUser?.uid ?: ""}/FixedTodo") // 익명 인증을 못받을 경우 currentUser = null

    fun observeFixedTodos(todos: MutableLiveData<MutableList<FixedTaskItem>>) {
        fixedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newList = mutableListOf<FixedTaskItem>()
                for (dataModel in snapshot.children) {
                    val item = dataModel.getValue(FixedTaskItem::class.java)
                    item?.let {
                        it.id = dataModel.key
                        newList.add(it)
                    }
                }
                todos.postValue(newList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Data load cancelled", error.toException())
            }
        })
    }

    fun addFixedTodo(task: FixedTaskItem) {
        val taskRef = fixedRef.push() // Firebase의 고유한 키 값 생성
        task.id = taskRef.key
        taskRef.setValue(task)
    }

    fun deleteFixedTodo(taskId: String?) {
        taskId?.let {
            fixedRef.child(it).removeValue()
        }
    }

    fun updateFixedTodo(taskId: String?, task: FixedTaskItem) {
        taskId?.let {
            fixedRef.child(it).setValue(task)
        }
    }
}
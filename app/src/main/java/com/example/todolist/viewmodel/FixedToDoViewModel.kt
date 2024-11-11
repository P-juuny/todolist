package com.example.todolist.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todolist.model.FixedTaskItem
import com.google.firebase.database.database
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class FixedToDoViewModel : ViewModel() {
    // 내부 데이터
    private val _fixedtodoList = MutableLiveData<MutableList<FixedTaskItem>>()
    // 외부 UI 데이터
    val fixedtodoList: LiveData<MutableList<FixedTaskItem>> get() = _fixedtodoList
    private val auth = Firebase.auth
    private val currentUser = auth.currentUser

    val database = Firebase.database
    val myRef = database.getReference("FixedTodo").child(currentUser?.uid ?: "")

    init {
        LoadData()
    }

    fun LoadData() {
        myRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val newList = mutableListOf<FixedTaskItem>()

                for (dataModel in snapshot.children){
                    val item = dataModel.getValue(FixedTaskItem::class.java)
                    item?.let {
                        newList.add(it)
                    }
                }
                _fixedtodoList.value = newList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Data load cancelled", error.toException())
            }

        })
    }

    fun addTodo(task: FixedTaskItem) {
        val currentList = _fixedtodoList.value ?: mutableListOf()
        val taskRef = myRef.push()

        currentList.add(task)
        _fixedtodoList.value = currentList

        task.id = taskRef.key
        taskRef.setValue(task)
    }

    fun deleteTodo(position: Int) {
        val currentList = _fixedtodoList.value ?: return
        val task = currentList[position]

        currentList.removeAt(position)
        _fixedtodoList.value = currentList

        task.id?.let {
            myRef.child(it).removeValue()
        }
    }

    fun updateTodoCheck(position: Int, isChecked: Boolean) {
        val currentList = _fixedtodoList.value ?: return
        val task = currentList[position]
        currentList[position].isChecked = isChecked
        _fixedtodoList.value = currentList

        task.id?.let {
            myRef.child(it).setValue(task)
        }
    }

    fun updateDate(Id: Int, position: Int, isChecked: Boolean) {
        val currentList = _fixedtodoList.value ?: return
        val task = currentList[position]

        when (Id) {
            0 -> task.monday = isChecked
            1 -> task.tuesday = isChecked
            2 -> task.wednesday = isChecked
            3 -> task.thursday = isChecked
            4 -> task.friday = isChecked
            5 -> task.saturday = isChecked
            6 -> task.sunday = isChecked
        }
        _fixedtodoList.value = currentList

        task.id?.let {
            myRef.child(it).setValue(task)
        }
    }
}
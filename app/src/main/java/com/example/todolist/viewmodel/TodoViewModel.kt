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

// 달력에서 오늘 날짜를 누르면 selectedDataTasks, todayTasks livedata가 동시에 물려 하나만 바꿔줘도 영향을 미침
class TodoViewModel : ViewModel() {
    // 선택된 날짜의 할일들 관리
    private val _selectedDateTasks = MutableLiveData<MutableList<TaskItem>>()
    val selectedDateTasks: LiveData<MutableList<TaskItem>> get() = _selectedDateTasks

    // 오늘의 할일을 관리(스톱워치, 메인화면)
    private val _todayTasks = MutableLiveData<MutableList<TaskItem>>()
    val todayTasks: LiveData<MutableList<TaskItem>> get() = _todayTasks

    private val auth = Firebase.auth
    private val database = Firebase.database
    private val userId = auth.currentUser?.uid ?: "defaultUser"
    private val baseRef = database.getReference("Users/$userId/ToDo")

    init {
        loadTodayTasks()
    }

    private fun getDateKey(date: LocalDate): String {
        // ToDo의 하위 디렉터리 path
        return "${date.year}-${date.monthValue}-${date.dayOfMonth}"
    }

    private fun loadTodayTasks() {
        val today = LocalDate.now()
        // 오늘 날짜 ex) 2024-11-19
        val dateKey = getDateKey(today)

        baseRef.child(dateKey).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val taskList = mutableListOf<TaskItem>()

                snapshot.children.forEach { taskSnapshot ->
                    // Firebase의 데이터를 TaskItem 객체로 변환
                    taskSnapshot.getValue(TaskItem::class.java)?.let { task ->
                        // Firebase가 자동으로 생성한 고유 ID로 task.id 초기화
                        task.id = taskSnapshot.key
                        taskList.add(task)
                    }
                }
                _todayTasks.value = taskList
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
                val taskList = mutableListOf<TaskItem>()

                snapshot.children.forEach { taskSnapshot ->
                    taskSnapshot.getValue(TaskItem::class.java)?.let { task ->
                        task.id = taskSnapshot.key
                        taskList.add(task)
                    }
                }
                _selectedDateTasks.value = taskList

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Data load cancelled", error.toException())
            }
        })
    }

    fun addTodo(task: String, date: LocalDate) {
        // 매개변수로 받은 date의 Firebase 키값
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

        task.id?.let {
            baseRef.child(dateKey).child(it).removeValue()
        }
    }

    fun updateTodoCheck(position: Int, isChecked: Boolean, date: LocalDate) {
        val dateKey = getDateKey(date)
        val currentList = _selectedDateTasks.value ?: return
        val task = currentList[position]

        task.id?.let {
            task.isChecked = isChecked
            baseRef.child(dateKey).child(it).setValue(task)
        }
    }
}
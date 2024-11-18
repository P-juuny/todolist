package com.example.todolist.model

// 동등성, toString()
data class TaskItem(
    val task: String = "",
    var id: String? = null,
    var isChecked: Boolean = false
)
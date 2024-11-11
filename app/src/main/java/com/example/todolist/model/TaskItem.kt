package com.example.todolist.model

data class TaskItem(
    val task: String = "",
    var id: String? = null,
    var isChecked: Boolean = false
)
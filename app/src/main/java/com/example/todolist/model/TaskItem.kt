package com.example.todolist.model

data class TaskItem(
    val task: String,
    var isChecked: Boolean = false
)
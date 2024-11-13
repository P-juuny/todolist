package com.example.todolist.model

data class TaskItem(
    val task: String = "",
    var id: String? = null,
    var isChecked: Boolean = false,
    var year: Int = 0,
    var month: Int = 0,
    var day: Int = 0

)
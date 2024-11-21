package com.example.todolist.model

// 동등성, toString()
data class FixedTaskItem(
    val task: String = "",
    var id: String? = null,
    var monday: Boolean = false,
    var tuesday: Boolean = false,
    var wednesday: Boolean = false,
    var thursday: Boolean = false,
    var friday: Boolean = false,
    var saturday: Boolean = false,
    var sunday: Boolean = false,
)
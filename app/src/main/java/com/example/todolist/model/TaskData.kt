package com.example.todolist.model

data class TaskData(
    val id: String = "",  // Firebase key
    val title: String = "",
    val isCompleted: Boolean = false,
    val isFixed: Boolean = false,
    val weekdays: List<Int> = listOf(),  // 고정 할 일인 경우만 사용 (1: 월요일 ~ 7: 일요일)
    val date: String = ""  // "YYYY-MM-DD" 형식, 일반 할 일인 경우만 사용
)
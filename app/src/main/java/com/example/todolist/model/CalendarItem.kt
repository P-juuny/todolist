package com.example.todolist.model

data class CalendarItem(
    val year: Int,           // 연
    val month: Int,          // 월
    val day: Int,            // 일
    val dayOfWeek: Int,      // 요일 (1: 일요일 ~ 7: 토요일)
    var id: String? = null,  // Firebase key
    var tasks: List<String> = listOf()  // 해당 날짜의 할 일 ID 리스트
)
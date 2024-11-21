package com.example.todolist.repository

import androidx.lifecycle.MutableLiveData
import com.example.todolist.model.DiaryItem
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import java.time.LocalDate

class DiaryRepository {
    private val auth = Firebase.auth
    private val database = Firebase.database
    private val diaryRef = database.getReference("Users").child(auth.currentUser?.uid ?: "").child("Diary")

    fun getDiary(date: LocalDate, diary: MutableLiveData<DiaryItem?>) {
        val dateKey = date.toString()
        // 일회성 데이터 로드
        diaryRef.child(dateKey).get().addOnSuccessListener { snapshot ->
            diary.postValue(snapshot.getValue(DiaryItem::class.java))
        }
    }

    fun saveDiary(content: String, date: LocalDate) {
        val dateKey = date.toString()
        val item = DiaryItem(
            id = dateKey,
            content = content
        )
        diaryRef.child(dateKey).setValue(item)
    }

    fun deleteDiary(date: LocalDate) {
        val dateKey = date.toString()
        diaryRef.child(dateKey).removeValue()
    }
}
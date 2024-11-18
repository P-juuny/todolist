package com.example.todolist.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todolist.model.DiaryItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate

class DiaryViewModel : ViewModel() {
    private val _diary = MutableLiveData<DiaryItem?>()
    val diary: LiveData<DiaryItem?> = _diary

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val diaryRef = database.getReference("Users/${auth.currentUser?.uid}/Diary")

    init {
        loadDiaryEntry()
    }

    private fun loadDiaryEntry() {
        diaryRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val item = snapshot.getValue(DiaryItem::class.java)
                _diary.value = item
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error loading diary entry: ${error.message}")
            }
        })
    }

    fun saveDiary(content: String) {
        val currentDate = LocalDate.now().toString()
        val item = DiaryItem(
            id = currentDate,
            content = content
        )
        diaryRef.setValue(item)
    }

    fun deleteDiary() {
        diaryRef.removeValue()
    }
}
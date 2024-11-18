package com.example.todolist.viewmodel

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
    private val _diary = MutableLiveData<MutableList<DiaryItem>>()
    val diary: LiveData<MutableList<DiaryItem>> = _diary

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val DiaryRef = database.getReference("Users").child("Diary")

    init {
        loadDiaryEntries()
    }

    private fun loadDiaryEntries() {
        DiaryRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val entries = mutableListOf<DiaryItem>()
                for (entrySnapshot in snapshot.children) {
                    entrySnapshot.getValue(DiaryItem::class.java)?.let { entries.add(it) }
                }
                _diary.value = entries
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error loading diary entries: ${error.message}")
            }
        })
    }

    fun saveDiary(content: String) {
        val currentDate = LocalDate.now().toString()
        val item = DiaryItem(
            id = currentDate,
            content = content
        )
        DiaryRef.setValue(item)
    }

    fun deleteDiary() {
        DiaryRef.removeValue()
    }
}
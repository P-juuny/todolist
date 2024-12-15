package com.example.todolist.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todolist.model.DiaryItem
import com.example.todolist.repository.DiaryRepository
import java.time.LocalDate

class DiaryViewModel : ViewModel() {
    private val _diary = MutableLiveData<DiaryItem?>()
    val diary: LiveData<DiaryItem?> = _diary

    private val repository = DiaryRepository()

    fun loadDiaryForDate(date: LocalDate) {
        repository.getDiary(date, _diary)
    }

    fun saveDiaryForDate(content: String, imageUri: Uri?, date: LocalDate) {
        repository.saveDiary(content, imageUri, date)
    }

    fun deleteDiaryForDate(date: LocalDate) {
        repository.deleteDiary(date)
        // 빈 일기장 전환
        _diary.value = DiaryItem()
    }

    fun getImageUrl(imageUrl: String): LiveData<Uri?> {
        return repository.getImageUrl(imageUrl)
    }
}
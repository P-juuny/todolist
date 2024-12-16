package com.example.todolist.repository

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.todolist.model.DiaryItem
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.storage.storage
import java.time.LocalDate

class DiaryRepository {
    private val auth = Firebase.auth
    private val database = Firebase.database
    private val storage = Firebase.storage
    private val diaryRef = database.getReference("Users").child(auth.currentUser?.uid ?: "").child("Diary")
    private val storageRef = storage.reference.child("Users").child(auth.currentUser?.uid ?: "").child("DiaryImages")

    fun getDiary(date: LocalDate, diary: MutableLiveData<DiaryItem?>) {
        val dateKey = date.toString()
        diaryRef.child(dateKey).get().addOnSuccessListener { snapshot ->
            diary.postValue(snapshot.getValue(DiaryItem::class.java))
        }
    }

    fun getImageUrl(imageUrl: String): LiveData<Uri?> {
        val result = MutableLiveData<Uri?>()
        storageRef.child(imageUrl).downloadUrl
            .addOnSuccessListener {
                result.postValue(it)
            }
            .addOnFailureListener {
                result.postValue(null)
            }
        return result
    }

    fun saveDiary(content: String, imageUri: Uri?, date: LocalDate) {
        val dateKey = date.toString()
        val item = DiaryItem(
            id = dateKey,
            content = content,
            imageUrl = if (imageUri != null) "$dateKey.jpg" else null
        )

        if (imageUri != null) {
            storageRef.child("$dateKey.jpg").putFile(imageUri)
        }

        diaryRef.child(dateKey).setValue(item)
    }

    fun deleteDiary(date: LocalDate) {
        val dateKey = date.toString()
        // 먼저 이미지 URL이 있는지 확인
        diaryRef.child(dateKey).get().addOnSuccessListener { snapshot ->
            val diary = snapshot.getValue(DiaryItem::class.java)
            if (diary?.imageUrl != null) {
                storageRef.child(diary.imageUrl).delete()
            }
        }
        diaryRef.child(dateKey).removeValue()
    }
}
package com.example.todolist.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StopWatchRepository {
    private val auth = Firebase.auth
    private val database = Firebase.database
    private val userRef = database.getReference("Users/${auth.currentUser?.uid ?: ""}/StopWatch")

    fun observeStopWatchData(
        goalTime: MutableLiveData<Int?>,
        totalAccumulatedTime: MutableLiveData<Int>,
        dailyAccumulatedTimes: MutableLiveData<Map<String, Int>>,
        medalCounts: Triple<MutableLiveData<Int>, MutableLiveData<Int>, MutableLiveData<Int>>,
        totalScore: MutableLiveData<Int>
    ) {
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                goalTime.postValue(snapshot.child("goalTime").getValue(Int::class.java))
                totalAccumulatedTime.postValue(snapshot.child("totalAccumulatedTime").getValue(Int::class.java) ?: 0)

                val timesMap = mutableMapOf<String, Int>()
                snapshot.child("dailyTimes").children.forEach {
                    timesMap[it.key ?: ""] = it.getValue(Int::class.java) ?: 0
                }
                dailyAccumulatedTimes.postValue(timesMap)

                medalCounts.first.postValue(snapshot.child("medals").child("goldMedals").getValue(Int::class.java) ?: 0)
                medalCounts.second.postValue(snapshot.child("medals").child("silverMedals").getValue(Int::class.java) ?: 0)
                medalCounts.third.postValue(snapshot.child("medals").child("bronzeMedals").getValue(Int::class.java) ?: 0)

                totalScore.postValue(snapshot.child("totalScore").getValue(Int::class.java) ?: 0)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Data load cancelled", error.toException())
            }
        })
    }

    fun updateDailyTime(date: String, timeInSeconds: Int) {
        // 현재 날짜의 dailyTimes를 먼저 조회
        userRef.child("dailyTimes").child(date).get().addOnSuccessListener { snapshot ->
            val currentValue = snapshot.getValue(Int::class.java) ?: 0
            val newValue = currentValue + timeInSeconds

            // dailyTimes와 totalAccumulatedTime 동시 업데이트
            userRef.updateChildren(mapOf(
                "dailyTimes/$date" to newValue,
                "totalAccumulatedTime" to ServerValue.increment(timeInSeconds.toLong())
            ))
        }
    }

    fun getTodayMedalStatus(date: String, callback: (Int) -> Unit) {
        userRef.child("medalStatus").child(date).get()
            .addOnSuccessListener { snapshot ->
                callback(snapshot.getValue(Int::class.java) ?: 0)
            }
            .addOnFailureListener {
                callback(0)
            }
    }

    fun updateMedalsAndScore(gold: Int, silver: Int, bronze: Int, score: Int) {
        userRef.updateChildren(mapOf(
            "medals/goldMedals" to gold,
            "medals/silverMedals" to silver,
            "medals/bronzeMedals" to bronze,
            "totalScore" to score,
            "medalStatus/${getCurrentDate()}" to when {
                gold > 0 -> 3
                silver > 0 -> 2
                bronze > 0 -> 1
                else -> 0
            }
        ))
    }

    fun updateGoalTime(timeInSeconds: Int?) {
        userRef.child("goalTime").setValue(timeInSeconds)
    }

    fun resetAllData() {
        userRef.setValue(null)
    }

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
}
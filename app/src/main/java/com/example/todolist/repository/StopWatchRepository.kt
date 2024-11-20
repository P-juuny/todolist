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

// StopWatchRepository.kt
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

                // 날짜별 시간
                val timesMap = mutableMapOf<String, Int>()
                snapshot.child("dailyTimes").children.forEach {
                    timesMap[it.key ?: ""] = it.getValue(Int::class.java) ?: 0
                }
                dailyAccumulatedTimes.postValue(timesMap)

                // 메달 카운트
                medalCounts.first.postValue(snapshot.child("goldMedals").getValue(Int::class.java) ?: 0)
                medalCounts.second.postValue(snapshot.child("silverMedals").getValue(Int::class.java) ?: 0)
                medalCounts.third.postValue(snapshot.child("bronzeMedals").getValue(Int::class.java) ?: 0)

                // 총점
                totalScore.postValue(snapshot.child("totalScore").getValue(Int::class.java) ?: 0)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Data load cancelled", error.toException())
            }
        })
    }

    fun updateGoalTime(timeInSeconds: Int?) {
        userRef.child("goalTime").setValue(timeInSeconds)
    }

    fun updateDailyTime(date: String, timeInSeconds: Int) {
        // 현재 날짜의 dailyTimes를 먼저 조회
        userRef.child("dailyTimes").child(date).get().addOnSuccessListener { snapshot ->
            val currentValue = snapshot.getValue(Int::class.java) ?: 0
            val newValue = currentValue + timeInSeconds

            // 누적된 값으로 업데이트
            userRef.child("dailyTimes").child(date).setValue(newValue)
        }

        // totalAccumulatedTime 업데이트
        userRef.child("totalAccumulatedTime")
            .setValue(ServerValue.increment(timeInSeconds.toLong()))
    }

    fun updateMedals(gold: Int, silver: Int, bronze: Int) {
        userRef.updateChildren(mapOf(
            "goldMedals" to gold,
            "silverMedals" to silver,
            "bronzeMedals" to bronze
        ))
    }

    fun updateTotalScore(score: Int) {
        userRef.child("totalScore").setValue(score)
    }

    fun resetAllData() {
        userRef.setValue(null)
    }
}
package com.example.todolist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todolist.repository.StopWatchRepository
import java.util.Timer
import kotlin.concurrent.timer

class StopwatchViewModel : ViewModel() {

    private val _time = MutableLiveData(0)
    val time: LiveData<Int> get() = _time

    private val _totalAccumulatedTime = MutableLiveData(0)
    val totalAccumulatedTime: LiveData<Int> get() = _totalAccumulatedTime

    private val _dailyAccumulatedTimes = MutableLiveData<Map<String, Int>>(emptyMap())
    val dailyAccumulatedTimes: LiveData<Map<String, Int>> get() = _dailyAccumulatedTimes

    private val _goalTime = MutableLiveData<Int?>()
    val goalTime: LiveData<Int?> get() = _goalTime

    private val _goldMedalCount = MutableLiveData(0)
    val goldMedalCount: LiveData<Int> get() = _goldMedalCount

    private val _silverMedalCount = MutableLiveData(0)
    val silverMedalCount: LiveData<Int> get() = _silverMedalCount

    private val _bronzeMedalCount = MutableLiveData(0)
    val bronzeMedalCount: LiveData<Int> get() = _bronzeMedalCount

    private val _totalScore = MutableLiveData(0)
    val totalScore: LiveData<Int> get() = _totalScore

    // Timer의 객체를 사용하여 타이머 구현, isRunning으로 타이머의 상태를 관리함
    private var timer: Timer? = null
    var isRunning = false
        private set

    private val repository = StopWatchRepository()
    private var currentDate: String = repository.getCurrentDate()
    private var todayMedal: Int = 0
    private var isInitialized = false

    init {
        repository.observeStopWatchData(
            _goalTime,
            _totalAccumulatedTime,
            _dailyAccumulatedTimes,
            Triple(_goldMedalCount, _silverMedalCount, _bronzeMedalCount),
            _totalScore
        )
        initializeTodayMedal()
    }

    private fun initializeTodayMedal() {
        repository.getTodayMedalStatus(currentDate) { savedMedal ->
            todayMedal = savedMedal
            isInitialized = true
        }
    }

    private fun checkDateAndInitialize() {
        val today = repository.getCurrentDate()
        if (today != currentDate) {
            currentDate = today
            isInitialized = false

            initializeTodayMedal()

            val updatedDailyTimes = _dailyAccumulatedTimes.value?.toMutableMap() ?: mutableMapOf()
            updatedDailyTimes[currentDate] = 0
            _dailyAccumulatedTimes.value = updatedDailyTimes
        }
    }

    fun startTimer() {
        if (isRunning) return
        isRunning = true
        timer = timer(period = 1000) {
            _time.postValue((_time.value ?: 0) + 1)
        }
    }

    fun stopTimer() {
        timer?.cancel()
        timer = null
        isRunning = false
    }

    fun resetTimer() {
        stopTimer()
        val recordedTime = _time.value ?: 0
        _time.value = 0

        // 현재 날짜(startBtn 누른 날짜)를 oldDate에 저장
        val oldDate = currentDate

        if (recordedTime > 0 && isInitialized) {
            val currentDailyTime = (_dailyAccumulatedTimes.value?.get(oldDate) ?: 0) + recordedTime
            repository.updateDailyTime(oldDate, recordedTime)
            calculateMedalAndScore(currentDailyTime)
        }

        // 날짜를 확인하여 다음날로 넘어갔을 경우에 변경
        checkDateAndInitialize()
    }


    private fun calculateMedalAndScore(totalDailyTime: Int) {
        if (todayMedal == 3) return

        val currentGold = _goldMedalCount.value ?: 0
        val currentSilver = _silverMedalCount.value ?: 0
        val currentBronze = _bronzeMedalCount.value ?: 0

        val newMedal = when {
            totalDailyTime >= 30 && todayMedal < 3 -> 3
            totalDailyTime >= 20 && todayMedal < 2 -> 2
            totalDailyTime >= 10 && todayMedal < 1 -> 1
            else -> todayMedal
        }

        if (newMedal > todayMedal) {
            val (updatedGold, updatedSilver, updatedBronze) = when (todayMedal) {
                1 -> Triple(currentGold, currentSilver, currentBronze - 1)
                2 -> Triple(currentGold, currentSilver - 1, currentBronze)
                //3 -> Triple(currentGold - 1, currentSilver, currentBronze)
                else -> Triple(currentGold, currentSilver, currentBronze)
            }

            val finalMedals = when (newMedal) {
                1 -> Triple(updatedGold, updatedSilver, updatedBronze + 1)
                2 -> Triple(updatedGold, updatedSilver + 1, updatedBronze)
                3 -> Triple(updatedGold + 1, updatedSilver, updatedBronze)
                else -> Triple(updatedGold, updatedSilver, updatedBronze)
            }

            val scoreDiff = newMedal - todayMedal
            val newScore = (_totalScore.value ?: 0) + scoreDiff

            todayMedal = newMedal

            repository.updateMedalsAndScore(
                finalMedals.first,
                finalMedals.second,
                finalMedals.third,
                newScore,
                newMedal
            )
        }
    }

    fun resetAllAccumulatedTime() {
        repository.resetAllData()
        todayMedal = 0
    }

    fun setGoalTime(timeInSeconds: Int?) {
        repository.updateGoalTime(timeInSeconds)
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }
}

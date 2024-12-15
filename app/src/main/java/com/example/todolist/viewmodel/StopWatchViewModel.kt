package com.example.todolist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todolist.repository.StopWatchRepository
import java.util.Timer
import kotlin.concurrent.timer

class StopwatchViewModel : ViewModel() {
    // 현재 타이머 시간
    private val _time = MutableLiveData(0)
    val time: LiveData<Int> get() = _time

    // 총 누적 시간
    private val _totalAccumulatedTime = MutableLiveData(0)
    val totalAccumulatedTime: LiveData<Int> get() = _totalAccumulatedTime

    // 날짜별 누적 시간
    private val _dailyAccumulatedTimes = MutableLiveData<Map<String, Int>>(emptyMap())
    val dailyAccumulatedTimes: LiveData<Map<String, Int>> get() = _dailyAccumulatedTimes

    // 목표 시간
    private val _goalTime = MutableLiveData<Int?>()
    val goalTime: LiveData<Int?> get() = _goalTime

    // 금, 은, 동 메달 수
    private val _goldMedalCount = MutableLiveData(0)
    val goldMedalCount: LiveData<Int> get() = _goldMedalCount

    private val _silverMedalCount = MutableLiveData(0)
    val silverMedalCount: LiveData<Int> get() = _silverMedalCount

    private val _bronzeMedalCount = MutableLiveData(0)
    val bronzeMedalCount: LiveData<Int> get() = _bronzeMedalCount

    // 메달별 계산에 따른 총 점수
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
            todayMedal = 0  // 새로운 날짜면 메달 초기화
            isInitialized = false

            repository.getTodayMedalStatus(today) { savedMedal ->
                todayMedal = savedMedal
                isInitialized = true
            }

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
        _time.value = 0  // 타이머 값 초기화

        // 현재 날짜(리셋 직전까지 사용하던 날짜)를 oldDate에 저장
        val oldDate = currentDate

        // 만약 기록된 시간이 있다면 먼저 이 시간을 시작했던 날짜의 누적시간에 저장
        if (recordedTime > 0 && isInitialized) {
            val currentDailyTime = (_dailyAccumulatedTimes.value?.get(oldDate) ?: 0) + recordedTime
            repository.updateDailyTime(oldDate, recordedTime)
            // 현재 currentDate(=oldDate)를 기준으로 메달 계산
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
                3 -> Triple(currentGold - 1, currentSilver, currentBronze)
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

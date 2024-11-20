package com.example.todolist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todolist.repository.StopWatchRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Timer
import kotlin.concurrent.timer

class StopwatchViewModel : ViewModel() {

    private val _time = MutableLiveData(0) // 초 단위로 진행되는 스탑워치 시간
    val time: LiveData<Int> get() = _time

    private val _totalAccumulatedTime = MutableLiveData(0) // 전체 누적 시간
    val totalAccumulatedTime: LiveData<Int> get() = _totalAccumulatedTime

    private val _dailyAccumulatedTimes = MutableLiveData<Map<String, Int>>(emptyMap()) // 날짜별 누적 시간
    val dailyAccumulatedTimes: LiveData<Map<String, Int>> get() = _dailyAccumulatedTimes

    private val _goalTime = MutableLiveData<Int?>() // 목표 시간
    val goalTime: LiveData<Int?> get() = _goalTime

    //금, 은, 동 메달 개수
    private val _goldMedalCount = MutableLiveData(0)
    val goldMedalCount: LiveData<Int> get() = _goldMedalCount

    private val _silverMedalCount = MutableLiveData(0)
    val silverMedalCount: LiveData<Int> get() = _silverMedalCount

    private val _bronzeMedalCount = MutableLiveData(0)
    val bronzeMedalCount: LiveData<Int> get() = _bronzeMedalCount

    //메달 개수를 토대로 점수 계산을 관리하는 변수
    private val _totalScore = MutableLiveData(0)
    val totalScore: LiveData<Int> get() = _totalScore

    //Timer 클래스 사용. 타이머가 실행중이 아닐땐 null로 설정
    private var timer: Timer? = null
    var isRunning = false
        private set

    private var currentDate: String = getCurrentDate() // 현재 날짜 저장
    private val repository = StopWatchRepository() // 레포지토리 객체 생성

    init { // 레포지토리 객체 설정
        repository.observeStopWatchData(
            _goalTime,
            _totalAccumulatedTime,
            _dailyAccumulatedTimes,
            Triple(_goldMedalCount, _silverMedalCount, _bronzeMedalCount),
            _totalScore
        )
    }


    // 현재 날짜를 "yyyy-MM-dd" 형식으로 반환하는 함수
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    // 날짜가 변경되었을 경우 현재 날짜를 갱신
    private fun checkDateAndInitialize() {
        val today = getCurrentDate()
        if (today != currentDate) {
            currentDate = today
            // 날짜가 변경되면 새로운 날의 일일 누적 시간을 0으로 시작
            val updatedDailyTimes = _dailyAccumulatedTimes.value?.toMutableMap() ?: mutableMapOf()
            updatedDailyTimes[currentDate] = 0
            _dailyAccumulatedTimes.value = updatedDailyTimes
        }
    }

    // 타이머 시작
    fun startTimer() {
        if (isRunning) return // 이미 실행 중이면 무시
        isRunning = true
        timer = timer(period = 1000) {
            checkDateAndInitialize()
            _time.postValue((_time.value ?: 0) + 1) // 1초씩 증가
        }
    }

    // 타이머 중지
    fun stopTimer() {
        timer?.cancel()
        isRunning = false
    }

    // 타이머 초기화 및 현재 시간을 날짜별 누적 시간에 추가
    fun resetTimer() {
        stopTimer()
        val currentTime = _time.value ?: 0
        checkDateAndInitialize()

        repository.updateDailyTime(currentDate, currentTime)
        updateMedalCounts()
        addDailyScore()

        _time.value = 0
    }

    // 모든 누적 시간을 초기화하는 함수
    fun resetAllAccumulatedTime() {
        repository.resetAllData()
    }

    // 메달 개수를 업데이트하는 함수
    // 날짜별로 가장 높은 시간의 메달을 결정하여 하루에 메달이  1개씩만 증가하도록 함.
    // val hours = dailyTime / 3600 // dailyTime은 초 단위로 되어있어서 시간을 알기위해 3600을 나눔
    private fun updateMedalCounts() {
        var goldCount = 0
        var silverCount = 0
        var bronzeCount = 0

        val dailyTime = _dailyAccumulatedTimes.value?.get(currentDate) ?: 0
        val hours = dailyTime / 3600
        when {
            hours >= 6 -> goldCount++
            hours >= 3 -> silverCount++
            hours >= 1 -> bronzeCount++
        }

        val newGold = (_goldMedalCount.value ?: 0) + goldCount
        val newSilver = (_silverMedalCount.value ?: 0) + silverCount
        val newBronze = (_bronzeMedalCount.value ?: 0) + bronzeCount

        repository.updateMedals(newGold, newSilver, newBronze)
    }

    // 총점을 계산하여 _totalScore에 더하는 함수
    // 해당 날짜의 메달 등급에 따른 점수를 총점에 추가
    private fun addDailyScore() {
        val dailyTime = _dailyAccumulatedTimes.value?.get(currentDate) ?: 0
        val hours = dailyTime / 3600
        val dailyScore = when {
            hours >= 6 -> 3 // 금메달 조건
            hours >= 3 -> 2 // 은메달 조건
            hours >= 1 -> 1 // 동메달 조건
            else -> 0
        }
        val newScore = (_totalScore.value ?: 0) + dailyScore
        repository.updateTotalScore(newScore)
    }

    // 목표 시간을 설정하는 함수, 초단위로 받아옴
    fun setGoalTime(timeInSeconds: Int?) {
        repository.updateGoalTime(timeInSeconds)
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel() // ViewModel이 소멸될 때 타이머 중지
    }
}

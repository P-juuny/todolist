package com.example.todolist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Timer
import kotlin.concurrent.timer

class StopwatchViewModel : ViewModel() {

    private val _elapsedTime = MutableLiveData(0) // 초 단위로 스탑워치 시간
    val elapsedTime: LiveData<Int> get() = _elapsedTime

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

    private var timer: Timer? = null
    var isRunning = false
        private set

    private var currentDate: String = getCurrentDate() // 현재 날짜 저장

    // 현재 날짜를 "yyyy-MM-dd" 형식으로 반환하는 함수
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    // 타이머 시작
    fun startTimer() {
        if (isRunning) return // 이미 실행 중이면 무시
        isRunning = true
        timer = timer(period = 1000) {
            checkDateAndInitialize()
            _elapsedTime.postValue((_elapsedTime.value ?: 0) + 1) // 1초씩 증가
        }
    }

    // 타이머 중지
    fun stopTimer() {
        timer?.cancel()
        isRunning = false
    }

    // 타이머 초기화 및 현재 시간을 날짜별 누적 시간에 추가
    fun resetTimer() {
        val currentElapsedTime = _elapsedTime.value ?: 0
        checkDateAndInitialize()

        // 날짜별 누적 시간 업데이트
        val updatedDailyTimes = _dailyAccumulatedTimes.value?.toMutableMap() ?: mutableMapOf()
        updatedDailyTimes[currentDate] = (updatedDailyTimes[currentDate] ?: 0) + currentElapsedTime
        _dailyAccumulatedTimes.value = updatedDailyTimes

        // 전체 누적 시간 업데이트
        _totalAccumulatedTime.value = (_totalAccumulatedTime.value ?: 0) + currentElapsedTime

        // 메달 개수 업데이트 및 총점 계산
        updateMedalCounts()
        addDailyScore()

        _elapsedTime.value = 0
    }

    // 모든 누적 시간을 초기화하는 함수
    fun resetAllAccumulatedTime() {
        _totalAccumulatedTime.value = 0
        _dailyAccumulatedTimes.value = emptyMap()
        _goldMedalCount.value = 0
        _silverMedalCount.value = 0
        _bronzeMedalCount.value = 0
        _totalScore.value = 0
    }

    // 메달 개수를 업데이트하는 함수
    private fun updateMedalCounts() {
        // 금, 은, 동 메달 개수 초기화
        var goldCount = 0
        var silverCount = 0
        var bronzeCount = 0

        // 날짜별로 가장 높은 시간의 메달을 결정하여 하루에 메달이  1개씩만 증가하도록 함.
        _dailyAccumulatedTimes.value?.forEach { (_, dailyTime) ->
            val hours = dailyTime / 3600 // dailyTime은 초 단위로 되어있어서 시간을 알기위해 3600을 나눔
            when {
                hours >= 6 -> goldCount++
                hours >= 3 -> silverCount++
                hours >= 1 -> bronzeCount++
            }
        }

        _goldMedalCount.value = goldCount
        _silverMedalCount.value = silverCount
        _bronzeMedalCount.value = bronzeCount
    }

    // 총점을 계산하여 _totalScore에 더하는 함수
    // 해당 날짜의 메달 등급에 따른 점수를 총점에 추가
    private fun addDailyScore() {
        val dailyTime = _dailyAccumulatedTimes.value?.get(currentDate) ?: 0
        val hours = dailyTime / 3600
        val dailyScore = when {
            hours >= 6 -> 3  // 금메달 조건
            hours >= 3 -> 2  // 은메달 조건
            hours >= 1 -> 1  // 동메달 조건
            else -> 0
        }
        _totalScore.value = (_totalScore.value ?: 0) + dailyScore
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

    // 목표 시간을 설정하는 함수, 초단위로 받아옴
    fun setGoalTime(timeInSeconds: Int?) {
        if (timeInSeconds != null && timeInSeconds > 0) {
            _goalTime.value = timeInSeconds
        } else {
            _goalTime.value = null // 잘못된 입력일 경우 null로 설정
        }
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel() // ViewModel이 소멸될 때 타이머 중지
    }
}

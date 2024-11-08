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

        _elapsedTime.value = 0
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

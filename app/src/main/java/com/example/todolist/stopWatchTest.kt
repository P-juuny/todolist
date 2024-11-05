package com.example.todolist

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.databinding.ActivityStopWatchTestBinding
import java.util.Timer
import kotlin.concurrent.timer

class StopWatchTest : AppCompatActivity() {

    //binding 객체 선언
    private lateinit var binding: ActivityStopWatchTestBinding

    private var isRunning = false
    private var timer: Timer? = null
    private var time = 0 // 시간을 나타낼 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //액티비티 바인딩 객체에 할당 및 뷰 설정
        binding = ActivityStopWatchTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //btn_start 및 btn_refresh가 눌렸을때 실행되는 함수
        binding.btnStart.setOnClickListener { // btn_start 버튼이 눌렸을때 btn_start id가 인식됨
            if(isRunning) pause() // 스탑워치가 작동하고 있을 땐 멈춤
            else start() // 스탑워치가 멈춘 상태이면 다시 실행
        }
        binding.btnRefresh.setOnClickListener {
            refresh()
        }
    }

    private fun start() {
        binding.btnStart.text = getString(R.string.btn_pause)
        binding.btnStart.setBackgroundColor(getColor(R.color.btn_pause))
        isRunning = true

        //timer는 background thread 에서 돌아감
        //period = 10 , 0.01초 단위로 증가함, period = 1000일때 1초단위
        timer = timer(period = 10) {
            time++ //0.01 time 1+

            val milliSecond = time % 100
            val second = (time % 6000) / 100
            val minute = time / 6000
            val hour = time / 360000

            //메인 스레드에서만 뷰를 만질수 있고, background 자원 에서는 뷰를 만질수 없음
            //text를 받아 ui자원에 접근하려하기 때문에 runOnUiThread를 이용해 timer는 background에서 실행되더라도
            //runOnUiThread구문 안에 text를 받는 구문은 메인스레드에서 실행되도록 구현함.
            runOnUiThread {
                if (isRunning) {
                    // refresh()실행 시 중복을 방지하기 위해 실행되고 있을때에만 시간을 바꿔줌
                    // binding 객체로 구성할 때 카멜케이스로 변수명이 자동으로 바뀌는 것도 있음 (xml에서는 tv_millisecond 이다)
                    binding.tvMillisecond.text = if (milliSecond < 10) ".0$milliSecond" else ".$milliSecond"
                    binding.tvSecond.text = if (second < 10) ":0$second" else ":$second"
                    binding.tvMinute.text = if (minute < 10) ":0$minute" else ":$minute"
                    binding.tvHour.text = "$hour"
                }
            }
        }
    }

    //pause를 누르면 다시 누를때 start로 바뀌어야 되기때문에
    //멈춘 상태를 알아보기 쉽게 btn text와 color를 변경
    private fun pause() {
        binding.btnStart.text = getString(R.string.btn_start)
        binding.btnStart.setBackgroundColor(getColor(R.color.btn_start))
        isRunning = false
        timer?.cancel()
    }

    private fun refresh() {
        timer?.cancel()

        binding.btnStart.text = getString(R.string.btn_start)
        binding.btnStart.setBackgroundColor(getColor(R.color.btn_start))
        isRunning = false

        time = 0
        binding.tvMillisecond.text = getString(R.string.tv_millisecond_text)
        binding.tvSecond.text = getString(R.string.tv_second_text)
        binding.tvMinute.text = getString(R.string.tv_minute_text)
        binding.tvHour.text = getString(R.string.tv_hour_text)
    }
}

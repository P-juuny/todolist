package com.example.todolist

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Timer
import kotlin.concurrent.timer

class stopWatchTest : AppCompatActivity() , View.OnClickListener {

    private lateinit var btn_start: Button
    private lateinit var btn_refresh: Button
    private lateinit var tv_hour: TextView
    private lateinit var tv_minute: TextView
    private lateinit var tv_second: TextView
    private lateinit var tv_millisecond: TextView

    private var isRunning = false

    private var timer: Timer? = null
    private var time = 0 //time을 나타낼 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_stop_watch_test)
        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        */
        btn_start = findViewById(R.id.btn_start)
        btn_refresh = findViewById(R.id.btn_refresh)
        tv_hour = findViewById(R.id.tv_hour)
        tv_minute = findViewById(R.id.tv_minute)
        tv_second = findViewById(R.id.tv_second)
        tv_millisecond = findViewById(R.id.tv_millisecond)

        btn_start.setOnClickListener(this) //this는 View.OnclickListener를 의미함
        btn_refresh.setOnClickListener(this)
    }
    //btn_start및 btn_refresh이 눌렸을 때 onClick(view: View?)함수가 실행됨
    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.btn_start -> { //btn_start 버튼이 눌렸을 때 btn_start id가 인식됨
                if(isRunning){ //스탑워치가 작동하고 있을땐 멈춤
                    pause()
                }
                else //스탑워치가 작동하고 있으면 실행함
                    start()
            }

            R.id.btn_refresh -> { //btn_fresh 버튼이 눌렸을 때 btn_refresh id 인식됨
                refresh() //초기화
            }
        }
    }

    private fun start() {
        btn_start.text = getString(R.string.btn_pause)
        btn_start.setBackgroundColor(getColor(R.color.btn_pause))
        isRunning = true

        timer = timer(period = 10) { // timer는 background에서 돌아감
            //1000ms = 1s
            //0.01 time 1+ , 0.01초 단위로 증가함
            time++

            val milliSecond = time % 100
            val second = (time % 6000) / 100
            val minute = time / 6000
            val hour = time / 360000
            //메인 스레드에서만 뷰를 만질수 있고, background 자원 에서는 뷰를 만질수 없음
            //text를 받아 ui자원에 접근하려하기 때문에 runOnUiThread를 이용해 timer는 background에서 실행되더라도
            //runOnUiThread구문 안에 text를 받는 구문은 메인스레드에서 실행되도록 구현함.
            runOnUiThread {
                if(isRunning){
                    // refresh()실행 시 중복을 방지하기 위해 실행되고 있을때에만 시간을 바꿔줌
                    tv_millisecond.text = if(milliSecond < 10) ".0${milliSecond}" else ".${milliSecond}"
                    tv_second.text = if(second < 10) ":0${second}" else ":${second}"
                    tv_minute.text = if(minute < 10) ":0${minute}" else ":${minute}"
                    tv_hour.text = "$hour"
                }
            }
        }
    }

    private fun pause() { //pause를 누르면 다시 누를때 start로 바뀌어야 되기때문에 btn text와 color를 변경
        btn_start.text = getString(R.string.btn_start)
        btn_start.setBackgroundColor(getColor(R.color.btn_start))
        isRunning = false
        timer?.cancel()
    }

    private fun refresh() {
        timer?.cancel()

        btn_start.text = getString(R.string.btn_start)
        btn_start.setBackgroundColor(getColor(R.color.btn_start))
        isRunning = false

        time = 0
        tv_millisecond.text = getString(R.string.tv_millisecond_text)
        tv_second.text = getString(R.string.tv_second_text)
        tv_minute.text = getString(R.string.tv_minute_text)
        tv_hour.text = getString(R.string.tv_hour_text)
    }
}
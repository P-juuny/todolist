package com.example.todolist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolist.adapter.TodoAdapter
import com.example.todolist.databinding.FragmentStopWatchBinding
import com.example.todolist.viewmodel.StopwatchViewModel
import com.example.todolist.viewmodel.TodoViewModel

class StopWatchFragment : Fragment() {

    // nullable 바인딩 객체 선언
    private var _binding: FragmentStopWatchBinding? = null
    private val binding get() = _binding!!

    //StopWatchViewModel 객체 생성
    private val stopwatchViewModel: StopwatchViewModel by activityViewModels()
    //TodoAdapter 객체 생성
    private lateinit var todoAdapter: TodoAdapter
    //TodoViewModel 객체 생성
    private val viewModel: TodoViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // btn_start 및 btn_refresh 클릭 리스너 설정
        _binding = FragmentStopWatchBinding.inflate(inflater, container, false).apply {
            btnStart.setOnClickListener {
                if (stopwatchViewModel.isRunning) {
                    stopwatchViewModel.stopTimer()
                    btnStart.text = getString(R.string.btn_start)
                    btnStart.setBackgroundColor(requireContext().getColor(R.color.btn_start))
                }
                else {
                    stopwatchViewModel.startTimer()
                    btnStart.text = getString(R.string.btn_pause)
                    btnStart.setBackgroundColor(requireContext().getColor(R.color.btn_pause))
                }
            }
            btnRefresh.setOnClickListener {
                stopwatchViewModel.resetTimer() // ViewModel에 저장된 현재 시간을 초기화
                btnStart.text = getString(R.string.btn_start)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDailyRecyclerView()
        observeViewModel()
    }

    // 오늘 할일 받아와서 RecyclerView에 넣는 함수
    private fun setupDailyRecyclerView() {
        todoAdapter = TodoAdapter(viewModel)
        binding.todoRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = todoAdapter
        }

        viewModel.todoList.observe(viewLifecycleOwner) { tasks ->
            todoAdapter.makeList(tasks)
        }
    }

    private fun observeViewModel() {
        // 스탑워치 시간을 UI에 업데이트
        stopwatchViewModel.time.observe(viewLifecycleOwner) { t ->
            val second = t % 60
            val minute = (t / 60) % 60
            val hour = t / 3600

            binding.tvSecond.text = if (second < 10) ":0$second" else ":$second"
            binding.tvMinute.text = if (minute < 10) ":0$minute" else ":$minute"
            binding.tvHour.text = "$hour"
        }
    }
    
// StopWatchViewModel 사용 전 코드
//    private fun start() {
//        binding.btnStart.text = getString(R.string.btn_pause)
//        binding.btnStart.setBackgroundColor(requireContext().getColor(R.color.btn_pause))
//        isRunning = true
//
//        // Timer는 background thread에서 동작
//        timer = timer(period = 1000) {
//            time++ // 1초씩 증가
//
//            val second = time % 60
//            val minute = (time / 60) % 60
//            val hour = time / 3600
//
//            // 메인 스레드에서 UI 업데이트
//            // 프래그먼트에서도 백그라운드에서 돌아가는 스레드가 메인 스레드에 반영 될 수 있도록함
//            requireActivity().runOnUiThread {
//                if (isRunning) { // 타이머가 실행중에만 시각이 변경될 수 있도록함
//                    binding.tvSecond.text = if (second < 10) ":0$second" else ":$second"
//                    binding.tvMinute.text = if (minute < 10) ":0$minute" else ":$minute"
//                    binding.tvHour.text = "$hour"
//                }
//            }
//        }
//    }
//
//    private fun pause() {
//        binding.btnStart.text = getString(R.string.btn_start)
//        binding.btnStart.setBackgroundColor(requireContext().getColor(R.color.btn_start))
//        isRunning = false
//        timer?.cancel()
//    }
//
//    private fun refresh() {
//        timer?.cancel()
//        binding.btnStart.text = getString(R.string.btn_start)
//        binding.btnStart.setBackgroundColor(requireContext().getColor(R.color.btn_start))
//        isRunning = false
//        time = 0
//
//        // 초기 시간 표시 설정
//        binding.tvSecond.text = getString(R.string.tv_second_text)
//        binding.tvMinute.text = getString(R.string.tv_minute_text)
//        binding.tvHour.text = getString(R.string.tv_hour_text)
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // 메모리 누수 방지
    }
}

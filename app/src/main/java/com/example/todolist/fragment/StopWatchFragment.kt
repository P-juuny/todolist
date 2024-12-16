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
import java.time.LocalDate

class StopWatchFragment : Fragment() {

    private var _binding: FragmentStopWatchBinding? = null
    private val binding get() = _binding!!

    private val stopwatchViewModel: StopwatchViewModel by activityViewModels()
    private lateinit var todoAdapter: TodoAdapter
    private val viewModel: TodoViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStopWatchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupButtonClickListeners()
        setupDailyRecyclerView()
        observeViewModel()
        updateButtonState()
    }

    private fun setupButtonClickListeners() {
        binding.btnStart.setOnClickListener {
            if (stopwatchViewModel.isRunning) {
                stopwatchViewModel.stopTimer()
            } else {
                stopwatchViewModel.startTimer()
            }
            updateButtonState()
        }

        binding.btnRefresh.setOnClickListener {
            stopwatchViewModel.resetTimer()
            updateButtonState()
        }
    }

    private fun updateButtonState() {
        binding.btnStart.apply {
            text = getString(if (stopwatchViewModel.isRunning) R.string.btn_pause else R.string.btn_start)
            setBackgroundColor(requireContext().getColor(
                if (stopwatchViewModel.isRunning) R.color.btn_pause else R.color.btn_start
            ))
        }
    }

    private fun setupDailyRecyclerView() {
        todoAdapter = TodoAdapter(viewModel, LocalDate.now())
        binding.todoRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = todoAdapter
        }

        viewModel.todayTasks.observe(viewLifecycleOwner) { tasks ->
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // 메모리 누수 방지
    }
}

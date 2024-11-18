package com.example.todolist.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolist.R
import com.example.todolist.adapter.SimpleFixedTodoAdapter
import com.example.todolist.viewmodel.TodoViewModel
import com.example.todolist.adapter.TodoAdapter
import com.example.todolist.databinding.FragmentTaskOverviewBinding
import com.example.todolist.viewmodel.FixedToDoViewModel
import com.example.todolist.viewmodel.StopwatchViewModel
import java.time.LocalDate


class TaskOverviewFragment : Fragment() {
    private var _binding: FragmentTaskOverviewBinding? = null
    private val binding get() = _binding!!

    private val todoViewModel: TodoViewModel by activityViewModels()
    private val fixedTodoViewModel: FixedToDoViewModel by activityViewModels()
    private val stopWatchViewModel: StopwatchViewModel by activityViewModels()

    private lateinit var todoAdapter: TodoAdapter
    private lateinit var simplefixedTodoAdapter: SimpleFixedTodoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskOverviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    //뷰모델 쓰는 공간
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 전달받은 날짜 데이터로 선택된 날짜 업데이트 -- 건수 추가
        arguments?.let { args ->
            val year = args.getInt("selectedYear")
            val month = args.getInt("selectedMonth")
            val day = args.getInt("selectedDay")

            val selectedDate = LocalDate.of(year, month, day)
            todoViewModel.updateSelectedDate(selectedDate)
        }

        setupRecyclerView()
        setupClickListeners()
        setupTimer()
    }

    private fun setupRecyclerView() {
        todoAdapter = TodoAdapter(todoViewModel)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = todoAdapter
        }

        simplefixedTodoAdapter = SimpleFixedTodoAdapter(fixedTodoViewModel)
        binding.fixedRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = simplefixedTodoAdapter
        }

        todoViewModel.selectedDateTasks.observe(viewLifecycleOwner) { tasks ->
            todoAdapter.makeList(tasks)
        }

        fixedTodoViewModel.fixedtodoList.observe(viewLifecycleOwner) {
            simplefixedTodoAdapter.makeList(it)
        }
    }

    private fun setupClickListeners() {
        binding.buttonAddTodo.setOnClickListener {
            findNavController().navigate(R.id.action_taskOverviewFragment_to_editTodoFragment)
        }

        binding.timerButton.setOnClickListener {
            findNavController().navigate(R.id.action_taskOverviewFragment_to_stopWatchFragment)
        }

        binding.diaryBtn.setOnClickListener {
            findNavController().navigate(R.id.action_taskOverviewFragment_to_diaryFragment)
        }
    }

    private fun setupTimer() {
        stopWatchViewModel.totalAccumulatedTime.observe(viewLifecycleOwner) {
            val hours = it / 3600
            val minutes = (it % 3600) / 60
            val seconds = it % 60
            val timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds)
            binding.totalTime.text = timeString
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
       _binding = null
    }
}

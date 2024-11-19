package com.example.todolist.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolist.R
import com.example.todolist.adapter.CalendarAdapter
import com.example.todolist.adapter.TodoAdapter
import com.example.todolist.databinding.FragmentEntryBinding
import com.example.todolist.viewmodel.CalendarViewModel
import com.example.todolist.viewmodel.SettingsViewModel
import com.example.todolist.viewmodel.TodoViewModel
import java.time.LocalDate
import java.time.YearMonth

class EntryFragment : Fragment() {

    private var _binding: FragmentEntryBinding? = null
    private val binding get() = _binding!!

    private val todoViewModel: TodoViewModel by activityViewModels()
    private val calendarViewModel: CalendarViewModel by activityViewModels()
    private val settingsViewModel: SettingsViewModel by activityViewModels()

    private lateinit var todoAdapter: TodoAdapter
    private lateinit var calendarAdapter: CalendarAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEntryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCalendarRecyclerView()
        setupDailyRecyclerView()
        setupButtons()
        observeCalendarData()

        calendarViewModel.currentMonth.observe(viewLifecycleOwner) {
            binding.tvCurrentDate.text = calendarViewModel.formatCurrentMonth()
        }

        settingsViewModel.todoHidden.observe(viewLifecycleOwner) { collapsed ->
            binding.dailyRecyclerView.visibility = if (collapsed) View.GONE else View.VISIBLE
        }
    }

    // 달력 RecyclerView 설정
    private fun setupCalendarRecyclerView() {
        calendarAdapter = CalendarAdapter(
            onDateClick = { date ->
                calendarViewModel.selectDate(date)
                todoViewModel.loadTasksForDate(date)
                // Bundle로 날짜 데이터 전달
                val bundle = Bundle().apply {
                    putInt("selectedYear", date.year)
                    putInt("selectedMonth", date.monthValue)
                    putInt("selectedDay", date.dayOfMonth)
                }
                findNavController().navigate(
                    R.id.action_entryFragment_to_taskOverviewFragment,
                    bundle
                )
            },
            currentMonth = calendarViewModel.currentMonth.value ?: YearMonth.now()
        )

        binding.calendarRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 7)
            adapter = calendarAdapter
        }
    }

    // 달력 데이터 관찰
    private fun observeCalendarData() {
        calendarViewModel.calendarItems.observe(viewLifecycleOwner) { items ->
            calendarAdapter.submitList(items)
        }

        calendarViewModel.selectedDate.observe(viewLifecycleOwner) { date ->
            calendarAdapter.setSelectedDate(date)
        }
    }

    // 오늘 할일 받아와서 RecyclerView에 넣는 함수
    private fun setupDailyRecyclerView() {
        todoAdapter = TodoAdapter(todoViewModel, LocalDate.now())  // 오늘 날짜 전달
        binding.dailyRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = todoAdapter
        }

        todoViewModel.todayTasks.observe(viewLifecycleOwner) { tasks ->
            todoAdapter.makeList(tasks)
        }
    }

    // Button의 Navigation 설정하는 함수
    private fun setupButtons() {
        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_entryFragment_to_settingsFragment)
        }

        binding.btnFixedToDo.setOnClickListener {
            findNavController().navigate(R.id.action_entryFragment_to_fixedOverviewFragment)
        }

        binding.btnAchievements.setOnClickListener {
            findNavController().navigate(R.id.action_entryFragment_to_challengeViewFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
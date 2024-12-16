package com.example.todolist.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolist.R
import com.example.todolist.adapter.CalendarAdapter
import com.example.todolist.adapter.TodoAdapter
import com.example.todolist.databinding.DialogMonthYearPickerBinding
import com.example.todolist.databinding.FragmentEntryBinding
import com.example.todolist.viewmodel.CalendarViewModel
import com.example.todolist.viewmodel.SettingsViewModel
import com.example.todolist.viewmodel.TodoViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
        calendarViewModel.initializeContext(requireContext())
        if (calendarViewModel.currentMonth.value == null) {
            calendarViewModel.setCurrentMonth(YearMonth.now())
        }

        setupViews()
        observeViewModels()
    }

    private fun setupViews() {
        setupCalendarRecyclerView()
        setupDailyRecyclerView()
        setupButtons()
        setupMonthYearPicker()
    }

    // Calendar 관련 관찰자들
    private fun observeCalendarChanges() {
        calendarViewModel.currentMonth.observe(viewLifecycleOwner) {
            binding.tvCurrentDate.text = calendarViewModel.formatCurrentMonth()
        }

        calendarViewModel.calendarItems.observe(viewLifecycleOwner) {
            calendarAdapter.submitList(it)
        }
    }

    // Settings 관련 관찰자
    private fun observeSettingsChanges() {
        settingsViewModel.todoHidden.observe(viewLifecycleOwner) {
            binding.dailyRecyclerView.visibility = if (it) View.GONE else View.VISIBLE
        }
    }

    // Todo 관련 관찰자
    private fun observeTodoChanges() {
        todoViewModel.todayTasks.observe(viewLifecycleOwner) {
            todoAdapter.makeList(it)
        }
    }

    private fun observeViewModels() {
        observeCalendarChanges()
        observeSettingsChanges()
        observeTodoChanges()
    }


    private fun setupCalendarRecyclerView() {
        calendarAdapter = CalendarAdapter(
            onDateClick = { date ->
                calendarViewModel.selectDate(date)
                todoViewModel.loadTasksForDate(date)
                navigateToTaskOverview(date)
            }
        )

        binding.calendarRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 7)
            adapter = calendarAdapter
        }
    }


    private fun navigateToTaskOverview(date: LocalDate) {
        val bundle = Bundle().apply {
            putInt("selectedYear", date.year)
            putInt("selectedMonth", date.monthValue)
            putInt("selectedDay", date.dayOfMonth)
        }

        findNavController().navigate(
            R.id.action_entryFragment_to_taskOverviewFragment,
            bundle,
            NavOptions.Builder()
                .setEnterAnim(android.R.anim.slide_in_left)
                .setExitAnim(android.R.anim.slide_out_right)
                .build()
        )
    }

    private fun setupDailyRecyclerView() {
        todoAdapter = TodoAdapter(todoViewModel, LocalDate.now())
        binding.dailyRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = todoAdapter
        }
    }

    // Button의 Navigation 설정하는 함수
    private fun setupButtons() {
        with(binding) {
            btnSettings.setOnClickListener {
                findNavController().navigate(R.id.action_entryFragment_to_settingsFragment)
            }

            btnFixedToDo.setOnClickListener {
                findNavController().navigate(R.id.action_entryFragment_to_fixedOverviewFragment)
            }

            btnAchievements.setOnClickListener {
                findNavController().navigate(R.id.action_entryFragment_to_challengeViewFragment)
            }
        }
    }

    private fun setupMonthYearPicker() {
        binding.tvCurrentDate.setOnClickListener {
            showMonthYearPickerDialog()
        }
    }

    private fun showMonthYearPickerDialog() {
        val current = calendarViewModel.currentMonth.value ?: YearMonth.now()
        val dialogBinding = DialogMonthYearPickerBinding.inflate(layoutInflater)

        setupMonthYearPickers(dialogBinding, current)
        showDialog(dialogBinding)
    }

    private fun setupMonthYearPickers(
        dialogBinding: DialogMonthYearPickerBinding,
        current: YearMonth
    ) {
        with(dialogBinding) {
            monthPicker.apply {
                minValue = 1
                maxValue = 12
                value = current.monthValue
            }

            yearPicker.apply {
                minValue = 2020
                maxValue = 2030
                value = current.year
            }
        }
    }

    private fun showDialog(dialogBinding: DialogMonthYearPickerBinding) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("월/연도 선택")
            .setView(dialogBinding.root)
            .setPositiveButton("확인") { _, _ ->
                val month = dialogBinding.monthPicker.value
                val year = dialogBinding.yearPicker.value
                calendarViewModel.setCurrentMonth(YearMonth.of(year, month))
            }
            .setNegativeButton("취소", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        calendarViewModel.checkDateChange()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
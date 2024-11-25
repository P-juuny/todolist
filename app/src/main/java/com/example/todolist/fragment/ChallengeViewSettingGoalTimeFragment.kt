package com.example.todolist.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.todolist.R
import com.example.todolist.databinding.FragmentChallengeViewSettingGoalTimeBinding
import com.example.todolist.viewmodel.StopwatchViewModel
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

class ChallengeViewSettingGoalTimeFragment : Fragment() {

    private var _binding: FragmentChallengeViewSettingGoalTimeBinding? = null
    private val binding get() = _binding!!

    private val stopwatchViewModel: StopwatchViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChallengeViewSettingGoalTimeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 마지막으로 설정한 목표 시간을 가져와서 표시
        stopwatchViewModel.goalTime.observe(viewLifecycleOwner) { goalTimeInSeconds ->
            if (goalTimeInSeconds != null) {
                val hours = goalTimeInSeconds / 3600
                val minutes = (goalTimeInSeconds % 3600) / 60

                binding.editHours.setText(if (hours < 10) "$hours" else String.format("%02d", hours))
                binding.editMinutes.setText(String.format("%02d", minutes))
            }
        }
        setupTimePickerDialog()
        setupButtons()
    }

    private fun setupTimePickerDialog() {
        // 시간과 분 EditText를 클릭했을 때 모두 TimePicker가 나타나도록 설정
        binding.editHours.setOnClickListener { showTimePicker() }
        binding.editMinutes.setOnClickListener { showTimePicker() }
    }

    private fun showTimePicker() {
        // 현재 입력된 값을 가져옴
        val currentHours = binding.editHours.text.toString().toIntOrNull() ?: 0
        val currentMinutes = binding.editMinutes.text.toString().toIntOrNull() ?: 0

        val timePickerDialog = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(currentHours)
            .setMinute(currentMinutes)
            .setTitleText("목표 시간 설정")
            .build()

        timePickerDialog.addOnPositiveButtonClickListener {
            val hours = timePickerDialog.hour
            val minutes = timePickerDialog.minute

            // 선택된 시간을 각각의 EditText에 표시
            binding.editHours.setText(String.format("%02d", hours))
            binding.editMinutes.setText(String.format("%02d", minutes))

            // 선택된 시간을 초 단위로 변환하여 저장
            val totalSeconds = (hours * 3600) + (minutes * 60)
            stopwatchViewModel.setGoalTime(totalSeconds)
        }

        timePickerDialog.show(parentFragmentManager, "TIME_PICKER")
    }

    private fun setupButtons() {
        binding.btnApplySettingGoalTime.setOnClickListener {
            val hours = binding.editHours.text.toString().toIntOrNull() ?: 0
            val minutes = binding.editMinutes.text.toString().toIntOrNull() ?: 0

            // 시간 형식에 맞춰 토스트 메시지 표시
            val timeString = if (hours < 10) {
                if (minutes < 10) "$hours:0$minutes" else "$hours:$minutes"
            } else {
                if (minutes < 10) "$hours:0$minutes" else "$hours:$minutes"
            }

            Toast.makeText(context, "목표시간이 ${timeString}으로 설정되었습니다", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }

        binding.btnResetCurrent.setOnClickListener {
            stopwatchViewModel.resetAllAccumulatedTime()
            Toast.makeText(context, "현재 시간이 초기화되었습니다", Toast.LENGTH_SHORT).show()
        }
    }
}
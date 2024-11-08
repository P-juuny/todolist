package com.example.todolist.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.todolist.R
import com.example.todolist.databinding.FragmentChallengeViewSettingGoalTimeBinding
import com.example.todolist.viewmodel.StopwatchViewModel

class ChallengeViewSettingGoalTimeFragment : Fragment() {

    private var _binding: FragmentChallengeViewSettingGoalTimeBinding? = null
    private val binding get() = _binding!!

    private val stopwatchViewModel: StopwatchViewModel by activityViewModels() // ViewModel을 Activity와 공유

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChallengeViewSettingGoalTimeBinding.inflate(inflater, container, false)

        // 목표 시간을 설정하고 적용 버튼을 누르면 ViewModel에 목표 시간을 저장
        binding.btnApplySettingGoalTime.setOnClickListener {
            applyGoalTime()
        }
        // 현재시간 초기화 버튼을 누르면 여지껏 누적된 총 시간과 일별 누적시간 및 메달 개수 총 점수가 모두 리셋됨
        binding.btnResetCurrent.setOnClickListener {
            stopwatchViewModel.resetAllAccumulatedTime()
        }
        return binding.root
    }

    private fun applyGoalTime() {
        val goalTimeInput = binding.editGoalTime.text.toString()
        val goalTimeInSeconds = goalTimeInput.toIntOrNull()?.times(3600) // 시간을 초로 변환

        if (goalTimeInSeconds != null && goalTimeInSeconds > 0) {
            stopwatchViewModel.setGoalTime(goalTimeInSeconds)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
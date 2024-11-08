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

    // binding이 있을때 edit_goalTime 에 목표 시간을 입력하고 목표시간 적용 버튼을 누르면
    // 목표 시간이 적용되도록 함수 설정. 이에는 목표시간을 저장하는 변수도 선언
    // 목표 시간을 입력하지 않거나 잘못된 입력을 했을 시에는 예외 처리도 같이 필요함

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChallengeViewSettingGoalTimeBinding.inflate(inflater, container, false)

        // 목표 시간을 설정하고 적용 버튼을 누르면 ViewModel에 목표 시간을 저장
        binding.btnApplySettingGoalTime.setOnClickListener {
            applyGoalTime()
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
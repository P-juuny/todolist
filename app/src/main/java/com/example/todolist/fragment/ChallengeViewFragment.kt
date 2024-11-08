package com.example.todolist.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.todolist.R
import com.example.todolist.databinding.FragmentChallengeViewBinding
import com.example.todolist.viewmodel.StopwatchViewModel
import kotlin.math.min
import kotlin.math.roundToInt

class ChallengeViewFragment : Fragment() {

    // 바인딩 객체 선언 (프래그먼트에서는 nullable로 선언 후 나중에 할당함)
    private var _binding: FragmentChallengeViewBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StopwatchViewModel by activityViewModels() // ViewModel을 Activity와 공유

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChallengeViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // btnSetGoalTime 버튼 클릭 시 프래그먼트 전환
        // Fragment에서 NavController를 가져와 navigate 호출
        binding.btnSetGoalTime.setOnClickListener {
            findNavController().navigate(R.id.action_challengeViewFragment_to_challengeViewSettingGoalTimeFragment)
        }

        // ViewModel의 데이터를 관찰하여 UI 업데이트
        observeViewModel()
    }

    private fun observeViewModel() {
        // 목표 시간을 관찰하여 tvChallengeHour에 표시
        viewModel.goalTime.observe(viewLifecycleOwner) { goalTimeInSeconds ->
            val hours = goalTimeInSeconds?.div(3600) ?: 0
            binding.tvChallengeHour.text = "$hours"
        }

        // 전체 누적 시간을 관찰하여 tvCurrentHour에 표시
        viewModel.totalAccumulatedTime.observe(viewLifecycleOwner) { totalAccumulatedSeconds ->
            val hours = totalAccumulatedSeconds / 3600
            val minutes = (totalAccumulatedSeconds % 3600) / 60
            binding.tvCurrentHour.text = if(minutes < 10) "$hours.0$minutes" else "$hours.$minutes"
        }

        // 목표 시간과 전체 누적 시간을 이용해 완료 퍼센티지 계산하여 tvCompletedPercentage에 표시, 초 단위로 저장 후 초 단위로 계산
        viewModel.goalTime.observe(viewLifecycleOwner) { goalTimeInSeconds ->
            val totalAccumulatedSeconds = viewModel.totalAccumulatedTime.value ?: 0
            val completionPercentage = if (goalTimeInSeconds != null && goalTimeInSeconds > 0) {
                ((totalAccumulatedSeconds.toDouble() / goalTimeInSeconds) * 100).roundToInt()
            } else {
                0
            }
            binding.tvCompletedPercentage.text =
                if(completionPercentage < 10) "0$completionPercentage%" else "$completionPercentage%"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
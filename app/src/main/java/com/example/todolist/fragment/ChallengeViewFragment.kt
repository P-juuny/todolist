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

class ChallengeViewFragment : Fragment() {

    private var _binding: FragmentChallengeViewBinding? = null
    private val binding get() = _binding!!

    private val stopwatchViewModel: StopwatchViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChallengeViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSetGoalTime.setOnClickListener {
            findNavController().navigate(R.id.action_challengeViewFragment_to_challengeViewSettingGoalTimeFragment)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        stopwatchViewModel.goalTime.observe(viewLifecycleOwner) { goalTimeInSeconds ->
            val hours = (goalTimeInSeconds ?: 0) / 3600
            val minutes = ((goalTimeInSeconds ?: 0) % 3600) / 60
            binding.tvChallengeHour.text = "$hours"
            binding.tvChallengeMinute.text = if(minutes < 10) "0$minutes" else "$minutes"
        }

        stopwatchViewModel.totalAccumulatedTime.observe(viewLifecycleOwner) { totalAccumulatedSeconds ->
            val hours = totalAccumulatedSeconds / 3600
            val minutes = (totalAccumulatedSeconds % 3600) / 60
            binding.tvCurrentHour.text = if(minutes < 10) "$hours.0$minutes" else "$hours.$minutes"
        }

        stopwatchViewModel.goalTime.observe(viewLifecycleOwner) { goalTimeInSeconds ->
            val totalAccumulatedSeconds = stopwatchViewModel.totalAccumulatedTime.value ?: 0
            val completionPercentage = if (goalTimeInSeconds != null && goalTimeInSeconds > 0) {
                ((totalAccumulatedSeconds.toDouble() / goalTimeInSeconds) * 100).toInt()
            } else {
                0
            }
            binding.tvCompletedPercentage.text =
                if(completionPercentage < 10) "$completionPercentage%" else "$completionPercentage%"
        }

        stopwatchViewModel.goldMedalCount.observe(viewLifecycleOwner) { goldCount ->
            binding.goldMedalCount.text = goldCount.toString()
        }

        stopwatchViewModel.silverMedalCount.observe(viewLifecycleOwner) { silverCount ->
            binding.silverMedalCount.text = silverCount.toString()
        }

        stopwatchViewModel.bronzeMedalCount.observe(viewLifecycleOwner) { bronzeCount ->
            binding.bronzeMedalCount.text = bronzeCount.toString()
        }

        stopwatchViewModel.totalScore.observe(viewLifecycleOwner) { score ->
            binding.tvTotalScore.text = score.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
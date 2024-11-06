package com.example.todolist.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.todolist.R
import com.example.todolist.databinding.FragmentChallengeViewBinding

class ChallengeViewFragment : Fragment() {

    // 바인딩 객체 선언 (프래그먼트에서는 nullable로 선언 후 나중에 할당함)
    private var _binding: FragmentChallengeViewBinding? = null
    private val binding get() = _binding!!

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {}
//    }

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

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /*
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_challenge_view, container, false)
    }
    */

    /*
    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChallengeViewFragment().apply {
                arguments = Bundle().apply {}
            }
    }
    */
}
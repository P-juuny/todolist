package com.example.todolist.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.todolist.R
import com.example.todolist.databinding.FragmentChallengeViewSettingGoalTimeBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

//binding 객체 선언
private var binding: FragmentChallengeViewSettingGoalTimeBinding? = null

class ChallengeViewSettingGoalTimeFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    // binding이 있을때 edit_goalTime 에 목표 시간을 입력하고 목표시간 적용 버튼을 누르면
    // 목표 시간이 적용되도록 함수 설정. 이에는 목표시간을 저장하는 변수도 선언
    // 목표 시간을 입력하지 않거나 잘못된 입력을 했을 시에는 예외 처리도 같이 필요함

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChallengeViewSettingGoalTimeBinding.inflate(inflater)
        return binding?.root
    }

    /*
    // 목표시간 적용 버튼을 누르면 다시 챌린지뷰로 넘어가게끔 코드 작성 해놓음
    // BUT 챌린지뷰에서 뒤로가기 누르면 다시 목표시간 설정 프래그먼트로 넘어가게됨
    // 위 문제 해결 방법은 nav_main 에서 action_ChallengeViewSettingGoalTimeFragmentToChallengeView 액션 설정에서
    // popBehavior -> popupto -> ChallengeViewFragment 해준 뒤 popuptoinclusive를 true 로 설정해주면 됨.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.btnApplySettingGoalTime?.setOnClickListener {
            findNavController().navigate(R.id.action_challengeViewSettingGoalTimeFragment_to_challengeViewFragment)
        }
    }
    */

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    /*
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChallengeViewSettingGoalTimeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    */
}
package com.example.todolist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.todolist.databinding.FragmentEntryBinding

class EntryFragment : Fragment() {

    var binding: FragmentEntryBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEntryBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.btnSettings?.setOnClickListener {
            findNavController().navigate(R.id.action_entryFragment_to_settingsFragment)
        }

        binding?.btnFixedToDo?.setOnClickListener {
            findNavController().navigate(R.id.action_entryFragment_to_fixedOverviewFragment)
            // navigation-nav_main 가서 원하는 Fragment 추가 후 EntryFragment에서 이어주기
            // 화살표 이름 보고 위에 코드에 action_entryFragment_to_~~) 에서 ~~만 수정 하면 볼 수 있음.
        }

        binding?.btnAchievements?.setOnClickListener {
            findNavController().navigate(R.id.action_entryFragment_to_challengeViewFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


    /*
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EntryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EntryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    */
}
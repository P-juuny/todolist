package com.example.todolist.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.todolist.databinding.FragmentDiaryBinding
import com.example.todolist.viewmodel.DiaryViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DiaryFragment : Fragment() {
    private var _binding: FragmentDiaryBinding? = null
    private val binding get() = _binding!!

    // 현재 날짜를 위한 Calendar 인스턴스
    private val calendar = Calendar.getInstance()
    private val diaryViewModel: DiaryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 현재 날짜 설정
        updateCurrentDate()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        // 이미지 추가 버튼 클릭 리스너
        binding.addPictureBtn.setOnClickListener {
            // Intent.ACTION_GET_CONTENT는 안드로이드 OS에 콘텐츠를 선택하고 싶다는 의도를 전달하는 액션
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply{
                // 모든 이미지 형식을 포함하는 MIME 타입
                type = "image/*"
            }
            startActivityForResult(intent, 1)
        }

        // 저장 버튼 클릭 리스너
        binding.saveBtn.setOnClickListener {
            saveDiary()
        }

        binding.deleteBtn.setOnClickListener {
            deleteDiary()
        }
    }

    private fun updateCurrentDate() {
        // 날짜를 한국 시간에 맞춤, Date -> String 형태로 변환
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA)
        binding.currentDate.text = dateFormat.format(calendar.time)
    }

    private fun saveDiary() {
        val content = binding.editDiaryContent.text.toString()
        if (content.isBlank()) {
            Toast.makeText(context, "내용을 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }

        diaryViewModel.saveDiary(content)

        Toast.makeText(context, "저장되었습니다", Toast.LENGTH_SHORT).show()
        binding.editDiaryContent.text.clear()
        binding.selectedImage.visibility = View.INVISIBLE
    }

    private fun deleteDiary() {
        diaryViewModel.deleteDiary()
        Toast.makeText(context, "일기가 삭제되었습니다", Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // resultCode == RESULT_OK , 이미지 선택을 위해 호출한 Intent 구분
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            // 첫번째 data는 매개변수, 두번째 data는 URI?로 선택된 이미지의 URI
            data?.data?.let {
                binding.selectedImage.apply {
                    // uri가 가리키는 이미지 주소 가져와서 표시
                    setImageURI(it)
                    visibility = View.VISIBLE
                }

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
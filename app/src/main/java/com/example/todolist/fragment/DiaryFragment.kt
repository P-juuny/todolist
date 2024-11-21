package com.example.todolist.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.todolist.databinding.FragmentDiaryBinding
import com.example.todolist.viewmodel.DiaryViewModel
import com.example.todolist.viewmodel.CalendarViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DiaryFragment : Fragment() {
    private var _binding: FragmentDiaryBinding? = null
    private val binding get() = _binding!!

    private val diaryViewModel: DiaryViewModel by activityViewModels()
    private val calendarViewModel: CalendarViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentDate = calendarViewModel.selectedDate.value ?: LocalDate.now()
        binding.currentDate.text = currentDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))

        // 처음엔 저장, 사진 버튼만 보이게
        updateButtonVisibility(true)

        setupClickListeners(currentDate)
        diaryViewModel.loadDiaryForDate(currentDate)

        updateUI()
    }

    private fun updateButtonVisibility(isEditMode: Boolean) {
        binding.apply {
            saveBtn.isVisible = isEditMode
            addPictureBtn.isVisible = isEditMode
            deleteBtn.isVisible = !isEditMode
            editDiaryContent.isEnabled = isEditMode
        }
    }

    private fun updateUI() {
        diaryViewModel.diary.observe(viewLifecycleOwner) { diary ->
            diary?.let {
                binding.editDiaryContent.setText(it.content)
                if (it.content.isNotBlank()) {
                    // 내용 있으면 읽기 모드로
                    updateButtonVisibility(false)
                }
            }
        }
    }

    private fun setupClickListeners(currentDate: LocalDate) {
        binding.addPictureBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            startActivityForResult(intent, 1)
        }

        binding.saveBtn.setOnClickListener {
            val content = binding.editDiaryContent.text.toString()
            if (content.isBlank()) {
                Toast.makeText(context, "내용을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            diaryViewModel.saveDiaryForDate(content, currentDate)
            // 저장하면 저장,사진 버튼 숨기고 삭제 버튼 보이게
            updateButtonVisibility(false)
            Toast.makeText(context, "저장되었습니다", Toast.LENGTH_SHORT).show()
        }

        binding.deleteBtn.setOnClickListener {
            diaryViewModel.deleteDiaryForDate(currentDate)
            binding.editDiaryContent.text.clear()
            binding.selectedImage.visibility = View.GONE
            // 삭제하면 다시 저장,사진 버튼 보이고 삭제 버튼 숨기기
            updateButtonVisibility(true)
            Toast.makeText(context, "일기가 삭제되었습니다", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            data?.data?.let {
                binding.selectedImage.apply {
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
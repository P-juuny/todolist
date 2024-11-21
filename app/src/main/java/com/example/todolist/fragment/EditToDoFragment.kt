package com.example.todolist.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolist.adapter.TodoAdapter
import com.example.todolist.viewmodel.TodoViewModel
import com.example.todolist.databinding.FragmentEditToDoBinding
import com.example.todolist.viewmodel.CalendarViewModel
import java.time.LocalDate

class EditTodoFragment : Fragment() {
    //메모리 누수 관리하기 위해서 2개로 바인딩 (구글의 바인딩 사용법)
    private var _binding: FragmentEditToDoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TodoViewModel by activityViewModels()
    private val calendarViewModel: CalendarViewModel by activityViewModels() // 일 별 데이터 분리를 위해 추가
    private lateinit var todoAdapter: TodoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditToDoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        val selectedDate = calendarViewModel.selectedDate.value ?: LocalDate.now() // 누른 날짜를 calendarViewModel에서 넘김
        todoAdapter = TodoAdapter(viewModel, selectedDate)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = todoAdapter
        }

        viewModel.selectedDateTasks.observe(viewLifecycleOwner) {
            todoAdapter.makeList(it)
        }
    }

    private fun setupClickListeners() {
        binding.AddTaskBtn.setOnClickListener {
            val todoText = binding.editTodoItem.text.toString()
            if (todoText.isNotBlank()) {
                val selectedDate = calendarViewModel.selectedDate.value ?: LocalDate.now()
                viewModel.addTodo(todoText, selectedDate)
                binding.editTodoItem.text.clear()
            }
            else {
                Toast.makeText(context, "내용을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }

        binding.SaveBtn.setOnClickListener {
            Toast.makeText(context, "저장되었습니다", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
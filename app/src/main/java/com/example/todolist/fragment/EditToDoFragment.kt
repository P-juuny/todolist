package com.example.todolist.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolist.adapter.TodoAdapter
import com.example.todolist.model.TaskItem
import com.example.todolist.viewmodel.TodoViewModel
import com.example.todolist.databinding.FragmentEditToDoBinding

class EditTodoFragment : Fragment() {
    //메모리 누수 관리하기 위해서 2개로 바인딩 (구글의 바인딩 사용법)
    private var _binding: FragmentEditToDoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TodoViewModel by activityViewModels()
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
        todoAdapter = TodoAdapter(viewModel)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = todoAdapter
        }

        viewModel.todoList.observe(viewLifecycleOwner) { tasks ->
            todoAdapter.makeList(tasks)
        }
    }

    private fun setupClickListeners() {
        binding.AddTaskBtn.setOnClickListener {
            val todoText = binding.editTodoItem.text.toString()
            if (todoText.isNotBlank()) {
                viewModel.addTodo(TaskItem(todoText))
                binding.editTodoItem.text.clear()
            }
        }

        binding.SaveBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
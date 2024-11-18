package com.example.todolist.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolist.adapter.FixedTodoAdapter
import com.example.todolist.databinding.FragmentFixedOverviewBinding
import com.example.todolist.model.FixedTaskItem
import com.example.todolist.viewmodel.FixedToDoViewModel

class FixedOverviewFragment : Fragment() {
    private var _binding: FragmentFixedOverviewBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FixedToDoViewModel by activityViewModels()
    private lateinit var fixedtodoAdapter: FixedTodoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFixedOverviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        fixedtodoAdapter = FixedTodoAdapter(viewModel)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = fixedtodoAdapter
        }

        viewModel.fixedtodoList.observe(viewLifecycleOwner) {
            fixedtodoAdapter.makeList(it)
        }
    }

    private fun setupClickListeners() {
        binding.AddTaskBtn.setOnClickListener {
            val newTask = binding.editTodoItem.text.toString()
            if(newTask.isNotBlank()) {
                viewModel.addTodo(FixedTaskItem(newTask).apply {
                    monday = binding.inputMonday.isChecked
                    tuesday = binding.inputTuesday.isChecked
                    wednesday = binding.inputWednesday.isChecked
                    thursday = binding.inputThursday.isChecked
                    friday = binding.inputFriday.isChecked
                    saturday = binding.inputSaturday.isChecked
                    sunday = binding.inputSunday.isChecked
                })
            }
            else {
                Toast.makeText(context, "내용을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            binding.apply {
                editTodoItem.text.clear()
                inputMonday.isChecked = false
                inputTuesday.isChecked = false
                inputWednesday.isChecked = false
                inputThursday.isChecked = false
                inputFriday.isChecked = false
                inputSaturday.isChecked = false
                inputSunday.isChecked = false
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
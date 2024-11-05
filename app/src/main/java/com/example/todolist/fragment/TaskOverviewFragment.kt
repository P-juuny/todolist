package com.example.todolist.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolist.R
import com.example.todolist.adapter.SimpleFixedTodoAdapter
import com.example.todolist.viewmodel.TodoViewModel
import com.example.todolist.adapter.TodoAdapter
import com.example.todolist.databinding.FragmentTaskOverviewBinding
import com.example.todolist.viewmodel.FixedToDoViewModel
import java.util.Calendar

/*class TaskOverviewFragment : Fragment() {
    private var _binding: FragmentTaskOverviewBinding? = null
    private val binding get() = _binding!!

    private val todoViewModel: TodoViewModel by activityViewModels()
    private val fixedTodoViewModel: FixedToDoViewModel by activityViewModels()

    private lateinit var todoAdapter: TodoAdapter
    private lateinit var simplefixedTodoAdapter: SimpleFixedTodoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskOverviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        todoAdapter = TodoAdapter(todoViewModel)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = todoAdapter
        }

        simplefixedTodoAdapter = SimpleFixedTodoAdapter(fixedTodoViewModel)
        binding.fixedRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = simplefixedTodoAdapter
        }

        todoViewModel.todoList.observe(viewLifecycleOwner) { tasks ->
            todoAdapter.submitList(tasks)
        }

        fixedTodoViewModel.fixedtodoList.observe(viewLifecycleOwner) { tasks ->
            val todayTasks = tasks.filter { task ->
                when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
                    Calendar.MONDAY -> task.monday
                    Calendar.TUESDAY -> task.tuesday
                    Calendar.WEDNESDAY -> task.wednesday
                    Calendar.THURSDAY -> task.thursday
                    Calendar.FRIDAY -> task.friday
                    Calendar.SATURDAY -> task.saturday
                    Calendar.SUNDAY -> task.sunday
                    else -> false
                }
            }
            simplefixedTodoAdapter.submitList(todayTasks)
        }
    }

    private fun setupClickListeners() {
        binding.buttonAddTodo.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, EditTodoFragment())
                addToBackStack(null)
                commit()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
       _binding = null
    }
}

 */
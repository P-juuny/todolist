package com.example.todolist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.databinding.ItemFixedTodoSimpleBinding
import com.example.todolist.viewmodel.FixedToDoViewModel
import com.example.todolist.model.FixedTaskItem
import java.time.LocalDate

class SimpleFixedTodoAdapter(
    private val viewModel: FixedToDoViewModel,
    private val date: LocalDate
) : RecyclerView.Adapter<SimpleFixedTodoAdapter.ViewHolder>() {

    // 모든 고정된 할 일 (날짜 상관 x)
    private var allTasks: List<FixedTaskItem> = mutableListOf()
    // 해당 날 고정된 할 일 (날짜 상관 o)
    private var visibleTasks: List<FixedTaskItem> = mutableListOf()

    inner class ViewHolder(
        private val binding: ItemFixedTodoSimpleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(taskItem: FixedTaskItem) {
            binding.FixedTodo.text = taskItem.task

            binding.checkBox.setOnCheckedChangeListener(null)
            binding.checkBox.isChecked = taskItem.isChecked

            binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
                // allTasks와 visibleTasks의 Position이 상이, FixedToDoViewModel에서 전체 리스트를 updateTodoCheck해주었기 떄문
                val originalPosition = allTasks.indexOf(taskItem)
                viewModel.updateTodoCheck(originalPosition, isChecked)
            }

            binding.DeleteBtn.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val originalPosition = allTasks.indexOf(taskItem)
                    viewModel.deleteTodo(originalPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFixedTodoSimpleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(visibleTasks[position])
    }

    override fun getItemCount(): Int = visibleTasks.size

    private fun todayTasks(tasks: List<FixedTaskItem>): List<FixedTaskItem> {
        return tasks.filter { task ->
            when (date.dayOfWeek.value) {
                1 -> task.monday
                2 -> task.tuesday
                3 -> task.wednesday
                4  -> task.thursday
                5  -> task.friday
                6 -> task.saturday
                7 -> task.sunday
                else -> false
            }
        }
    }

    fun makeList(newList: List<FixedTaskItem>) {
        allTasks = newList
        visibleTasks = todayTasks(newList)
        notifyDataSetChanged()
    }
}
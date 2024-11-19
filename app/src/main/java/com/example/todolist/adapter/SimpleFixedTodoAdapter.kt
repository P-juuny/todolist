package com.example.todolist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.databinding.ItemFixedTodoSimpleBinding
import com.example.todolist.viewmodel.FixedToDoViewModel
import com.example.todolist.model.FixedTaskItem
import java.time.LocalDate
import java.util.Calendar


class SimpleFixedTodoAdapter(
    private val viewModel: FixedToDoViewModel,
    private val date: LocalDate// 선택한 날짜 추가
) : RecyclerView.Adapter<SimpleFixedTodoAdapter.ViewHolder>() {

    private var allTasks: List<FixedTaskItem> = mutableListOf()
    private var visibleTasks: List<FixedTaskItem> = mutableListOf()

    inner class ViewHolder(
        private val binding: ItemFixedTodoSimpleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(taskItem: FixedTaskItem) {
            binding.FixedTodo.text = taskItem.task

            binding.checkBox.setOnCheckedChangeListener(null)
            binding.checkBox.isChecked = taskItem.isChecked

            binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
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
        }.toMutableList()
    }

    fun makeList(newList: List<FixedTaskItem>) {
        allTasks = newList
        visibleTasks = todayTasks(newList)
        notifyDataSetChanged()
    }
}
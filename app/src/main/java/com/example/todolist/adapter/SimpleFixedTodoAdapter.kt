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
    private val selectedDate: LocalDate // 선택한 날짜 추가
) : RecyclerView.Adapter<SimpleFixedTodoAdapter.ViewHolder>() {

    private var fixedtodoList: List<FixedTaskItem> = mutableListOf()

    inner class ViewHolder(
        private val binding: ItemFixedTodoSimpleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(taskItem: FixedTaskItem) {
            binding.FixedTodo.text = taskItem.task

            binding.checkBox.setOnCheckedChangeListener(null)
            binding.checkBox.isChecked = taskItem.isChecked

            binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
                viewModel.updateTodoCheck(adapterPosition, isChecked)
            }

            binding.DeleteBtn.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    viewModel.deleteTodo(position)
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
        holder.bind(fixedtodoList[position])
    }

    override fun getItemCount(): Int = fixedtodoList.size

    private fun todayTasks(tasks: List<FixedTaskItem>): List<FixedTaskItem> {
        return tasks.filter { task ->
            // when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            when (selectedDate.dayOfWeek) {
                java.time.DayOfWeek.MONDAY -> task.monday
                java.time.DayOfWeek.TUESDAY -> task.tuesday
                java.time.DayOfWeek.WEDNESDAY -> task.wednesday
                java.time.DayOfWeek.THURSDAY -> task.thursday
                java.time.DayOfWeek.FRIDAY -> task.friday
                java.time.DayOfWeek.SATURDAY -> task.saturday
                java.time.DayOfWeek.SUNDAY -> task.sunday
            }
        }
    }

    fun makeList(newList: List<FixedTaskItem>) {
        fixedtodoList = todayTasks(newList)
        notifyDataSetChanged()
    }
}
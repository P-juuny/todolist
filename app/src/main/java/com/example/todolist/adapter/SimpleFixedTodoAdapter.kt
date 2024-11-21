package com.example.todolist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.databinding.ItemFixedTodoSimpleBinding
import com.example.todolist.viewmodel.FixedToDoViewModel
import com.example.todolist.model.FixedTaskItem
import java.time.DayOfWeek
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

            // allTasks와 visibleTasks의 Position이 상이, FixedToDoViewModel에서 전체 리스트를 updateTodoCheck해주었기 떄문
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
        return tasks.filter {
            // enum 클래스 사용 -> else문 필요 x
            when (date.dayOfWeek) {
                DayOfWeek.MONDAY -> it.monday
                DayOfWeek.TUESDAY -> it.tuesday
                DayOfWeek.WEDNESDAY -> it.wednesday
                DayOfWeek.THURSDAY  -> it.thursday
                DayOfWeek.FRIDAY  -> it.friday
                DayOfWeek.SATURDAY -> it.saturday
                DayOfWeek.SUNDAY -> it.sunday
            }
        }
    }

    fun makeList(newList: List<FixedTaskItem>) {
        allTasks = newList
        visibleTasks = todayTasks(newList)
        notifyDataSetChanged()
    }
}
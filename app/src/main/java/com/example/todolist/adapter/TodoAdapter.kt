package com.example.todolist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.viewmodel.TodoViewModel
import com.example.todolist.model.TaskItem
import com.example.todolist.databinding.ItemTodoListBinding
import java.time.LocalDate

class TodoAdapter(
    private val viewModel: TodoViewModel,
    private val date: LocalDate
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    private var todoList: List<TaskItem> = emptyList()

    inner class TodoViewHolder(
        private val binding: ItemTodoListBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(taskItem: TaskItem) {
            binding.todoItemText.text = taskItem.task
            binding.checkBox.setOnCheckedChangeListener(null)
            binding.checkBox.isChecked = taskItem.isChecked

            binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
                viewModel.updateTodoCheck(adapterPosition, isChecked, date)
            }

            binding.DeleteBtn.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    viewModel.deleteTodo(position, date)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding = ItemTodoListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(todoList[position])
    }

    override fun getItemCount(): Int = todoList.size

    fun makeList(newList: List<TaskItem>) {
        if (todoList.size < newList.size) {
            todoList = newList
            notifyItemInserted(todoList.size - 1)
        } else if (todoList.size > newList.size) {
            for (i in todoList.indices) {
                if (i >= newList.size || todoList[i].id != newList[i].id) {
                    val deletedPosition = i
                    todoList = newList
                    notifyItemRemoved(deletedPosition)
                    break
                }
            }
        }
    }
}
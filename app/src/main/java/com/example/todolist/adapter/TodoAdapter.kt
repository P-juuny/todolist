package com.example.todolist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.viewmodel.TodoViewModel
import com.example.todolist.model.TaskItem
import com.example.todolist.databinding.ItemTodoListBinding

class TodoAdapter(
    private val viewModel: TodoViewModel
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    private var todoList: List<TaskItem> = listOf()

    inner class TodoViewHolder(
        private val binding: ItemTodoListBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(taskItem: TaskItem) {
            // 할일 텍스트 설정
            binding.todoItemText.text = taskItem.task
            // 체크박스 상태 설정
            // 뷰를 재활용하기때문에 처음 Listener은 null로 초기화
            binding.checkBox.setOnCheckedChangeListener(null)
            binding.checkBox.isChecked = taskItem.isChecked

            binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
                binding.checkBox.isChecked = taskItem.isChecked
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding = ItemTodoListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TodoViewHolder(binding)
    }
    // 스크롤 할 때마다 뷰 재활용
    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(todoList[position])
    }

    override fun getItemCount(): Int = todoList.size

    fun submitList(newList: List<TaskItem>) {
        todoList = newList
        notifyDataSetChanged()
    }
}
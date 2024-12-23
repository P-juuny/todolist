package com.example.todolist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.databinding.FixedItemTodoListBinding
import com.example.todolist.model.FixedTaskItem
import com.example.todolist.viewmodel.FixedToDoViewModel

class FixedTodoAdapter(
    private val viewModel: FixedToDoViewModel
) : RecyclerView.Adapter<FixedTodoAdapter.FixedTodoViewHolder>() {

    private var fixedtodoList: List<FixedTaskItem> = mutableListOf()

    inner class FixedTodoViewHolder(
        private val binding: FixedItemTodoListBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(taskItem: FixedTaskItem) {
            binding.FixedTodo.text = taskItem.task

            initCheckboxes(taskItem)
            saveCheckboxes(taskItem)
            setOnCheckedChangeListener()

            // 함수로 빼기
            binding.DeleteBtn.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    viewModel.deleteTodo(position)
                }
            }
        }

        private fun initCheckboxes(taskItem: FixedTaskItem) {
            // 리사이클러뷰 충돌 방지
            binding.checkboxMonday.setOnCheckedChangeListener(null)
            binding.checkboxTuesday.setOnCheckedChangeListener(null)
            binding.checkboxWednesday.setOnCheckedChangeListener(null)
            binding.checkboxThursday.setOnCheckedChangeListener(null)
            binding.checkboxFriday.setOnCheckedChangeListener(null)
            binding.checkboxSaturday.setOnCheckedChangeListener(null)
            binding.checkboxSunday.setOnCheckedChangeListener(null)
        }

        private fun saveCheckboxes(taskItem: FixedTaskItem) {
            // 고정된 할 일 저장시 체크박스 상태 저장
            binding.checkboxMonday.isChecked = taskItem.monday
            binding.checkboxTuesday.isChecked = taskItem.tuesday
            binding.checkboxWednesday.isChecked = taskItem.wednesday
            binding.checkboxThursday.isChecked = taskItem.thursday
            binding.checkboxFriday.isChecked = taskItem.friday
            binding.checkboxSaturday.isChecked = taskItem.saturday
            binding.checkboxSunday.isChecked = taskItem.sunday
        }

        private fun setOnCheckedChangeListener() {
            // 체크박스에 체크를 하면 _, isChecked = true -> vieModel.updateDate(id, adapterposition, true) 로 변경
            binding.checkboxMonday.setOnCheckedChangeListener { _, isChecked ->
                viewModel.updateDate(1,adapterPosition, isChecked)
            }

            binding.checkboxTuesday.setOnCheckedChangeListener { _, isChecked ->
                viewModel.updateDate(2, adapterPosition, isChecked)
            }

            binding.checkboxWednesday.setOnCheckedChangeListener { _, isChecked ->
                viewModel.updateDate(3, adapterPosition, isChecked)
            }

            binding.checkboxThursday.setOnCheckedChangeListener { _, isChecked ->
                viewModel.updateDate(4, adapterPosition, isChecked)
            }

            binding.checkboxFriday.setOnCheckedChangeListener { _, isChecked ->
                viewModel.updateDate(5, adapterPosition, isChecked)
            }

            binding.checkboxSaturday.setOnCheckedChangeListener { _, isChecked ->
                viewModel.updateDate(6, adapterPosition, isChecked)
            }

            binding.checkboxSunday.setOnCheckedChangeListener { _, isChecked ->
                viewModel.updateDate(7, adapterPosition, isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FixedTodoViewHolder {
        val binding = FixedItemTodoListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FixedTodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FixedTodoViewHolder, position: Int) {
        holder.bind(fixedtodoList[position])
    }

    override fun getItemCount(): Int = fixedtodoList.size

    fun makeList(newList: List<FixedTaskItem>) {
        if (fixedtodoList.size < newList.size) {
            fixedtodoList = newList
            notifyItemInserted(fixedtodoList.size - 1)
        } else if (fixedtodoList.size > newList.size) {
            for (i in fixedtodoList.indices) {
                if (i >= newList.size || fixedtodoList[i].id != newList[i].id) {
                    fixedtodoList = newList
                    notifyItemRemoved(i)
                    break
                }
            }
        }
    }
}
package com.example.todolist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.viewmodel.CalendarViewModel
import com.google.android.material.card.MaterialCardView
import java.time.LocalDate
import java.time.YearMonth

class CalendarAdapter(
    private val onDateClick: (LocalDate) -> Unit,
    private val currentMonth: YearMonth
) : ListAdapter<CalendarViewModel.DayInfo, CalendarAdapter.CalendarViewHolder>(CalendarDiffCallback()) {

    private var selectedDate: LocalDate? = null

    inner class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDay: TextView = itemView.findViewById(R.id.tv_day)
        private val tvTaskCount: TextView = itemView.findViewById(R.id.tv_task_count)
        private val cardView: MaterialCardView = itemView.findViewById(R.id.calendar_card)

        fun bind(item: CalendarViewModel.DayInfo) {
            // 날짜 표시
            tvDay.text = item.date.dayOfMonth.toString()

            // 현재 달이 아닌 날짜는 흐리게 표시
            tvDay.alpha = if (item.isCurrentMonth) 1.0f else 0.5f

            // 할 일 개수 표시 (일반 + 고정 할 일)
            val totalTasks = item.normalTaskCount + item.fixedTaskCount
            tvTaskCount.text = if (totalTasks > 0) "$totalTasks" else ""

            // 오늘 날짜 표시
            if (item.date == LocalDate.now()) {
                tvDay.setBackgroundResource(R.drawable.today_circle_background)
                tvDay.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.white))
            } else {
                tvDay.background = null
                tvDay.setTextColor(ContextCompat.getColor(itemView.context,
                    com.google.android.material.R.color.material_on_background_emphasis_high_type))
            }

            // 클릭 리스너
            itemView.setOnClickListener {
                selectedDate = item.date
                onDateClick(item.date)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.calendar_item, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setSelectedDate(date: LocalDate) {
        selectedDate = date
        notifyDataSetChanged()
    }
}

private class CalendarDiffCallback : DiffUtil.ItemCallback<CalendarViewModel.DayInfo>() {
    override fun areItemsTheSame(oldItem: CalendarViewModel.DayInfo, newItem: CalendarViewModel.DayInfo): Boolean {
        return oldItem.date == newItem.date
    }

    override fun areContentsTheSame(oldItem: CalendarViewModel.DayInfo, newItem: CalendarViewModel.DayInfo): Boolean {
        return oldItem == newItem
    }
}
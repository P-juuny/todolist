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
import com.example.todolist.model.DayInfo
import java.time.LocalDate
import java.time.YearMonth

class CalendarAdapter(
    private val onDateClick: (LocalDate) -> Unit,
    private val currentMonth: YearMonth
) : ListAdapter<DayInfo, CalendarAdapter.CalendarViewHolder>(CalendarDiffCallback()) {

    private var selectedDate: LocalDate? = null

    inner class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDay: TextView = itemView.findViewById(R.id.tv_day)

        fun bind(item: DayInfo) {
            tvDay.apply {
                text = item.date.dayOfMonth.toString()
                alpha = if (item.isCurrentMonth) 1.0f else 0.5f

                // 오늘 날짜 처리
                if (item.date == LocalDate.now()) {
                    setBackgroundResource(R.drawable.today_circle_background)
                    setTextColor(ContextCompat.getColor(context, android.R.color.white))
                } else {
                    background = null
                    setTextColor(ContextCompat.getColor(context,
                        com.google.android.material.R.color.material_on_background_emphasis_high_type))
                }
            }

            // 클릭 리스너
            itemView.setOnClickListener {
                selectedDate = item.date
                onDateClick(item.date)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        return CalendarViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.calendar_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setSelectedDate(date: LocalDate) {
        selectedDate = date
        notifyDataSetChanged()
    }
}

private class CalendarDiffCallback : DiffUtil.ItemCallback<DayInfo>() {
    override fun areItemsTheSame(oldItem: DayInfo, newItem: DayInfo): Boolean {
        return oldItem.date == newItem.date
    }

    override fun areContentsTheSame(oldItem: DayInfo, newItem: DayInfo): Boolean {
        return oldItem == newItem
    }
}
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

class CalendarAdapter(
    private val onDateClick: (LocalDate) -> Unit
) : ListAdapter<DayInfo, CalendarAdapter.CalendarViewHolder>(CalendarDiffCallback()) {

    inner class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // findViewById를 매번 호출하지 않도록 프로퍼티로 미리 선언
        private val tvDay: TextView = itemView.findViewById(R.id.tv_day)
        private lateinit var currentDate: LocalDate

        fun bind(item: DayInfo) {
            currentDate = item.date

            tvDay.apply {
                text = item.date.dayOfMonth.toString()
                alpha = if (item.isCurrentMonth) 1.0f else 0.5f

                if (item.date == LocalDate.now()) {
                    setBackgroundResource(R.drawable.today_circle_background)
                    setTextColor(ContextCompat.getColor(context, android.R.color.white))
                } else {
                    background = null
                    setTextColor(ContextCompat.getColor(context,
                        com.google.android.material.R.color.material_on_background_emphasis_high_type))
                }
            }

            itemView.setOnClickListener {
                currentDate?.let { onDateClick(it) }
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
}

private class CalendarDiffCallback : DiffUtil.ItemCallback<DayInfo>() {
    override fun areItemsTheSame(oldItem: DayInfo, newItem: DayInfo): Boolean {
        return oldItem.date.isEqual(newItem.date)  // isEqual 사용으로 더 정확한 비교
    }

    override fun areContentsTheSame(oldItem: DayInfo, newItem: DayInfo): Boolean {
        return oldItem.date.isEqual(newItem.date) && oldItem.isCurrentMonth == newItem.isCurrentMonth
    }
}
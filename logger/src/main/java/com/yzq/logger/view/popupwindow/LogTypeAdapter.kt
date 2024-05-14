package com.yzq.logger.view.popupwindow

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yzq.logger.common.LogType
import com.yzq.logger.data.LogTypeItem
import com.yzq.logger.databinding.LayoutItemTypeBinding


/**
 * @description: 日志类型适配器
 * @author : yuzhiqiang
 */

internal class LogTypeAdapter(private val clickListener: LogTypeClickListener) :
    RecyclerView.Adapter<LogTypeAdapter.LogTypeViewHolder>() {

    private val logTypes = mutableListOf(
        LogTypeItem(LogType.VERBOSE, "Verbose", true),
        LogTypeItem(LogType.DEBUG, "Debug", false),
        LogTypeItem(LogType.INFO, "Info", false),
        LogTypeItem(LogType.WARN, "Warn", false),
        LogTypeItem(LogType.ERROR, "Error", false),
    )


    inner class LogTypeViewHolder(val binding: LayoutItemTypeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.tvType.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val logType = logTypes[position]
                    logTypes.forEach {
                        it.selected = false
                    }
                    logType.selected = true
                    notifyDataSetChanged()
                    clickListener.onLogTypeClick(logType)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogTypeViewHolder {

        LayoutItemTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            .let {
                return LogTypeViewHolder(it)
            }
    }

    override fun getItemCount(): Int {
        return logTypes.size
    }

    override fun onBindViewHolder(holder: LogTypeViewHolder, position: Int) {
        val logTypeItem = logTypes[position]
        holder.binding.tvType.text = logTypeItem.type
        holder.binding.tvType.isSelected = logTypeItem.selected
        if (logTypeItem.selected) {
            holder.binding.tvType.setTextColor(Color.WHITE)
        } else {
            holder.binding.tvType.setTextColor(Color.BLACK)
        }
    }
}
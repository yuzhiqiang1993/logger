package com.yzq.logger.view.log_view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yzq.application.AppContext
import com.yzq.logger.common.LogType
import com.yzq.logger.common.getLogColor
import com.yzq.logger.data.ViewLogItem
import com.yzq.logger.databinding.LayoutItemLogBinding


/**
 * @description: 日志列表适配器
 * @author : yuzhiqiang
 */

internal class LogAdapter :
    RecyclerView.Adapter<LogAdapter.LogViewHolder>() {
    //原始数据
    private var originData: MutableList<ViewLogItem> = mutableListOf()

    //列表显示的数据
    private var logItems: MutableList<ViewLogItem> = mutableListOf()

    //过滤类型
    private var filterType: LogType = LogType.VERBOSE

    private var filterKeyWord: String = ""

    //更新数据
    private fun diffData(newLogs: MutableList<ViewLogItem>) {
        if (logItems.isEmpty()) {
            logItems = newLogs
            notifyDataSetChanged()
            return
        }

        val calculateDiff = DiffUtil.calculateDiff(LogDiffCallback(logItems, newLogs), true)
        logItems = newLogs
        calculateDiff.dispatchUpdatesTo(this)


    }


    inner class LogViewHolder(val binding: LayoutItemLogBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.tvLog.setOnLongClickListener {
                val clipboardManager =
                    AppContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip =
                    ClipData.newPlainText("日志内容", logItems[bindingAdapterPosition].content)
                clipboardManager.setPrimaryClip(clip)
                Toast.makeText(AppContext, "当前日志已复制到剪切板", Toast.LENGTH_SHORT).show()
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        LayoutItemLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            .let {
                return LogViewHolder(it)
            }
    }

    override fun getItemCount(): Int {
        return logItems.size
    }


    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val logItem = logItems[position]
        holder.binding.tvLog.text = logItem.content
        holder.binding.tvLog.setTextColor(getLogColor(logItem.logType))
    }


    fun addData(it: ViewLogItem) {
        originData.add(it)

        if (it.isMatch()) {
            logItems.add(it)
            notifyItemInserted(logItems.size - 1)
        }

    }

    fun filterData(logType: LogType? = null, keyWord: String? = null) {

        val filterType = logType ?: this.filterType
        val filterKeyWord = keyWord ?: this.filterKeyWord

        if (filterType == this.filterType && filterKeyWord == this.filterKeyWord) {
            return
        }
        this.filterType = filterType
        this.filterKeyWord = filterKeyWord

        val filterLogs = originData.filter {
            it.isMatch()
        }

        diffData(filterLogs.toMutableList())
    }


    private fun ViewLogItem.isMatch(): Boolean {
        return logType >= filterType && (filterKeyWord.isEmpty() || content.contains(
            filterKeyWord
        ))

    }

    fun clearData() {
        originData.clear()
        logItems.clear()
        diffData(mutableListOf())
    }


}
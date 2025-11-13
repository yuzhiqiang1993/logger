package com.yzq.logger.view.log_view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yzq.application.AppContext
import com.yzq.logger.common.LogType
import com.yzq.logger.common.getLogColor
import com.yzq.logger.data.LogItem
import com.yzq.logger.databinding.LayoutItemLogEnhancedBinding

/**
 * @description: 增强版日志列表适配器
 * @author : yuzhiqiang
 */
internal class LogAdapterEnhanced :
    RecyclerView.Adapter<LogAdapterEnhanced.LogViewHolder>() {

    // 原始数据
    private var originData: MutableList<LogItem> = mutableListOf()

    // 列表显示的数据
    private var logItems: MutableList<LogItem> = mutableListOf()

    // 过滤类型
    private var filterType: LogType = LogType.VERBOSE

    // 过滤关键字
    private var filterKeyWord: String = ""

    // 长按点击监听器
    private var onLongClickListener: ((LogItem) -> Unit)? = null

    fun setOnLongClickListener(listener: (LogItem) -> Unit) {
        this.onLongClickListener = listener
    }

    // 添加数据
    fun addData(newLogs: List<LogItem>) {
        val mergedData = (originData + newLogs).toMutableList()
        updateData(mergedData)
    }

    // 清空数据
    fun clearData() {
        originData.clear()
        logItems.clear()
        notifyDataSetChanged()
    }

    // 过滤数据
    fun filterData(logType: LogType = LogType.VERBOSE, keyWord: String = "") {
        this.filterType = logType
        this.filterKeyWord = keyWord
        applyFilters()
    }

    private fun updateData(newData: MutableList<LogItem>) {
        if (logItems.isEmpty()) {
            originData = newData
            applyFilters()
            return
        }

        val filteredNewData = applyFiltersToData(newData)
        val calculateDiff = DiffUtil.calculateDiff(LogEnhancedDiffCallback(logItems, filteredNewData), true)

        originData = newData
        logItems = filteredNewData
        calculateDiff.dispatchUpdatesTo(this)
    }

    private fun applyFilters() {
        logItems = applyFiltersToData(originData)
        notifyDataSetChanged()
    }

    private fun applyFiltersToData(data: List<LogItem>): MutableList<LogItem> {
        val filtered = data.filter { item ->
            val typeMatch = item.logType.level >= filterType.level
            val contentStr = item.content.joinToString(" ")
            val keyWordMatch = filterKeyWord.isEmpty() ||
                item.tag.contains(filterKeyWord, ignoreCase = true) ||
                contentStr.contains(filterKeyWord, ignoreCase = true) ||
                item.threadName?.contains(filterKeyWord, ignoreCase = true) == true

            typeMatch && keyWordMatch
        }
        return filtered.toMutableList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val binding = LayoutItemLogEnhancedBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        holder.bind(logItems[position])
    }

    override fun getItemCount(): Int = logItems.size

    inner class LogViewHolder(
        private val binding: LayoutItemLogEnhancedBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(logItem: LogItem) {
            with(binding) {
                // 设置日志级别和颜色
                tvLogType.text = logItem.logType.name
                tvLogType.setTextColor(getLogColor(logItem.logType))

                // 设置时间
                tvTime.text = formatTimestamp(logItem.timeMillis)

                // 设置 TAG
                tvTag.text = logItem.tag

                // 设置线程信息
                logItem.threadName?.let { threadName ->
                    tvThread.text = "[$threadName]"
                    tvThread.visibility = android.view.View.VISIBLE
                } ?: run {
                    tvThread.visibility = android.view.View.GONE
                }

                // 设置日志内容（带高亮）
                val contentStr = logItem.content.joinToString(" ")
                tvContent.text = highlightKeyword(contentStr, filterKeyWord)

                // 设置背景色
                val backgroundColor = when (logItem.logType) {
                    LogType.ERROR -> ContextCompat.getColor(root.context, com.yzq.logger.R.color.log_error_background)
                    LogType.WARN -> ContextCompat.getColor(root.context, com.yzq.logger.R.color.log_warn_background)
                    else -> Color.TRANSPARENT
                }
                root.setBackgroundColor(backgroundColor)

                // 设置点击事件
                root.setOnLongClickListener {
                    onLongClickListener?.invoke(logItem)
                    true
                }

                root.setOnClickListener {
                    // 复制到剪贴板
                    copyLogToClipboard(logItem)
                }
            }
        }

        private fun formatTimestamp(timestamp: Long): String {
            val date = java.util.Date(timestamp)
            val format = java.text.SimpleDateFormat("HH:mm:ss.SSS", java.util.Locale.getDefault())
            return format.format(date)
        }

        private fun highlightKeyword(text: String, keyword: String): SpannableString {
            if (keyword.isEmpty()) {
                return SpannableString(text)
            }

            val spannable = SpannableString(text)
            val startIndex = text.indexOf(keyword, ignoreCase = true)

            if (startIndex != -1) {
                val endIndex = startIndex + keyword.length
                spannable.setSpan(
                    BackgroundColorSpan(Color.YELLOW),
                    startIndex,
                    endIndex,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannable.setSpan(
                    ForegroundColorSpan(Color.BLACK),
                    startIndex,
                    endIndex,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            return spannable
        }

        private fun copyLogToClipboard(logItem: LogItem) {
            val logText = buildString {
                append("[${logItem.logType.name}] ")
                append(formatTimestamp(logItem.timeMillis))
                append(" ")
                if (logItem.threadName != null) {
                    append("[${logItem.threadName}] ")
                }
                append("${logItem.tag}: ")
                append(logItem.content.joinToString(" "))
            }

            val clipboard = AppContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("日志", logText)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(AppContext, "日志已复制", Toast.LENGTH_SHORT).show()
        }
    }
}

// DiffUtil 回调
private class LogEnhancedDiffCallback(
    private val oldList: List<LogItem>,
    private val newList: List<LogItem>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].timeMillis == newList[newItemPosition].timeMillis &&
               oldList[oldItemPosition].tag == newList[newItemPosition].tag
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
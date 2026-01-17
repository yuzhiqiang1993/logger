package com.yzq.logger.view.log_view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
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
    // 原始数据
    private var originData: MutableList<ViewLogItem> = mutableListOf()

    // 使用 AsyncListDiffer 进行异步 Diff 计算
    private val asyncDiffer = AsyncListDiffer(this, object : DiffUtil.ItemCallback<ViewLogItem>() {
        override fun areItemsTheSame(oldItem: ViewLogItem, newItem: ViewLogItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ViewLogItem, newItem: ViewLogItem): Boolean {
            // 如果是同一个 item，内容肯定相同
            return true
        }
    })

    // 过滤类型
    private var filterType: LogType = LogType.VERBOSE

    private var filterKeyWord: String = ""

    // 过滤的 TAG 集合（空集合表示不过滤 TAG）
    private var filterTags: Set<String> = emptySet()


    inner class LogViewHolder(val binding: LayoutItemLogBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.tvLog.setOnLongClickListener {
                val position = bindingAdapterPosition
                if (position in 0 until asyncDiffer.currentList.size) {
                    val clipboardManager =
                        AppContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip =
                        ClipData.newPlainText("日志内容", asyncDiffer.currentList[position].content)
                    clipboardManager.setPrimaryClip(clip)
                    Toast.makeText(AppContext, "当前日志已复制到剪切板", Toast.LENGTH_SHORT).show()
                }
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
        return asyncDiffer.currentList.size
    }


    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val logItem = asyncDiffer.currentList[position]

        // 设置基本信息
        holder.binding.tvTime.text = com.yzq.logger.common.dateFormat.get()?.format(logItem.timestamp)
        holder.binding.tvThread.text = "[${logItem.threadName ?: "unknown"}]"
        holder.binding.tvTagLabel.text = logItem.tag
        holder.binding.tvLog.text = logItem.content

        // 设置颜色
        val logColor = getLogColor(logItem.logType)
        holder.binding.tvTagLabel.setTextColor(logColor)
        holder.binding.tvLog.setTextColor(logColor)

        // 设置堆栈信息
        if (logItem.stackTrace.isNullOrEmpty()) {
            holder.binding.tvStackTrace.visibility = android.view.View.GONE
        } else {
            holder.binding.tvStackTrace.visibility = android.view.View.VISIBLE
            holder.binding.tvStackTrace.text = logItem.stackTrace
        }
    }


    // 展示的数据（filtered）
    private var showData: MutableList<ViewLogItem> = mutableListOf()

    fun addData(it: ViewLogItem) {
        originData.add(it)

        if (it.isMatch()) {
            showData.add(it)
            // 使用 AsyncListDiffer 异步更新
            asyncDiffer.submitList(ArrayList(showData))
        }

    }

    fun filterData(logType: LogType? = null, keyWord: String? = null, tags: Set<String>? = null) {

        val filterType = logType ?: this.filterType
        val filterKeyWord = keyWord ?: this.filterKeyWord
        val filterTags = tags ?: this.filterTags

        if (filterType == this.filterType && filterKeyWord == this.filterKeyWord && filterTags == this.filterTags) {
            return
        }
        this.filterType = filterType
        this.filterKeyWord = filterKeyWord
        this.filterTags = filterTags

        showData.clear()
        showData.addAll(originData.filter { it.isMatch() })
        asyncDiffer.submitList(ArrayList(showData))
    }


    private fun ViewLogItem.isMatch(): Boolean {
        val matchLogType = logType >= filterType
        val matchKeyword = filterKeyWord.isEmpty() || content.contains(filterKeyWord)
        val matchTag = filterTags.isEmpty() || filterTags.contains(tag)
        return matchLogType && matchKeyword && matchTag
    }

    /**
     * 获取所有不同的 TAG
     */
    fun getAllTags(): Set<String> {
        return originData.map { it.tag }.toSet()
    }

    fun clearData() {
        originData.clear()
        showData.clear()
        asyncDiffer.submitList(emptyList())
    }


}


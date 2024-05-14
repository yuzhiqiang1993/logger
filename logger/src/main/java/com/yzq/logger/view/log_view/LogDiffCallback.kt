package com.yzq.logger.view.log_view

import androidx.recyclerview.widget.DiffUtil
import com.yzq.logger.data.ViewLogItem


/**
 * @description: 日志列表差异比较器
 * @author : yuzhiqiang
 */

internal class LogDiffCallback(
    private val oldList: List<ViewLogItem>, private val newList: List<ViewLogItem>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {

        return oldList.size

    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        //如果是同一个item，那么内容肯定是一样的
        return true
    }

}

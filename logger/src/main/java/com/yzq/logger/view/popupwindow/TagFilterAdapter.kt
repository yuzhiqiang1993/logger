package com.yzq.logger.view.popupwindow

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yzq.logger.data.TagFilterItem
import com.yzq.logger.databinding.LayoutItemTagFilterBinding

/**
 * @description: TAG筛选适配器，支持多选
 * @author : yuzhiqiang
 */

internal class TagFilterAdapter(
    private val onSelectionChanged: () -> Unit
) : RecyclerView.Adapter<TagFilterAdapter.TagFilterViewHolder>() {

    private val tagItems = mutableListOf<TagFilterItem>()

    inner class TagFilterViewHolder(val binding: LayoutItemTagFilterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val tagItem = tagItems[position]
                    tagItem.selected = !tagItem.selected
                    notifyItemChanged(position)
                    onSelectionChanged()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagFilterViewHolder {
        val binding = LayoutItemTagFilterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TagFilterViewHolder(binding)
    }

    override fun getItemCount(): Int = tagItems.size

    override fun onBindViewHolder(holder: TagFilterViewHolder, position: Int) {
        val tagItem = tagItems[position]
        holder.binding.tvTag.text = tagItem.tag
        holder.binding.cbTag.isChecked = tagItem.selected
    }

    /**
     * 更新TAG列表
     */
    fun updateTags(tags: Set<String>, selectedTags: Set<String> = emptySet()) {
        tagItems.clear()
        tagItems.addAll(tags.sorted().map { tag ->
            TagFilterItem(tag, selectedTags.contains(tag))
        })
        notifyDataSetChanged()
    }

    /**
     * 获取选中的TAG
     */
    fun getSelectedTags(): Set<String> {
        return tagItems.filter { it.selected }.map { it.tag }.toSet()
    }

    /**
     * 全选
     */
    fun selectAll() {
        tagItems.forEach { it.selected = true }
        notifyDataSetChanged()
        onSelectionChanged()
    }

    /**
     * 清空选择
     */
    fun clearAll() {
        tagItems.forEach { it.selected = false }
        notifyDataSetChanged()
        onSelectionChanged()
    }
}

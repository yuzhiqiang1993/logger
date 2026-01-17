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
        // 由于布局根节点就是 TextView，并且 ID 为 tv_tag，所以 binding.tvTag 指向的就是根节点
        holder.binding.tvTag.text = tagItem.tag
        holder.binding.tvTag.isSelected = tagItem.selected
    }

    /**
     * 更新TAG列表
     */
    private val allTagItems = mutableListOf<TagFilterItem>()
    private var currentKeyword = ""

    /**
     * 更新TAG列表
     */
    fun updateTags(tags: Set<String>, selectedTags: Set<String> = emptySet()) {
        allTagItems.clear()
        allTagItems.addAll(tags.sorted().map { tag ->
            TagFilterItem(tag, selectedTags.contains(tag))
        })
        filter(currentKeyword)
    }

    /**
     * 过滤 TAG
     */
    fun filter(keyword: String) {
        currentKeyword = keyword
        tagItems.clear()
        if (keyword.isEmpty()) {
            tagItems.addAll(allTagItems)
        } else {
            tagItems.addAll(allTagItems.filter {
                it.tag.contains(keyword, ignoreCase = true)
            })
        }
        notifyDataSetChanged()
    }

    /**
     * 获取选中的TAG
     */
    fun getSelectedTags(): Set<String> {
        return allTagItems.filter { it.selected }.map { it.tag }.toSet()
    }

    /**
     * 全选（仅全选当前显示的）
     */
    fun selectAll() {
        tagItems.forEach { it.selected = true }
        notifyDataSetChanged()
        onSelectionChanged()
    }

    /**
     * 清空选择（清空所有）
     */
    fun clearAll() {
        allTagItems.forEach { it.selected = false }
        filter(currentKeyword)
        onSelectionChanged()
    }
}

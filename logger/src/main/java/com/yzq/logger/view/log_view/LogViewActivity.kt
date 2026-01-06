package com.yzq.logger.view.log_view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yzq.binding.viewBinding
import com.yzq.coroutine.flow.debounce
import com.yzq.logger.R
import com.yzq.logger.data.LogTypeItem
import com.yzq.logger.databinding.ActivityLogViewBinding
import com.yzq.logger.databinding.LayoutPopuWindowBinding
import com.yzq.logger.databinding.LayoutTagFilterPopupBinding
import com.yzq.logger.view.popupwindow.LogTypeAdapter
import com.yzq.logger.view.popupwindow.LogTypeClickListener
import com.yzq.logger.view.popupwindow.TagFilterAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * @description: 优化后的日志查看页面
 * @author : yuzhiqiang
 */
class LogViewActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityLogViewBinding::inflate)
    private val logVm = ViewModelProvider(
        ViewLogVMStoreOwner.instance,
        ViewLogVm.provideFactory()
    ).get(ViewLogVm::class.java)
    private val logAdapter = LogAdapter()

    // UI 组件
    private var logTypePopupWindow: PopupWindow? = null
    private var tagFilterPopupWindow: PopupWindow? = null
    private lateinit var tagFilterAdapter: TagFilterAdapter
    private val isAutoScrollEnabled = true
    private var selectedTags: Set<String> = emptySet()

    // 状态变量
    private var isAtBottom = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupStatusBar()
        initializeViews()
        setupObservers()
        setupListeners()
        updateLogCount()
    }

    private fun setupStatusBar() {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = Color.WHITE
    }

    private fun initializeViews() {
        initPopupWindow()
        initTagFilterPopup()
        setupRecyclerView()
        setupSearchView()
    }

    private fun setupRecyclerView() {
        binding.logRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@LogViewActivity)
            adapter = logAdapter

            // 添加滚动监听
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    checkScrollPosition()
                }
            })
        }
    }

    private fun setupSearchView() {
        // 设置搜索防抖
        binding.etSearch.debounce(300).onEach { query ->
            performSearch(query)
        }.launchIn(lifecycleScope)

        // 实时监听输入变化以显示/隐藏清除按钮
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.ivClearSearch.visibility =
                    if (s?.isNotEmpty() == true) View.VISIBLE else View.GONE
            }
        })

        // 清除搜索按钮
        binding.ivClearSearch.setOnClickListener {
            binding.etSearch.text?.clear()
            hideKeyboard()
        }
    }

    private fun setupObservers() {
        // 观察日志数据变化
        lifecycleScope.launch {
            logVm.logsSharedFlow?.collect { logItem ->
                logAdapter.addData(logItem)
                updateLogCount()

                // 如果开启自动滚动且当前在底部，则继续滚动到底部
                if (isAutoScrollEnabled && isAtBottom) {
                    scrollToBottomWithAnimation()
                }

                // 更新TAG筛选器的TAG列表
                updateTagFilterList()
            }
        }
    }

    private fun setupListeners() {
        // 日志类型筛选
        binding.btnLogType.setOnClickListener {
            toggleLogTypePopup()
        }

        // TAG筛选
        binding.btnTagFilter.setOnClickListener {
            toggleTagFilterPopup()
        }

        // 清空日志
        binding.btnClearLog.setOnClickListener {
            clearLogs()
        }

    }

    private fun initPopupWindow() {
        val popuWindowBinding = LayoutPopuWindowBinding.inflate(layoutInflater)
        logTypePopupWindow = PopupWindow(
            popuWindowBinding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            isOutsideTouchable = true
            animationStyle = android.R.style.Animation_Dialog
            isFocusable = true

            // 设置背景和阴影
            setBackgroundDrawable(getDrawable(R.drawable.popup_window_bg))
        }

        popuWindowBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@LogViewActivity)
            adapter = LogTypeAdapter(object : LogTypeClickListener {
                override fun onLogTypeClick(logTypeItem: LogTypeItem) {
                    logTypePopupWindow?.dismiss()
                    updateLogTypeIndicator(logTypeItem)
                    logAdapter.filterData(logType = logTypeItem.logType)
                    updateLogCount()
                }
            })
        }
    }

    private fun updateLogTypeIndicator(item: LogTypeItem) {
        binding.tvLogTypeIndicator.visibility = View.VISIBLE
        binding.tvLogTypeIndicator.text = item.type.firstOrNull()?.toString() ?: ""
    }

    private fun toggleLogTypePopup() {
        logTypePopupWindow?.let { popup ->
            if (popup.isShowing) {
                popup.dismiss()
            } else {
                popup.showAsDropDown(binding.btnLogType)
            }
        }
    }

    private fun initTagFilterPopup() {
        val popupBinding = LayoutTagFilterPopupBinding.inflate(layoutInflater)
        tagFilterPopupWindow = PopupWindow(
            popupBinding.root,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            isOutsideTouchable = true
            animationStyle = android.R.style.Animation_Dialog
            isFocusable = true
            setBackgroundDrawable(getDrawable(R.drawable.popup_window_bg))
        }

        tagFilterAdapter = TagFilterAdapter {
            onTagFilterChanged()
        }

        popupBinding.recyclerViewTags.apply {
            layoutManager = LinearLayoutManager(this@LogViewActivity)
            adapter = tagFilterAdapter
        }

        // 全选按钮
        popupBinding.tvSelectAll.setOnClickListener {
            tagFilterAdapter.selectAll()
        }

        // 清空按钮
        popupBinding.tvClearAll.setOnClickListener {
            tagFilterAdapter.clearAll()
        }
    }

    private fun toggleTagFilterPopup() {
        tagFilterPopupWindow?.let { popup ->
            if (popup.isShowing) {
                popup.dismiss()
            } else {
                // 更新TAG列表
                updateTagFilterList()
                // 全宽显示在工具栏下方
                popup.showAsDropDown(binding.toolbarCard, 0, 0)
            }
        }
    }

    private fun updateTagFilterList() {
        val allTags = logAdapter.getAllTags()
        if (allTags.isNotEmpty()) {
            tagFilterAdapter.updateTags(allTags, selectedTags)
        }
    }

    private fun onTagFilterChanged() {
        selectedTags = tagFilterAdapter.getSelectedTags()
        logAdapter.filterData(tags = selectedTags)
        updateLogCount()
        updateTagFilterButtonText()
    }

    private fun updateTagFilterButtonText() {
        if (selectedTags.isEmpty()) {
            binding.tvTagIndicator.visibility = View.GONE
            binding.tvTagIndicator.text = ""
        } else {
            binding.tvTagIndicator.visibility = View.VISIBLE
            binding.tvTagIndicator.text = "${selectedTags.size}"
        }
    }

    private fun performSearch(query: String) {
        logAdapter.filterData(keyWord = query)
        updateLogCount()
    }

    private fun clearLogs() {
        // 确认清空操作
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(getString(R.string.clear_logs_title))
            .setMessage(getString(R.string.clear_logs_message))
            .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                logVm.logsSharedFlow?.resetReplayCache()
                logAdapter.clearData()
                updateLogCount()
                Toast.makeText(this, getString(R.string.logs_cleared), Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun scrollToBottom() {
        scrollToPosition(logAdapter.itemCount - 1)
    }

    private fun scrollToBottomWithAnimation() {
        lifecycleScope.launch {
            delay(100) // 等待数据更新完成
            scrollToPosition(logAdapter.itemCount - 1)
        }
    }

    private fun scrollToPosition(position: Int) {
        val layoutManager = binding.logRecyclerView.layoutManager as LinearLayoutManager
        layoutManager.smoothScrollToPosition(binding.logRecyclerView, null, position)
    }

    private fun checkScrollPosition() {
        val layoutManager = binding.logRecyclerView.layoutManager as LinearLayoutManager
        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

        // 检查是否在底部
        val wasAtBottom = isAtBottom
        isAtBottom = (lastVisibleItemPosition == totalItemCount - 1) &&
                (visibleItemCount + firstVisibleItemPosition >= totalItemCount - 1)

        // 如果从非底部状态变为底部状态，且开启自动滚动，则滚动到底部
        if (!wasAtBottom && isAtBottom && isAutoScrollEnabled) {
            scrollToBottom()
        }
    }

    private fun updateLogCount() {
        val count = logAdapter.itemCount
        binding.etSearch.hint = "搜索... ($count)"
    }


    private fun hideKeyboard() {
        val imm =
            getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        logTypePopupWindow?.dismiss()
        tagFilterPopupWindow?.dismiss()
    }

    companion object Companion {
        fun createIntent(context: android.content.Context): Intent {
            return Intent(context, LogViewActivity::class.java)
        }
    }
}

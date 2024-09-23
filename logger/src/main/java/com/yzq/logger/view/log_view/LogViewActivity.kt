package com.yzq.logger.view.log_view

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.PopupWindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.yzq.binding.viewbind
import com.yzq.coroutine.flow.debounce
import com.yzq.logger.data.LogTypeItem
import com.yzq.logger.databinding.ActivityLogViewBinding
import com.yzq.logger.databinding.LayoutPopuWindowBinding
import com.yzq.logger.view.popupwindow.LogTypeAdapter
import com.yzq.logger.view.popupwindow.LogTypeClickListener
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


/**
 * @description: 日志查看页面
 * @author : yuzhiqiang
 */

class LogViewActivity : AppCompatActivity() {

    private val binding by viewbind(ActivityLogViewBinding::inflate)


    private val logVm = ViewModelProvider(
        ViewLogVMStoreOwner.instance,
        ViewLogVm.provideFactory()
    ).get(ViewLogVm::class.java)

    private val logAdapter = LogAdapter()

    //日志类型筛选弹窗
    private var logTypePopupWindow: PopupWindow? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window?.statusBarColor = Color.WHITE
        initView()
        observeData()


    }

    private fun observeData() {
        lifecycleScope.launch {
            logVm.logsSharedFlow?.collect {
                logAdapter.addData(it)
            }
        }
    }

    private fun initView() {
        initPopupWindow()


        binding.logRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@LogViewActivity)
            adapter = logAdapter
        }

        binding.etSearch.debounce(500).onEach {
            logAdapter.filterData(keyWord = it)
        }.launchIn(lifecycleScope)



        binding.tvType.setOnClickListener {
            //选择筛选的日志级别
            logTypePopupWindow?.run {
                if (isShowing) {
                    dismiss()
                    return@run
                }
                PopupWindowCompat.showAsDropDown(this, binding.tvType, 10, -30, Gravity.CENTER)
            }

        }

        binding.tvClear.setOnClickListener {
            logVm.logsSharedFlow?.resetReplayCache()
            logAdapter.clearData()
        }

        binding.tvTop.setOnClickListener {
            binding.logRecyclerView.smoothScrollToPosition(0)
        }

        binding.tvBottom.setOnClickListener {
            binding.logRecyclerView.smoothScrollToPosition(logAdapter.itemCount - 1)
        }


    }

    private fun initPopupWindow() {
        val popuWindowBinding = LayoutPopuWindowBinding.inflate(layoutInflater)
        // 创建 PopupWindow
        logTypePopupWindow = PopupWindow(
            popuWindowBinding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            isOutsideTouchable = true
            animationStyle = android.R.style.Animation_Dialog
            isFocusable = true
        }

        popuWindowBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@LogViewActivity)
            adapter = LogTypeAdapter(object : LogTypeClickListener {
                override fun onLogTypeClick(logTypeItem: LogTypeItem) {
                    logTypePopupWindow?.dismiss()

                    binding.tvType.text = logTypeItem.type
                    logAdapter.filterData(logType = logTypeItem.logType)
                }

            })
        }

    }
}
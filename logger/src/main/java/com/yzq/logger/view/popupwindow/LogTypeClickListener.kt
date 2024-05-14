package com.yzq.logger.view.popupwindow

import com.yzq.logger.data.LogTypeItem

internal interface LogTypeClickListener {
    fun onLogTypeClick(logTypeItem: LogTypeItem)
}
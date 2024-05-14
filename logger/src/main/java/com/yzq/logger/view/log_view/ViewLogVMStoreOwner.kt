package com.yzq.logger.view.log_view

import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner


/**
 * @description: 视图日志ViewModelStoreOwner，用于保证获取的ViewModelStore是同一个实例，从而使得ViewModel是同一个实例
 * @author : yuzhiqiang
 */


internal class ViewLogVMStoreOwner(override val viewModelStore: ViewModelStore) :
    ViewModelStoreOwner {
    companion object {

        val instance: ViewLogVMStoreOwner by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ViewLogVMStoreOwner(
                ViewModelStore()
            )
        }

    }
}
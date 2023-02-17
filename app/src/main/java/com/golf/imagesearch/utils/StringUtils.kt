package com.golf.imagesearch.utils

import com.golf.imagesearch.base.BaseApplication

fun Int.toResourceString() = BaseApplication.getInstance()?.getString(this)

fun Int.toResourceStringWithFormat(vararg formatArgs: Any?): String {
    return BaseApplication.getInstance()?.getString(this, *formatArgs).toString()
}
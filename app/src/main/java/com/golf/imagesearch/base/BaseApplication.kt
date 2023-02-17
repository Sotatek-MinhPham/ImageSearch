package com.golf.imagesearch.base

import android.app.Application
import com.golf.imagesearch.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber.DebugTree
import timber.log.Timber.Forest.plant


@HiltAndroidApp
class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        baseApplication = this
        if (BuildConfig.DEBUG) {
            plant(DebugTree())
        }
    }

    companion object {
        var baseApplication: BaseApplication? = null
        fun getInstance() = baseApplication
    }
}

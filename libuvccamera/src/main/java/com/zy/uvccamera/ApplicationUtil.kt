package com.zy.uvccamera

import android.app.Application

/**
 * @description：
 * @author: zhaoya
 * @create：2022/6/27 0027 17:48
 */
object ApplicationUtil {
    lateinit var application: Application
        private set

    fun init(application: Application) {
        if (!this::application.isInitialized) {
            this.application = application
        }
        YuvUtils.init(application)
    }
}
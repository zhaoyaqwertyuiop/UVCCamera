package com.example.myapplication

import android.app.Application
import com.zy.uvccamera.YuvUtils

/**
 * @description：
 * @author: zhaoya
 * @create：2022/5/30 0030 16:17
 */
class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()

//        YuvUtils.init(this)
//        JPush.init(this)
    }
}
package com.zy.uvccamera

import android.util.Log
import com.serenegiant.uvccamera.BuildConfig

/**
 * @description：
 * @author: zhaoya
 * @create：2022/3/24 0024
 */
object LogUtil {
    private var isLog = BuildConfig.DEBUG

    fun getIsLog(): Boolean? {
        return isLog
    }

    fun init(isLog: Boolean) {
        LogUtil.isLog = isLog
    }

    fun d(tag: String?, msg: String?) {
        if (isLog) {
            Log.d(tag, msg?:"")
        }
    }

    fun d(msg: String) {
        if (isLog) {
            Log.d("TAG", msg)
        }
    }

    fun i(tag: String?, msg: String?) {
        if (isLog) {
            Log.i(tag, msg?:"")
        }
    }

    fun e(tag: String = "TAG", msg: String) {
        if (isLog) {
            Log.e(tag, msg)
        }
    }

    fun v(tag: String?, msg: String?) {
        if (isLog) {
            Log.v(tag, msg?:"")
        }
    }

    fun w(tag: String?, msg: String) {
        if (isLog) {
            Log.w(tag, msg)
        }
    }
}
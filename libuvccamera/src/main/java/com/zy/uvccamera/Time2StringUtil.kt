package com.zy.uvccamera

import android.text.TextUtils
import java.text.SimpleDateFormat
import java.util.*

object Time2StringUtil{

    // 毫秒值换算成 天，时，分，秒
    fun timeFormate(time: Long, block:(Int, Int, Int, Int) -> Unit) {
        val s: Int = (time/1000%60).toInt() // 秒
        val m: Int = ((time/1000/60)%60).toInt() // 分钟
        val H: Int = ((time/1000/60/60)%24).toInt() // 小时
        val D: Int = (time/1000/60/60/24).toInt() // 天

        block(D, H, m, s)
    }

    /**
     * 毫秒值转string
     * @param time 毫秒值
     * @param formatStr yyyyMMddHHmmss
     * @return
     */
    fun format(time: String, formatStr: String?): String {
        if (TextUtils.isEmpty(time)) {
            return ""
        }
        val sdf = SimpleDateFormat(formatStr)
        val date = Date(time.toLong())
        return sdf.format(date)
    }

    fun format(time: Long, formatStr: String?): String {
        val sdf = SimpleDateFormat(formatStr)
        val date = Date(time)
        return sdf.format(date)
    }

    // 当前时间
    fun getCurrentDate(formatStr: String?): String {
        val sdf = SimpleDateFormat(formatStr)
        val date = Date(System.currentTimeMillis())
        return sdf.format(date)
    }
}
package com.popupwindow

import android.app.Activity
import android.view.View
import android.widget.TextView
import com.wheelview.R
import com.wheelview.wheel.ArrayWheelAdapter
import com.wheelview.wheel.WheelView

/**
 * Created by Administrator on 2018/3/15 0015.
 */
open class OnlyWheelPop(context: Activity, title: String, dataList: List<Any>, callback: WheelCallBack?) : DownPopupWindow(context) {

    var callBack = callback
    var dataList = dataList
    var title = title
    open var autoDismiss = true
    lateinit var pop_only_wheel: WheelView

    init {
        initView()
    }

    fun initView() {
        super.initView(IInitView { inflater ->
            var view = inflater.inflate(R.layout.down_pop_only_wheel, null, false)
            view.findViewById<TextView>(R.id.pop_wheel_title).text = title
            pop_only_wheel = view.findViewById(R.id.pop_only_wheel) as WheelView
            var adapter = ArrayWheelAdapter(inflater.context, dataList, 0)
            adapter.setItemResource(R.layout.item_wheel) // 设置轮子的item
            adapter.setItemTextResource(R.id.wheelTV)
            adapter.setItemTextCallBack {index ->
                return@setItemTextCallBack callBack?.setItemText(index)?:""
            }
            pop_only_wheel.viewAdapter = adapter

            var pop_wheel_ok_tv = view.findViewById(R.id.pop_wheel_ok_tv) as View
            pop_wheel_ok_tv.setOnClickListener {
                if (dataList.size == 0) {
                    this!!.dismiss()
                    return@setOnClickListener
                }
                callBack?.resultCallBack(pop_only_wheel.currentItem)
                if (autoDismiss) {
                    this!!.dismiss()
                }
            }

            return@IInitView view
        })
    }

    interface WheelCallBack {
        // 设置每个item的text
        fun setItemText(position: Int): String
        // 当前选中posiition
        fun resultCallBack(position: Int)
    }

    fun notifaceDataSetChanged() {
        pop_only_wheel.invalidateWheel(true)
    }
}
package com.wheelview

import android.content.Context
import com.wheelview.wheel.AbstractWheelTextAdapter
import com.wheelview.wheel.WheelView

/**
 * @description：联动工具
 * @author: zhaoya
 * @create：2022/6/6 0006 15:40
 */
object LinkWhellUtil {

    // 更新adapter
    fun updateAdapter(wheel: WheelView, list: List<IData>, currentItem: Int = 0) {
        val adapter = ArrayWheelAdapter(wheel.context, list)
        adapter.itemResource = R.layout.item_wheel // 设置轮子的item
        adapter.itemTextResource = R.id.wheelTV
        wheel.viewAdapter = adapter
        wheel.currentItem = currentItem // 默认选中项
    }

    /** 联动需要继承AbstractWheelTextAdapter  */
    /**
     * @param list 数据
     * @param level 联动的级数
     */
    class ArrayWheelAdapter(context: Context, private val list: List<IData>) : AbstractWheelTextAdapter(context) {

        public override fun getItemText(index: Int): CharSequence {
            return list[index]._getName()
        }

        override fun getItemsCount(): Int {
            return list.size
        }
    }

    /** 联动数据继承IData  */
    interface IData {
        fun _getName(): String // 显示的值
        fun _getID(): String // 值对应的id
    }
}
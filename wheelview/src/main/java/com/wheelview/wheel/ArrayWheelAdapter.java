/*
 *  Copyright 2011 Yuri Kanivets
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.wheelview.wheel;

import android.content.Context;

import java.util.List;

/**
 * The simple Array wheel adapter
 * 这是一个demo,配置联动数据
 * @param <T> the element type
 */
public class ArrayWheelAdapter<T> extends AbstractWheelTextAdapter {
    private  List<T> list;
    int type;
    private Callback callback;
    /**
     * Constructor
     * @param context the current context
     * @param list 数据
     */
    public ArrayWheelAdapter(Context context, List<T> list ,int type) {
        super(context);
        
//        setEmptyItemResource(TEXT_VIEW_ITEM_RESOURCE);
        this.list=list;
        this.type=type;
//        this.items = items;

        this.setItemResource(com.wheelview.R.layout.item_wheel); // 设置轮子的item
        this.setItemTextResource(com.wheelview.R.id.wheelTV);
    }
    
    @Override
    public CharSequence getItemText(int index) {
        if (callback == null) {
            if (index >= 0 && index < list.size()) {
                T item = list.get(index);
                if (item instanceof CharSequence) {
                    return (CharSequence) item;
                }
                return item.toString();
            }
            return "";
        } else {
            return callback.setItemText(index);
        }
//    	CharSequence result="";
//    	if(type == AddressInfo.Province){
//    		result=list.get(index).getProvinceName();
//    	}
//    	else if(type == AddressInfo.City){
//    		result=list.get(index).getCityName();
//    	}
//    	else if(type == AddressInfo.County){
//    		result=list.get(index).getCountyName();
//    	}
//        return result;
    }

    @Override
    public int getItemsCount() {
        return list.size();
    }

    public interface Callback {
        String setItemText(int index);
    }

    public void setItemTextCallBack(Callback callback) {
        this.callback = callback;
    }
}

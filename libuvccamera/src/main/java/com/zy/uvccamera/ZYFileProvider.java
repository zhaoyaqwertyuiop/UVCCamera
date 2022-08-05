package com.zy.uvccamera;

import android.app.Application;

import androidx.core.content.FileProvider;

public class ZYFileProvider extends FileProvider {

    @Override
    public boolean onCreate() {
        //noinspection ConstantConditions
        ApplicationUtil.INSTANCE.init((Application) getContext().getApplicationContext());
        return true;
    }
}
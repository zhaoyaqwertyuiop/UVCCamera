package com.example.myapplication

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import kotlinx.android.synthetic.main.dialog_image.*

class ImageDialog(context: Context) : Dialog(context) {

    init {
        setContentView(R.layout.dialog_image)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT) // 去除系统dialog自带的margin

        setCanceledOnTouchOutside(true)
        imagell.setOnClickListener {
            dismiss()
        }
    }

    fun setBitmap(bitmap: Bitmap): ImageDialog {
        image.setImageBitmap(bitmap)
        return this
    }

}
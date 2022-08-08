package com.example.myapplication

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.ConvertUtils
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions
import com.popupwindow.OnlyWheelPop
import com.serenegiant.encoder.MediaMuxerWrapper
import com.serenegiant.usb.Size
import com.zy.uvccamera.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    var takePicture = false
    var scanCode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUSBMonitor()

        openNext.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        select.setOnClickListener {
            val list = USBMonitorUtilHeight.getSupportedSizeList(this)?: listOf()
            OnlyWheelPop(this, "选择分辨率", list, object : OnlyWheelPop.WheelCallBack{
                override fun setItemText(position: Int): String {
                    return list[position].toString()
                }

                override fun resultCallBack(position: Int) {
                    val item = list[position]
                    USBMonitorUtilHeight.changeSize(this@MainActivity, item.width, item.height)
                    select.text = "${item.width}*${item.height}"
                }
            }).showPop()
        }

        scankit.setOnClickListener { // 识别bitmap
            scanCode = true
        }

        takePicture1.setOnClickListener { // 拍照
            takePicture = true
        }
    }


    private fun initUSBMonitor() {
        USBMonitorUtilHeight.initUSBMonitor(this, textureView1) {
            val bitmap = USBMonitorUtilHeight.frame2Bitmap(this, it)
            bitmap?.let {
                onBitmap(it)
            }
        }
    }

    private fun onBitmap(bitmap: Bitmap) {
        LogUtil.d("USBMonitorUtilHeight", "onFrame true")

        if (scanCode) {
            scanCode = false
            scankitCode(bitmap)
        }
        if (takePicture) {
            takePicture = false

            val path = "${this@MainActivity.getExternalFilesDir("capture/height/onFrame")?.absolutePath}/${getFileName()}"
            try {
//                    val bitmap: Bitmap = mWeakCameraView.get().captureStillImage()
                // get buffered output stream for saving a captured still image as a file on external storage.
                // the file name is came from current time.
                // You should use extension name as same as CompressFormat when calling Bitmap#compress.
                val outputFile = if (TextUtils.isEmpty(path)) MediaMuxerWrapper.getCaptureFile(
                    Environment.DIRECTORY_DCIM,
                    ".png"
                ) else File(path)
                val os = BufferedOutputStream(FileOutputStream(outputFile))
                try {
                    try {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os)
                        os.flush()
                    } catch (e: IOException) {
                    }
                } finally {
                    os.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getFileName(): String {
        return "${Time2StringUtil.getCurrentDate("yyyyMMddHHmmss")}.png"
    }

    fun scankitCode(mBitmap: Bitmap) = launch(Dispatchers.IO){


        Log.d("scan", "mBitmap:${mBitmap.byteCount}")
        // “QRCODE_SCAN_TYPE”和“DATAMATRIX_SCAN_TYPE”表示只扫描QR和Data Matrix的码
//            val options = HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE, HmsScan.DATAMATRIX_SCAN_TYPE, HmsScan.ALL_SCAN_TYPE).setPhotoMode(true).create()
        val options = HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.CODE128_SCAN_TYPE).setPhotoMode(true).create()
//                val options = HmsScanAnalyzerOptions.Creator().setPhotoMode(true).create()
        val hmsScans = ScanUtil.decodeWithBitmap(this@MainActivity, mBitmap, options)
// 处理扫码结果
        withContext(Dispatchers.Main) {
            if (hmsScans != null && hmsScans.size > 0) {
                // 展示扫码结果
                Toast.makeText(this@MainActivity, "识别成功:${hmsScans[0].originalValue}", Toast.LENGTH_SHORT).show()
                Log.d("scan", "识别成功:${hmsScans[0].originalValue}")
            } else {
                Toast.makeText(this@MainActivity, "识别失败", Toast.LENGTH_SHORT).show()
                Log.d("scan", "识别失败")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}
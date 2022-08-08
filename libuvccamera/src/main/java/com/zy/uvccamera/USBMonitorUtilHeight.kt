package com.zy.uvccamera

import android.hardware.usb.UsbDevice
import android.view.TextureView
import androidx.core.app.ComponentActivity
import com.serenegiant.usb.IFrameCallback
import com.serenegiant.widget.CameraViewInterface
import com.serenegiant.widget.UVCCameraTextureView

/**
 * @description：
 * @author: zhaoya
 * @create：2022/6/23 0023 17:34
 */
object USBMonitorUtilHeight: USBMonitorUtilBase() {

    // 支持的分辨率
    val resolution1 = Pair(640, 480)
    val resolution2 = Pair(800, 600)
    val resolution3 = Pair(1024, 768)
    val resolution4 = Pair(1280, 720)
    val resolution5 = Pair(1280, 960)
    val resolution6 = Pair(1600, 1200)
    val resolution7 = Pair(1920, 1080)
    val resolution8 = Pair(2048, 1536)
    val resolution9 = Pair(2592, 1944)
    val resolution10 = Pair(3264, 2448)

    val resolution = resolution5 // 使用的分辨率

    private const val vendorid = 6935
    private const val productId = 1336

    fun initUSBMonitor(context: ComponentActivity, mTextureView: TextureView?, mIFrameCallback: IFrameCallback? = null) {
        super.initUSBMonitor(context, mTextureView, resolution.first, resolution.second, mIFrameCallback)
    }

    override fun findDevice(usbDeviceList: List<UsbDevice>): UsbDevice? {
        return usbDeviceList.find { it.vendorId == vendorid && it.productId == productId }
    }
}
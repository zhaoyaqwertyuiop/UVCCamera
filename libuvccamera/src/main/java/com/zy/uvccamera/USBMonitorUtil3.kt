package com.zy.uvccamera

import android.hardware.usb.UsbDevice
import android.view.TextureView
import androidx.core.app.ComponentActivity
import com.serenegiant.usb.IFrameCallback
import com.serenegiant.widget.UVCCameraTextureView

/**
 * @description：
 * @author: zhaoya
 * @create：2022/6/23 0023 17:34
 */
object USBMonitorUtil3: USBMonitorUtilBase() {

    val resolution1 = Pair(640, 480)
    val resolution2 = Pair(320, 240)
    val resolution4 = Pair(1280, 720)
    val resolution6 = Pair(1600, 1200)
    val resolution7 = Pair(1920, 1080)
    val resolution8 = Pair(2048, 1536)
    val resolution9 = Pair(2560, 1440)
    val resolution10 = Pair(2592, 1944)

    val resolution = resolution1

    val vendorid = 7749
    val productId = 32802

    override fun findDevice(usbDeviceList: List<UsbDevice>): UsbDevice? {
        return usbDeviceList.find { it.vendorId == vendorid && it.productId == productId }
    }

    fun initUSBMonitor(context: ComponentActivity, mTextureView: TextureView, errCallback: ((Exception) -> Unit)? = null, mIFrameCallback: IFrameCallback? = null) {
        super.initUSBMonitor(context, resolution.first, resolution.second, mTextureView, errCallback, mIFrameCallback)
    }

}
package com.zy.uvccamera

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.hardware.usb.UsbDevice
import android.view.TextureView
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ComponentActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.serenegiant.dialog.MessageDialogFragmentV4
import com.serenegiant.usb.*
import com.serenegiant.usb.USBMonitor.OnDeviceConnectListener
import com.serenegiant.usb.USBMonitor.UsbControlBlock
import com.serenegiant.utils.PermissionCheck
import com.serenegiant.uvccamera.R
import com.serenegiant.widget.UVCCameraTextureView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.util.*

/**
 * @description：一个设备对应一个USBMonitorUtilBase，通过findDevice()找到设备
 * @author: zhaoya
 * @create：2022/6/23 0023 17:34
 */
abstract class USBMonitorUtilBase2 {

    private val TAG = "USBMonitorUtil"

    // 从设备列表里面找到并返回需要连接的设备
    abstract fun findDevice(usbDeviceList: List<UsbDevice>): UsbDevice?

    val filter = DeviceFilter.getDeviceFilters(ApplicationUtil.application, R.xml.device_filter)

//    private var mUSBMonitor: USBMonitor? = null // usb工具

    private val map = WeakHashMap<LifecycleOwner, MapValue>() // 保存activity对应的mUSBMonitor和mCameraHandler

    private class MapValue(
        var mUSBMonitor: USBMonitor? = null, // usb工具
        var config : UVCCameraUtil.UVCCameraConfig
    )
    private val mUVCCameraUtil = UVCCameraUtil()

    /**
     * @param mUVCCameraView 拍照使用
     */
    @SuppressLint("RestrictedApi")
    fun initUSBMonitor(context: ComponentActivity, mTextureView: TextureView?, mWidth: Int, mHeight: Int, mIFrameCallback: IFrameCallback? = null) {
        val mTextureView = mTextureView?:TextureView(context).apply {
            layoutParams = ViewGroup.LayoutParams(1, 1)
        }
        if (map.containsKey(context)) {
            return
        }

        val config = UVCCameraUtil.UVCCameraConfig().apply {
            this.mWidth = mWidth
            this.mHeight = mHeight
            this.mPreviewMode = UVCCamera.FRAME_FORMAT_MJPEG
            this.mIFrameCallback2 = mIFrameCallback
            this.textureView = mTextureView
        }

        val mUSBMonitor = USBMonitor(context, object : OnDeviceConnectListener {
            var isFirstIn = false // 首次插入设备
            override fun onAttach(device: UsbDevice?) {
                val list = map[context]?.mUSBMonitor?.getDeviceList(filter)
                list?.let {
                    if (findDevice(it) == null) {
                        isFirstIn = true
                    } else {
                        if (isFirstIn) {
                            isFirstIn = false
//                                map[context]?.let {
//                                    onStart(context, mUVCCameraViewInterface)
//                                }

                            context.lifecycleScope.launch {
                                delay(300)
                                requestPermission(map[context]?.mUSBMonitor!!)
                            }
                        }
                    }
                }
            }

            override fun onDettach(device: UsbDevice?) {
            }

            override fun onConnect(
                device: UsbDevice?,
                ctrlBlock: UsbControlBlock?,
                createNew: Boolean
            ) {
                context.lifecycleScope.launch {
                    mUVCCameraUtil.open(context, ctrlBlock)
                    mUVCCameraUtil.startPreview(context, map[context]!!.config)
                }
            }

            override fun onDisconnect(device: UsbDevice?, ctrlBlock: UsbControlBlock?) {
            }

            override fun onCancel(device: UsbDevice?) {
            }
        })

        mUSBMonitor!!.register()

        context.lifecycleScope.launch {
            delay(300)
            requestPermission(mUSBMonitor!!)
        }

        map[context] = MapValue(mUSBMonitor, config)

        context.lifecycle.addObserver(object : LifecycleObserver{
            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun onStart() {
                this@USBMonitorUtilBase2.onStart(context)
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun onStop() {
                mUSBMonitor?.unregister()
                this@USBMonitorUtilBase2.onStop()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestory() {
                mUSBMonitor?.destroy()
                mUVCCameraUtil.destory()
                map.remove(context)
            }
        })
    }

    // 找到设备
    fun findDevice(mUSBMonitor: USBMonitor): UsbDevice? {
        return findDevice(mUSBMonitor.getDeviceList(filter))
    }

    // 拍照, 需要 WRITE_EXTERNAL_STORAGE 权限
    fun capture(activity: AppCompatActivity, path: String? = null) {
//        map[activity]?.mCameraHandler?.let {
//            if (it.isOpened()) {
//                if (checkPermissionWriteExternalStorage(activity)) {
//                    if (path.isNullOrEmpty()) {
//                        it.captureStill()
//                    } else {
//                        it.captureStill(path)
//                    }
//                }
//            }
//        }
    }

    private fun requestPermissions(activity: AppCompatActivity, block:() -> Unit) {
        val perms = arrayOf(Permission.WRITE_EXTERNAL_STORAGE)
        XXPermissions.with(activity)
            .permission(perms)
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                    if (all) {
                        block()
                    }
                }
            })
    }

    // 動的パーミッション要求時の要求コード
    protected val REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 0x12345
    protected val REQUEST_PERMISSION_AUDIO_RECORDING = 0x234567
    protected val REQUEST_PERMISSION_NETWORK = 0x345678
    protected val REQUEST_PERMISSION_CAMERA = 0x537642

    /**
     * 外部ストレージへの書き込みパーミッションが有るかどうかをチェック
     * なければ説明ダイアログを表示する
     * @return true 外部ストレージへの書き込みパーミッションが有る
     */
    protected open fun checkPermissionWriteExternalStorage(context: FragmentActivity): Boolean {
        if (!PermissionCheck.hasWriteExternalStorage(context)) {
            MessageDialogFragmentV4.showDialog(
                context,
                REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE,
                R.string.permission_title,
                R.string.permission_ext_storage_request,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            )
            return false
        }
        return true
    }

    // 改变分辨率
    fun changeSize(owner: LifecycleOwner, width: Int, height: Int, textureView: TextureView? = null) {
        map[owner]?.config?.apply {
            mWidth = width
            mHeight = height
            textureView?.let {
                this.textureView = it
            }
        }

        map[owner]?.let {
            onStop()
            onStart(owner)
        }
    }

    private fun onStart(owner: LifecycleOwner) {
        LogUtil.d(TAG, "onStart()")

        map[owner]?.config?.let {
            mUVCCameraUtil.startPreview(owner, it)
        }
    }

    private fun onStop() {
        LogUtil.d(TAG, "onStop()")
        mUVCCameraUtil.stopPreview()
    }

    private fun requestPermission(mUSBMonitor: USBMonitor) {
        mUSBMonitor.let {
            val device = findDevice(it)
            it.requestPermission(device)
        }
    }

//    fun getWeight(owner: LifecycleOwner): Int? {
//        return map[owner]?.mCameraHandler?.width
//    }
//
//    fun getHeight(owner: LifecycleOwner): Int? {
//        return map[owner]?.mCameraHandler?.height
//    }

    fun frame2Bitmap(owner: LifecycleOwner, byteBuffer: ByteBuffer): Bitmap? {
        val len = byteBuffer.capacity()
        val yuv = ByteArray(len)
        byteBuffer[yuv]

        return map[owner]?.config?.let {
            YuvUtils.nv21ToBitmap(yuv, it.mWidth, it.mHeight)
        }
    }

    fun getSupportedSizeList(owner: LifecycleOwner): MutableList<Size>? {
        return mUVCCameraUtil.mUVCCamera?.supportedSizeList
    }
}
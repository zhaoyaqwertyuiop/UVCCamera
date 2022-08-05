//package com.zy.uvccamera
//
//import android.Manifest
//import android.annotation.SuppressLint
//import android.graphics.Bitmap
//import android.hardware.usb.UsbDevice
//import android.view.Surface
//import android.view.ViewGroup
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.ComponentActivity
//import androidx.fragment.app.FragmentActivity
//import androidx.lifecycle.*
//import com.hjq.permissions.OnPermissionCallback
//import com.hjq.permissions.Permission
//import com.hjq.permissions.XXPermissions
//import com.serenegiant.dialog.MessageDialogFragmentV4
//import com.serenegiant.usb.*
//import com.serenegiant.usb.USBMonitor.OnDeviceConnectListener
//import com.serenegiant.usb.USBMonitor.UsbControlBlock
//import com.serenegiant.usbcameracommon.UVCCameraHandlerKt2
//import com.serenegiant.utils.PermissionCheck
//import com.serenegiant.uvccamera.R
//import com.serenegiant.widget.UVCCameraTextureView
//import kotlinx.coroutines.*
//import java.nio.ByteBuffer
//
///**
// * @description：
// * @author: zhaoya
// * @create：2022/6/23 0023 17:34
// */
//abstract class USBMonitorUtil {
//
//    private val TAG = "USBMonitorUtil"
//
//    // 从设备列表里面找到并返回需要连接的设备
//    abstract fun findDevice(usbDeviceList: List<UsbDevice>): UsbDevice?
//
//    val filter = DeviceFilter.getDeviceFilters(ApplicationUtil.application, R.xml.device_filter)
//
//    class USBManager(
//        val mUSBMonitor: USBMonitor, // usb工具
////        val mCameraHandler: UVCCameraHandler, // 每个摄像头对应一个handler
////        val mCameraHandler: UVCCameraHandlerKt, // 每个摄像头对应一个handler
//        val mCameraHandler: UVCCameraHandlerKt2, // 每个摄像头对应一个handler
//    )
//
//    private val map = HashMap<LifecycleOwner, USBManager>() // 保存activity对应的mUSBMonitor和mCameraHandler
//
//    /**
//     * @param mUVCCameraView 拍照使用
//     */
//    @SuppressLint("RestrictedApi")
//    fun initUSBMonitor(context: ComponentActivity, mUVCCameraView: UVCCameraTextureView?, mWidth: Int, mHeight: Int, mIFrameCallback: IFrameCallback? = null) {
//        val mUVCCameraViewInterface = mUVCCameraView?:UVCCameraTextureView(context).apply {
//            layoutParams = ViewGroup.LayoutParams(1, 1)
//        }
//        if (map.containsKey(context)) {
//            return
//        }
//
////        val mCameraHandler = UVCCameraHandler.createHandler(
////            context, mUVCCameraView,
////            2, mWidth, mHeight, UVCCamera.FRAME_FORMAT_MJPEG
////        )
////        val mCameraHandler = UVCCameraHandlerKt(context).apply {
////            this.mWidth = mWidth
////            this.mHeight = mHeight
////            this.mPreviewMode = UVCCamera.FRAME_FORMAT_MJPEG
////            this.mIFrameCallback2 = mIFrameCallback
////        }
//
//        val config = UVCCameraHandlerKt2.UVCCameraConfig().apply {
//            this.mWidth = mWidth
//            this.mHeight = mHeight
//            this.mPreviewMode = UVCCamera.FRAME_FORMAT_MJPEG
//            this.mIFrameCallback2 = mIFrameCallback
//        }
//
//        var mSurface: Surface? = null
//
//        val mUSBMonitor = USBMonitor(context, object : OnDeviceConnectListener {
//            var isFirstIn = false // 首次插入设备
//            override fun onAttach(device: UsbDevice?) {
//                val list = map[context]?.mUSBMonitor?.getDeviceList(filter)
//                list?.let {
//                    if (findDevice(it) == null) {
//                        isFirstIn = true
//                    } else {
//                        if (isFirstIn) {
//                            isFirstIn = false
//                            map[context]?.let {
//                                onStart(context, it, mUVCCameraViewInterface)
//                            }
//                        }
//                    }
//                }
//            }
//
//            override fun onDettach(device: UsbDevice?) {
//                val list = map[context]?.mUSBMonitor?.getDeviceList(filter)?: listOf()
////                if (findDevice(list) == null) {
////                    UVCCameraHandlerKt2.mUVCCamera?.destroy()
////                    UVCCameraHandlerKt2.mUVCCamera = null
////                }
//            }
//
//            override fun onConnect(
//                device: UsbDevice?,
//                ctrlBlock: UsbControlBlock?,
//                createNew: Boolean
//            ) {
////                mCameraHandler?.let {
//////                    it.open(ctrlBlock)
////
////                    it.handleOpen(ctrlBlock)
////
////                    val st = mUVCCameraViewInterface.surfaceTexture
////                    mSurface?.release()
////                    st?.let {
////                        mSurface = Surface(st)
//////                        mCameraHandler.startPreview(mSurface)
////                        mCameraHandler.handleStartPreview(mSurface)
////                    }
////
//////                    it.setIFrameCallback2(mIFrameCallback)
////                }
//
//                context.lifecycleScope.launch {
////                    mCameraHandler?.let {
//////                    it.open(ctrlBlock)
////
////                        it.handleOpen(ctrlBlock)
////
////                        val st = mUVCCameraViewInterface.surfaceTexture
////                        mSurface?.release()
////                        st?.let {
////                            mSurface = Surface(st)
//////                        mCameraHandler.startPreview(mSurface)
////                            mCameraHandler.handleStartPreview(mSurface)
////                        }
////
//////                    it.setIFrameCallback2(mIFrameCallback)
////                    }
//
//                    UVCCameraHandlerKt2.handleOpen(context, ctrlBlock, config)
////                    val st = mUVCCameraViewInterface.surfaceTexture
////                    mSurface?.release()
////                    st?.let {
////                        mSurface = Surface(st)
//////                        mCameraHandler.startPreview(mSurface)
////                        UVCCameraHandlerKt2.handleStartPreview(context, mSurface)
////                    }
//                    startPreview(context, mUVCCameraViewInterface)
//                }
//            }
//
//            override fun onDisconnect(device: UsbDevice?, ctrlBlock: UsbControlBlock?) {
////                mCameraHandler?.close()
////                mCameraHandler?.handleClose()
//            }
//
//            override fun onCancel(device: UsbDevice?) {
//            }
//
//        })
//
//        map[context] = USBManager(mUSBMonitor, UVCCameraHandlerKt2)
//
//        context.lifecycle.addObserver(object : LifecycleObserver{
////            @OnLifecycleEvent(Lifecycle.Event.ON_START)
//            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
//            fun onStart() {
//                map[context]?.let {
//                    onStart(context, it, mUVCCameraViewInterface)
//                }
//            }
//
////            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
//            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
//            fun onStop() {
//                map[context]?.let {
//                    onStop(it)
//                }
//            }
//
//            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
//            fun onDestory() {
////                mCameraHandler?.release()
//                mUSBMonitor?.unregister()
//                mUSBMonitor?.destroy()
//                map.remove(context)
//            }
//        })
//
//        mUSBMonitor.register()
//
//        context.lifecycleScope.launch {
//            delay(300)
//            requestPermission(mUSBMonitor)
//        }
//
//    }
//
//    suspend fun startPreview(owner: LifecycleOwner, mUVCCameraViewInterface: UVCCameraTextureView) {
//        var mSurface: Surface? = null
//        val st = mUVCCameraViewInterface.surfaceTexture
//        mSurface?.release()
//        st?.let {
//            mSurface = Surface(st)
////                        mCameraHandler.startPreview(mSurface)
//            UVCCameraHandlerKt2.handleStartPreview(owner, mSurface)
//        }
//    }
//
//    // 找到设备
//    fun findDevice(mUSBMonitor: USBMonitor): UsbDevice? {
//        return findDevice(mUSBMonitor.getDeviceList(filter))
//    }
//
//    fun getUSBManager(owner: LifecycleOwner): USBManager? {
//        return map[owner]
//    }
//
//    // 拍照, 需要 WRITE_EXTERNAL_STORAGE 权限
//    fun capture(activity: AppCompatActivity, path: String? = null) {
////        map[activity]?.mCameraHandler?.let {
////            if (it.isOpened()) {
////                if (checkPermissionWriteExternalStorage(activity)) {
////                    if (path.isNullOrEmpty()) {
////                        it.captureStill()
////                    } else {
////                        it.captureStill(path)
////                    }
////                }
////            }
////        }
//
//        requestPermissions(activity) {
//            map[activity]?.mCameraHandler?.let {
////                if (it.isOpened()) {
////                    if (path.isNullOrEmpty()) {
////                        it.captureStill()
////                    } else {
////                        it.captureStill(path)
////                    }
////                }
//            }
//        }
//    }
//
//    private fun requestPermissions(activity: AppCompatActivity, block:() -> Unit) {
//        val perms = arrayOf(Permission.WRITE_EXTERNAL_STORAGE)
//        XXPermissions.with(activity)
//            .permission(perms)
//            .request(object : OnPermissionCallback {
//                override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
//                    if (all) {
//                        block()
//                    }
//                }
//            })
//    }
//
//    // 動的パーミッション要求時の要求コード
//    protected val REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 0x12345
//    protected val REQUEST_PERMISSION_AUDIO_RECORDING = 0x234567
//    protected val REQUEST_PERMISSION_NETWORK = 0x345678
//    protected val REQUEST_PERMISSION_CAMERA = 0x537642
//
//    /**
//     * 外部ストレージへの書き込みパーミッションが有るかどうかをチェック
//     * なければ説明ダイアログを表示する
//     * @return true 外部ストレージへの書き込みパーミッションが有る
//     */
//    protected open fun checkPermissionWriteExternalStorage(context: FragmentActivity): Boolean {
//        if (!PermissionCheck.hasWriteExternalStorage(context)) {
//            MessageDialogFragmentV4.showDialog(
//                context,
//                REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE,
//                R.string.permission_title,
//                R.string.permission_ext_storage_request,
//                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//            )
//            return false
//        }
//        return true
//    }
//
//    // 改变分辨率
//    fun changeSize(owner: LifecycleOwner, width: Int, height: Int, mUVCCameraViewInterface: UVCCameraTextureView) {
//        map[owner]?.mCameraHandler?.changeSize(owner, width, height)
//
////        map[owner]?.mCameraHandler?.let {
////            it.mWidth = width
////            it.mHeight = height
////        }
//        map[owner]?.let {
//            onStop(it)
//            onStart(owner, it, mUVCCameraViewInterface)
////            requestPermission(it.mUSBMonitor)
//        }
//    }
//
//    private fun onStart(owner: LifecycleOwner, usbManager: USBManager, mUVCCameraViewInterface: UVCCameraTextureView) {
//        LogUtil.d(TAG, "onStart()")
////        usbManager.mUSBMonitor.unregister()
////        usbManager.mUSBMonitor.register()
//
////        owner.lifecycleScope.launch{
////            delay(300)
////            requestPermission(usbManager.mUSBMonitor)
////        }
//
//        owner.lifecycleScope.launch {
//            startPreview(owner, mUVCCameraViewInterface)
//        }
//    }
//
//    private fun onStop(usbManager: USBManager) {
//        LogUtil.d(TAG, "onStop()")
////        usbManager.mCameraHandler.close()
//        usbManager.mCameraHandler.handleClose()
////        usbManager.mUSBMonitor.unregister()
//    }
//
//    private fun requestPermission(mUSBMonitor: USBMonitor) {
//        mUSBMonitor.let {
//            val device = findDevice(it)
//            it.requestPermission(device)
//        }
//    }
//
////    fun getWeight(owner: LifecycleOwner): Int? {
////        return map[owner]?.mCameraHandler?.width
////    }
////
////    fun getHeight(owner: LifecycleOwner): Int? {
////        return map[owner]?.mCameraHandler?.height
////    }
//
//    fun frame2Bitmap(owner: LifecycleOwner, byteBuffer: ByteBuffer): Bitmap? {
//        val len = byteBuffer.capacity()
//        val yuv = ByteArray(len)
//        byteBuffer[yuv]
//
//        val handler = map[owner]?.mCameraHandler
//        if (handler == null) {
//            return null
//        }
////        return YuvUtils.nv21ToBitmap(yuv, handler.width, handler.height)
////        return YuvUtils.nv21ToBitmap(yuv, handler.mWidth, handler.mHeight)
//        return YuvUtils.nv21ToBitmap(yuv, handler.getConfig(owner)!!.mWidth, handler.getConfig(owner)!!.mHeight)
//    }
//
//    fun getSupportedSizeList(owner: LifecycleOwner): MutableList<Size>? {
//        return map[owner]?.mCameraHandler?.mUVCCamera?.supportedSizeList
//    }
//}
//package com.serenegiant.usbcameracommon
//
//import android.graphics.SurfaceTexture
//import android.view.Surface
//import android.view.SurfaceHolder
//import androidx.lifecycle.*
//import com.serenegiant.encoder.MediaMuxerWrapper
//import com.serenegiant.encoder.MediaVideoBufferEncoder
//import com.serenegiant.usb.IFrameCallback
//import com.serenegiant.usb.USBMonitor.UsbControlBlock
//import com.serenegiant.usb.UVCCamera
//import com.zy.uvccamera.LogUtil
//import kotlinx.coroutines.delay
//
///**
// * @description：替代 UVCCameraHandler 和 AbstractUVCCameraHandler，方便使用(一个object对应一个设备，有多个设备需要多个object)
// * @author: zhaoya
// * @create：2022/7/22 0022 14:55
// */
//object UVCCameraHandlerKt2 {
//    val TAG = "UVCCameraHandlerKt"
//
//    // 一个camera多个页面打开的时候可能有多个配置信息
//    class UVCCameraConfig {
//        var mWidth = 640
//        var mHeight = 480
//        var mPreviewMode = UVCCamera.FRAME_FORMAT_MJPEG // FRAME_FORMAT_YUYV(0) or FRAME_FORMAT_MJPEG(1)
//        val mBandwidthFactor = 0f
//        var mIFrameCallback2: IFrameCallback? = null
//    }
//
//
//    private var mIsPreviewing = false
//    private val mIsRecording = false
//
//
//    var mUVCCamera: UVCCamera? = null
//
//    /**
//     * muxer for audio/video recording
//     */
//    private val mMuxer: MediaMuxerWrapper? = null
//    private val mVideoEncoder: MediaVideoBufferEncoder? = null
//
//    private val map = HashMap<LifecycleOwner, UVCCameraConfig>() // 保存owner 和 UVCCameraConfig
//
//    fun handleOpen(owner: LifecycleOwner, ctrlBlock: UsbControlBlock?, config: UVCCameraConfig) {
//
//        if (map.size == 0) {
//            try {
//                val camera = UVCCamera()
//                camera.open(ctrlBlock)
//                mUVCCamera = camera
////            callOnOpen()
//            } catch (e: Exception) {
//                e.printStackTrace()
////            callOnError(e)
//            }
//
//            LogUtil.d(TAG, "handleOpen supportedSize: ${mUVCCamera?.getSupportedSize()}")
//        }
//
//        map[owner] = config
//        if (map.containsKey(owner)) {
//
//        } else {
//            owner.lifecycle.addObserver(object : LifecycleObserver{
//                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
//                fun onDestory() {
//                    map.remove(owner)
//                    if (map.size == 0) {
//                        mUVCCamera?.destroy()
//                        mUVCCamera = null
//                    }
//                }
//            })
//        }
//    }
//
//    fun handleClose() {
////        owner.lifecycleScope.launch {
//            LogUtil.d(TAG, "handleClose:")
//
//        handleStopPreview()
//
////        mUVCCamera?.apply {
////            stopPreview()
////            destroy()
////        }
//
////        handleStopRecording()
////            val camera: UVCCamera?
////            synchronized(mSync) { camera = mUVCCamera }
////            if (camera != null) {
////                camera.stopPreview()
////                camera.destroy()
//////            callOnClose()
////            }
////        }
//    }
//
//    open suspend fun handleStartPreview(owner: LifecycleOwner, surface: Any?) {
//        LogUtil.d(TAG, "handleStartPreview:")
//        if (mUVCCamera == null || mIsPreviewing) return
//        if (owner.lifecycle.currentState == Lifecycle.State.RESUMED) { // 当前是resumed，直接preview
//            LogUtil.d(TAG, "handleStartPreview case1:")
//            doPreview(owner, surface)
//        } else { // 不是，则等到是的时候再preview
//            owner.lifecycleScope.launchWhenResumed {
//                LogUtil.d(TAG, "handleStartPreview case2:")
//                delay(100)
//                doPreview(owner, surface)
//            }
//        }
//
//    }
//
//    private fun doPreview(owner: LifecycleOwner, surface: Any?) {
//        map[owner]?.apply {
//            try {
//                mUVCCamera!!.setPreviewSize(mWidth, mHeight, 1, 31, mPreviewMode, mBandwidthFactor)
//            } catch (e: IllegalArgumentException) {
//                try {
//                    // fallback to YUV mode
//                    mUVCCamera!!.setPreviewSize(
//                        mWidth,
//                        mHeight,
//                        1,
//                        31,
//                        UVCCamera.DEFAULT_PREVIEW_MODE,
//                        mBandwidthFactor
//                    )
//                } catch (e1: IllegalArgumentException) {
//                    e1.printStackTrace()
////                callOnError(e1)
//                    return
//                }
//            }
//            if (surface is SurfaceHolder) {
//                mUVCCamera!!.setPreviewDisplay(surface as SurfaceHolder?)
//            }
//            if (surface is Surface) {
//                mUVCCamera!!.setPreviewDisplay(surface as Surface?)
//            } else {
//                mUVCCamera!!.setPreviewTexture(surface as SurfaceTexture?)
//            }
//            mUVCCamera!!.startPreview()
//            mUVCCamera!!.updateCameraParams()
////            synchronized(mSync) { mIsPreviewing = true }
//            mIsPreviewing = true
//            setmIFrameCallback2(owner)
//        }
//    }
//
//    // 设置mIFrameCallback2
//    open fun setmIFrameCallback2(owner: LifecycleOwner) {
//        map[owner]?.mIFrameCallback2?.let {
//            mUVCCamera?.setFrameCallback(it, UVCCamera.PIXEL_FORMAT_NV21)
//        }
//    }
//
//    private fun handleStopPreview() {
//        if (mIsPreviewing) {
//            if (mUVCCamera != null) {
//                mUVCCamera!!.stopPreview()
//                mUVCCamera!!.setFrameCallback(null, 0) // 取消mIFrameCallback2
//            }
//            mIsPreviewing = false
////            callOnStopPreview()
//        }
//        LogUtil.d(TAG, "handleStopPreview:finished")
//    }
//
//    fun changeSize(owner: LifecycleOwner, width: Int, height: Int) {
//        map[owner]?.let {
//            it.mWidth = width
//            it.mHeight = height
//        }
//    }
//
//    fun getConfig(owner: LifecycleOwner): UVCCameraConfig? {
//        return map[owner]
//    }
//}
//package com.serenegiant.usbcameracommon
//
//import android.graphics.SurfaceTexture
//import android.view.Surface
//import android.view.SurfaceHolder
//import androidx.lifecycle.LifecycleOwner
//import androidx.lifecycle.lifecycleScope
//import com.serenegiant.encoder.MediaMuxerWrapper
//import com.serenegiant.encoder.MediaVideoBufferEncoder
//import com.serenegiant.usb.IFrameCallback
//import com.serenegiant.usb.USBMonitor.UsbControlBlock
//import com.serenegiant.usb.UVCCamera
//import com.serenegiant.usbcameracommon.AbstractUVCCameraHandler.CameraCallback
//import com.serenegiant.widget.CameraViewInterface
//import com.zy.uvccamera.LogUtil
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//import java.lang.ref.WeakReference
//import java.util.concurrent.CopyOnWriteArraySet
//
///**
// * @description：替代 UVCCameraHandler 和 AbstractUVCCameraHandler，方便使用
// * @author: zhaoya
// * @create：2022/7/22 0022 14:55
// */
//class UVCCameraHandlerKt(private val owner: LifecycleOwner) {
//    val TAG = "UVCCameraHandlerKt"
//
//    private val mWeakTextture: WeakReference<CameraViewInterface>? = null
//    private val mEncoderType = 0
//    private val mCallbacks: Set<CameraCallback> = CopyOnWriteArraySet()
//
//    var mWidth = 640
//    var mHeight = 480
//    var mPreviewMode = UVCCamera.FRAME_FORMAT_MJPEG // FRAME_FORMAT_YUYV(0) or FRAME_FORMAT_MJPEG(1)
//    private val mBandwidthFactor = 0f
//    private var mIsPreviewing = false
//    private val mIsRecording = false
//
//    var mIFrameCallback2: IFrameCallback? = null
//
//    /**
//     * for accessing UVC camera
//     */
//    var mUVCCamera: UVCCamera? = null
//
//    /**
//     * muxer for audio/video recording
//     */
//    private val mMuxer: MediaMuxerWrapper? = null
//    private val mVideoEncoder: MediaVideoBufferEncoder? = null
//
//    fun handleOpen(ctrlBlock: UsbControlBlock?) {
//        handleClose()
//        try {
//
//            val camera = UVCCamera()
//            camera.open(ctrlBlock)
//            mUVCCamera = camera
////            callOnOpen()
//        } catch (e: Exception) {
//            e.printStackTrace()
////            callOnError(e)
//        }
//        LogUtil.d(TAG, "handleOpen supportedSize: ${mUVCCamera?.getSupportedSize()}")
//    }
//
//    fun handleClose() {
////        owner.lifecycleScope.launch {
//            LogUtil.d(TAG, "handleClose:")
//
//        handleStopPreview()
//
//        mUVCCamera?.apply {
//            stopPreview()
//            destroy()
//        }
//        mUVCCamera = null
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
//    open fun handleStartPreview(surface: Any?) {
////        owner.lifecycleScope.launch {
//            LogUtil.d(TAG, "handleStartPreview:")
//            if (mUVCCamera == null || mIsPreviewing) return
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
//            setmIFrameCallback2()
////        callOnStartPreview()
////        }
//    }
//
//    // 设置mIFrameCallback2
//    open fun setmIFrameCallback2() {
//        owner.lifecycleScope.launch(Dispatchers.IO) {
//            if (mIFrameCallback2 != null) {
//                mUVCCamera?.setFrameCallback(mIFrameCallback2, UVCCamera.PIXEL_FORMAT_NV21)
//            }
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
//}
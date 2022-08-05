package com.zy.uvccamera

import android.graphics.SurfaceTexture
import android.view.Surface
import android.view.SurfaceHolder
import android.view.TextureView
import androidx.lifecycle.*
import com.serenegiant.encoder.MediaMuxerWrapper
import com.serenegiant.encoder.MediaVideoBufferEncoder
import com.serenegiant.usb.IFrameCallback
import com.serenegiant.usb.USBMonitor.UsbControlBlock
import com.serenegiant.usb.UVCCamera
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @description：替代 UVCCameraHandler 和 AbstractUVCCameraHandler，方便使用(一个object对应一个设备，有多个设备需要多个object)
 * @author: zhaoya
 * @create：2022/7/22 0022 14:55
 */
class UVCCameraUtil {
    val TAG = "UVCCameraUtil"

    private var mIsPreviewing = false
    private var mIsRecording = false
    private var willPreview = false

    var mUVCCamera: UVCCamera? = null
        private set

    /**
     * muxer for audio/video recording
     */
    private val mMuxer: MediaMuxerWrapper? = null
    private val mVideoEncoder: MediaVideoBufferEncoder? = null

    private val previewWateMastTime = 1000 // 准备preview最多等待时间，超过则跳出循环，preview失败

    open class UVCCameraConfig {
        var mWidth = 640
        var mHeight = 480
        var mPreviewMode = UVCCamera.FRAME_FORMAT_MJPEG // FRAME_FORMAT_YUYV(0) or FRAME_FORMAT_MJPEG(1)
        val mBandwidthFactor = 0f
        var mIFrameCallback2: IFrameCallback? = null
        var textureView: TextureView? = null
    }

    fun open(owner: LifecycleOwner, ctrlBlock: UsbControlBlock?) {

        try {
            val camera = UVCCamera()
            camera.open(ctrlBlock)
            mUVCCamera = camera
            mIsPreviewing = false
//            callOnOpen()
        } catch (e: Exception) {
            e.printStackTrace()
//            callOnError(e)
        }
    }

    fun destory() {
        LogUtil.d(TAG, "destory:")
        mUVCCamera?.startPreview()
        mUVCCamera?.destroy()
        mUVCCamera = null
    }

    fun startPreview(owner: LifecycleOwner, config: UVCCameraConfig) {
        LogUtil.d(TAG, "handleStartPreview:")
        if (mUVCCamera == null || mIsPreviewing) return

        if (config.textureView == null) {
            return
        }

        willPreview = true
        owner.lifecycleScope.launch(Dispatchers.IO) {
            var startTime = System.currentTimeMillis()
            while (willPreview && !mIsPreviewing && System.currentTimeMillis() - startTime <= previewWateMastTime) {
                config.textureView!!.surfaceTexture?.let {
                    doPreview(owner, config, it)
                }
                delay(50)
            }
        }
    }

    private fun doPreview(owner: LifecycleOwner, config: UVCCameraConfig, surface: SurfaceTexture) {
        if (mUVCCamera == null || mIsPreviewing) {
            return
        }
        try {
            mUVCCamera!!.setPreviewSize(config.mWidth, config.mHeight, 1, 31, config.mPreviewMode, config.mBandwidthFactor)
        } catch (e: IllegalArgumentException) {
            try {
                // fallback to YUV mode
                mUVCCamera!!.setPreviewSize(
                    config.mWidth,
                    config.mHeight,
                    1,
                    31,
                    UVCCamera.DEFAULT_PREVIEW_MODE,
                    config.mBandwidthFactor
                )
            } catch (e1: IllegalArgumentException) {
                e1.printStackTrace()
//                callOnError(e1)
                return
            }
        }

        if (surface is SurfaceHolder) {
            mUVCCamera!!.setPreviewDisplay(surface as SurfaceHolder?)
        }
        if (surface is Surface) {
            mUVCCamera!!.setPreviewDisplay(surface as Surface?)
        } else {
            mUVCCamera!!.setPreviewTexture(surface as SurfaceTexture?)
        }


        mUVCCamera!!.startPreview()
        mUVCCamera!!.updateCameraParams()
//            synchronized(mSync) { mIsPreviewing = true }
        mIsPreviewing = true

        // 设置mIFrameCallback2
        config.mIFrameCallback2?.let {
            mUVCCamera!!.setFrameCallback(it, UVCCamera.PIXEL_FORMAT_NV21)
        }
    }

    fun stopPreview() {
        willPreview = false
        if (mIsPreviewing) {
            if (mUVCCamera != null) {
                mUVCCamera!!.stopPreview()
                mUVCCamera!!.setFrameCallback(null, 0) // 取消mIFrameCallback2
            }
            mIsPreviewing = false
//            callOnStopPreview()
        }
        LogUtil.d(TAG, "handleStopPreview:finished")
    }
}
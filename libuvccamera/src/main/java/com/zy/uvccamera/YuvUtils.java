package com.zy.uvccamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.media.Image;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.util.Log;

import java.nio.ByteBuffer;

public class YuvUtils {
    private static final String TAG = YuvUtils.class.getSimpleName();

    private static RenderScript rs = null;
    private static ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic = null;
    private static ScriptIntrinsicYuvToRGB yuvToRgbIntrinsicRGB = null;

//    private static Type.Builder nv21Type, rgbaType;
//    private static Allocation in, out;

    private static RenderScript getRenderScript(Context context) {
        if (rs == null) {
            synchronized (YuvUtils.class) {
                if (rs == null) {
                    rs = RenderScript.create(context);
                }
            }
        }
        return rs;
    }

    private static ScriptIntrinsicYuvToRGB getYuvToRgbIntrinsic(Context context) {
        if (yuvToRgbIntrinsic == null) {
            synchronized (YuvUtils.class) {
                if (yuvToRgbIntrinsic == null) {
                    RenderScript renderScript = getRenderScript(context);
                    yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(renderScript, Element.U8_4(rs));
                }
            }
        }
        return yuvToRgbIntrinsic;
    }

    private static ScriptIntrinsicYuvToRGB getYuvToRgbIntrinsicRGB(Context context) {
        if (yuvToRgbIntrinsicRGB == null) {
            synchronized (YuvUtils.class) {
                if (yuvToRgbIntrinsicRGB == null) {
                    RenderScript renderScript = getRenderScript(context);
                    yuvToRgbIntrinsicRGB = ScriptIntrinsicYuvToRGB.create(renderScript, Element.U8_3(rs));
                }
            }
        }
        return yuvToRgbIntrinsicRGB;
    }

    public static void init(Context context) {
        getRenderScript(context);
        getYuvToRgbIntrinsic(context);
        getYuvToRgbIntrinsicRGB(context);
    }

    public static void destroy() {
        rs.destroy();
        rs = null;
        yuvToRgbIntrinsic.destroy();
        yuvToRgbIntrinsic = null;
        yuvToRgbIntrinsicRGB.destroy();
        yuvToRgbIntrinsicRGB = null;
    }

    public static byte[] yuvToRGBA(byte[] nv21, int width, int height, int format, byte[] rgba){
        if (rs == null || yuvToRgbIntrinsic == null || nv21 == null || width < 1 || height < 1
        || rgba == null || rgba.length < width * height * 4) {
            Log.e(TAG, "nv21ToRGBA params error: rs=" + rs
                    + ", yToR=" + yuvToRgbIntrinsic
                    + ", buf=" + nv21 + ", size=" + nv21.length
                    + ", w=" + width + "x" + height);
            return null;
        }

        Type.Builder nv21Type, rgbaType;
        Allocation in, out;

        if (format != ImageFormat.NV21 && format != ImageFormat.YV12) {
            format = ImageFormat.NV21;
        }
        nv21Type = new Type.Builder(rs, Element.U8(rs)).setX(width).setY(height).setYuvFormat(format);
        in = Allocation.createTyped(rs, nv21Type.create(), Allocation.USAGE_SCRIPT);

        rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs)).setX(width).setY(height);
        out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);

        in.copyFrom(nv21);
        yuvToRgbIntrinsic.setInput(in);
        yuvToRgbIntrinsic.forEach(out);

//        byte[] rgba = new byte[out.getBytesSize()];
        out.copyTo(rgba);

        in.destroy();
        out.destroy();
        return rgba;
    }

    public static Bitmap nv21ToBitmap(byte[] nv21, int width, int height){
        if (rs == null || yuvToRgbIntrinsic == null || nv21 == null || width < 1 || height < 1) {
            return null;
        }

        Type.Builder nv21Type, rgbaType;
        Allocation in, out;

        nv21Type = new Type.Builder(rs, Element.U8(rs)).setX(width).setY(height).setYuvFormat(ImageFormat.NV21);
        in = Allocation.createTyped(rs, nv21Type.create(), Allocation.USAGE_SCRIPT);

        rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs)).setX(width).setY(height);
        out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);

        in.copyFrom(nv21);
        yuvToRgbIntrinsic.setInput(in);
        yuvToRgbIntrinsic.forEach(out);
//        Log.e(TAG, "mama= nv21ToRGBA byteSize=" + out.getBytesSize());

        Bitmap bmpout = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        out.copyTo(bmpout);
        return bmpout;
    }

    public static byte[] YUV_420_888toGRAY(Image image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int ySize = width*height;

        byte[] y = new byte[ySize];

        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer(); // Y

        int rowStride = image.getPlanes()[0].getRowStride();
        assert(image.getPlanes()[0].getPixelStride() == 1);

        int pos = 0;
        if (rowStride == width) { // likely
            yBuffer.get(y, 0, ySize);
            pos += ySize;
        } else {
            int yBufferPos = width - rowStride; // not an actual position
            for (; pos<ySize; pos+=width) {
                yBufferPos += rowStride - width;
                yBuffer.position(yBufferPos);
                yBuffer.get(y, pos, width);
            }
        }

        return y;
    }

    public static byte[] YUV_420_888toNV21(ByteBuffer yBuffer, ByteBuffer uBuffer, ByteBuffer vBuffer) {
        byte[] nv;

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        nv = new byte[ySize + uSize + vSize];

        yBuffer.get(nv, 0, ySize);
        vBuffer.get(nv, ySize, vSize);
        uBuffer.get(nv, ySize + vSize, uSize);
        return nv;
    }

    public static byte[] YUV_420_888toYUV(Image image, int[] outFormat) {
        byte[] nv;

        Image.Plane plane0 = image.getPlanes()[0];
        Image.Plane plane1 = image.getPlanes()[1];
        Image.Plane plane2 = image.getPlanes()[2];

        ByteBuffer yBuffer = plane0.getBuffer();
        ByteBuffer uBuffer = plane1.getBuffer();
        ByteBuffer vBuffer = plane2.getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        nv = new byte[ySize + uSize + vSize];

        if (plane1.getPixelStride() == 1 && plane2.getPixelStride() == 1) {
            // YV12
            if (outFormat.length > 0) outFormat[0] = ImageFormat.YV12;
            yBuffer.get(nv, 0, ySize);
            uBuffer.get(nv, ySize, uSize);
            vBuffer.get(nv, ySize + uSize, vSize);

        } else {
            // nv21
            if (outFormat.length > 0) outFormat[0] = ImageFormat.NV21;
            yBuffer.get(nv, 0, ySize);
            vBuffer.get(nv, ySize, vSize);
            uBuffer.get(nv, ySize + vSize, uSize);
        }

//        yBuffer.get(nv, 0, ySize);
//        vBuffer.get(nv, ySize, vSize);
//        uBuffer.get(nv, ySize + vSize, uSize);
        return nv;
    }
}

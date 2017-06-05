package com.jkt.tcompress;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Allen at 2017/6/5 17:31
 */
public class Compress {
    private int mQuality = 80;
    private int mMaxHeight =960;
    private int mMaxWeight = 680;
    private Bitmap.CompressFormat mFormat = Bitmap.CompressFormat.JPEG;
    private Bitmap.Config mConfig = Bitmap.Config.ARGB_8888;

    public Bitmap getCompressedBitmap(Bitmap bitmap) {
        Bitmap ret = null;
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        float ratio = setRatio(width, height);
        Log.i("ratioinfo",ratio+"  "+(int) (width * ratio)+"          "+(int) (height * ratio));
        ret = Bitmap.createBitmap((int) (width * ratio), (int) (height * ratio), mConfig);
        Canvas canvas = new Canvas(ret);
        canvas.drawBitmap(bitmap, null, new RectF(0, 0, ret.getWidth(),ret.getHeight()), null);
        return ret;

    }

    public File bitmap2File(Bitmap bitmap) {
        File ret = null;
        try {
            String prefix = String.valueOf(System.currentTimeMillis());
            String suffix = null;
            switch (mFormat) {
                case JPEG:
                    suffix = "jpg";
                case PNG:
                    suffix = "png";
                case WEBP:
                    suffix = "webp";
            }
            ret = File.createTempFile(prefix, suffix);
            ret.deleteOnExit();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileOutputStream outputStream = new FileOutputStream(ret);
            bitmap.compress(mFormat, mQuality, baos);
            outputStream.write(baos.toByteArray());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    private float setRatio(int width, int height) {
        float ratio = 1;
        if (mMaxWeight < width && mMaxHeight < height) {
            if (mMaxWeight /(float) width < mMaxHeight /(float) height) ratio = mMaxWeight /(float) width;
            else ratio = mMaxHeight /(float) height;
        } else if (mMaxWeight < width) ratio = mMaxWeight /(float) width;
        else if (mMaxHeight < height) ratio = mMaxHeight /(float) height;
        return ratio;
    }
    //--------------------------------设置参数--------------------------------------


    public void setMaxHeight(int maxHeight) {
        mMaxHeight = maxHeight;
    }

    public void setMaxWeight(int maxWeight) {
        mMaxWeight = maxWeight;
    }

    public void setQuality(int quality) {
        mQuality = quality;
    }

    public void setConfig(Bitmap.Config config) {
        mConfig = config;
    }

    public void setFormat(Bitmap.CompressFormat format) {
        mFormat = format;
    }

    //---------------------------------构建者模式---------------------------------------
    public static class Build {
        private Compress mCompress;

        public Build() {
            mCompress = new Compress();
        }

        public void setMaxHeight(int height) {
            mCompress.mMaxHeight = height;
        }

        public void setMaxWeight(int weight) {
            mCompress.mMaxWeight = weight;
        }

        public void setQuality(int quality) {
            mCompress.mQuality = quality;
        }

        public void setConfig(Bitmap.Config config) {
            mCompress.mConfig = config;
        }

        public void setFormat(Bitmap.CompressFormat format) {
            mCompress.mFormat = format;
        }

        public Compress create() {
            return mCompress;
        }
    }

}

package com.jkt.tcompress;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    //默认属性，通过构造者模式或者set方法设置
    private int mQuality = 80;
    private float mMaxHeight = 1280;
    private float mMaxWeight = 960;
    private Bitmap.CompressFormat mFormat = Bitmap.CompressFormat.JPEG;
    private Bitmap.Config mConfig = Bitmap.Config.ARGB_8888;

    public File compressedToFile(File srcFile) {
        Bitmap bitmap = BitmapFactory.decodeFile(srcFile.getAbsolutePath());
        File file = compressedToFile(bitmap);
        return file;
    }

    public File compressedToFile(Bitmap bitmap) {
        Bitmap compressedBitmap = compressedToBitmap(bitmap);
        File file = bitmap2File(compressedBitmap);
        return file;
    }

    public Bitmap compressedToBitmap(File srcFile) {
        Bitmap bitmap = BitmapFactory.decodeFile(srcFile.getAbsolutePath());
        Bitmap compressedBitmap = compressedToBitmap(bitmap);
        return compressedBitmap;
    }

    public Bitmap compressedToBitmap(Bitmap bitmap) {
        Bitmap ret = null;
        float height = bitmap.getHeight();
        float width = bitmap.getWidth();
        float ratio = setRatio(width, height);
        Log.i("ratioinfo", ratio + "  " + (int) (width * ratio) + "  " + (int) (height * ratio));
        ret = Bitmap.createBitmap((int) (width * ratio), (int) (height * ratio), mConfig);
        Canvas canvas = new Canvas(ret);
        canvas.drawBitmap(bitmap, null, new RectF(0, 0, ret.getWidth(), ret.getHeight()), null);
        bitmap.recycle();
        return ret;
    }


    private File bitmap2File(Bitmap bitmap) {
        File ret = null;
        try {
            String prefix = String.valueOf(System.currentTimeMillis());
            String suffix = null;
            switch (mFormat) {
                case JPEG:
                    suffix = ".jpg";
                    break;
                case PNG:
                    suffix = ".png";
                    break;
                case WEBP:
                    suffix = ".webp";
                    break;
            }
            ret = File.createTempFile(prefix, suffix);
            ret.deleteOnExit();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileOutputStream outputStream = new FileOutputStream(ret);
            bitmap.compress(mFormat, mQuality, baos);
            bitmap.recycle();
            outputStream.write(baos.toByteArray());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    private float setRatio(float width, float height) {
        float ratio = 1;
        if (mMaxWeight < width && mMaxHeight < height) {
            if (mMaxWeight /  width < mMaxHeight / height)
                ratio = mMaxWeight /  width;
            else ratio = mMaxHeight /  height;
        } else if (mMaxWeight < width) ratio = mMaxWeight /  width;
        else if (mMaxHeight < height) ratio = mMaxHeight /  height;
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
    public static class Builder {
        private Compress mCompress;

        public Builder() {
            mCompress = new Compress();
        }

        public Builder setMaxHeight(int height) {
            mCompress.mMaxHeight = height;
            return this;
        }

        public Builder setMaxWeight(int weight) {
            mCompress.mMaxWeight = weight;
            return this;
        }

        public Builder setQuality(int quality) {
            mCompress.mQuality = quality;
            return this;
        }

        public Builder setConfig(Bitmap.Config config) {
            mCompress.mConfig = config;
            return this;
        }

        public Builder setFormat(Bitmap.CompressFormat format) {
            mCompress.mFormat = format;
            return this;
        }

        public Compress build() {
            return mCompress;
        }
    }

}

package com.jkt.tcompress;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.media.ExifInterface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Allen at 2017/6/5 17:31
 */
public class Compress {
    //默认属性，通过构造者模式或者set方法设置
    private int mQuality = 80;
    private float mMaxHeight = 1280;
    private float mMaxWidth = 960;
    private Bitmap.CompressFormat mFormat = Bitmap.CompressFormat.JPEG;
    private Bitmap.Config mConfig = Bitmap.Config.ARGB_8888;

    //---------------------主线路：文件里面的图片压缩完毕存入文件---------------------------------
    public File compressedToFile(File srcFile) {
        Bitmap bitmap = getBitmap(srcFile);
        Bitmap compressedBitmap = compressedToBitmap(bitmap);
        File file = bitmap2File(compressedBitmap);
        bitmap.recycle();
        compressedBitmap.recycle();
        return file;
    }

    private Bitmap getBitmap(File srcFile) {
        BitmapFactory.Options options = getOptions(srcFile);
        Bitmap bitmap = BitmapFactory.decodeFile(srcFile.getAbsolutePath(), options);
        bitmap = rotateBitmap(bitmap, srcFile);
        return bitmap;
    }

    private BitmapFactory.Options getOptions(File srcFile) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(srcFile.getAbsolutePath(), options);
        options.inSampleSize = setSampleSize(options.outWidth, options.outHeight);
        options.inJustDecodeBounds = false;
        return options;
    }

    private int setSampleSize(int outWidth, int outHeight) {
        int sampleSize = 1;
        while (outWidth > mMaxWidth * (sampleSize + 1) && outHeight > mMaxHeight * (sampleSize + 1)) {
            sampleSize++;
        }
        return sampleSize;
    }

    private Bitmap rotateBitmap(Bitmap bitmap, File srcFile) {
        int degree = getPictureDegree(srcFile);
        if (degree == 0) {
            return bitmap;
        }
        Matrix matrix = new Matrix();
        Bitmap ret = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        matrix.setRotate(degree, ret.getWidth() / 2, ret.getHeight() / 2);
        Canvas canvas = new Canvas(ret);
        canvas.drawBitmap(bitmap, matrix, null);
        return ret;
    }

    private static int getPictureDegree(File file) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(file.getAbsolutePath());
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    //bitmap到压缩后的bitmap
    public Bitmap compressedToBitmap(Bitmap bitmap) {
        Bitmap ret = null;
        float height = bitmap.getHeight();
        float width = bitmap.getWidth();
        float ratio = setRatio(width, height);
        ret = Bitmap.createBitmap((int) (width * ratio), (int) (height * ratio), mConfig);
        Canvas canvas = new Canvas(ret);
        canvas.drawBitmap(bitmap, null, new RectF(0, 0, ret.getWidth(), ret.getHeight()), null);
        return ret;
    }

    private float setRatio(float width, float height) {
        float ratio = 1;
        if (mMaxWidth < width && mMaxHeight < height) {
            if (mMaxWidth / width < mMaxHeight / height)
                ratio = mMaxWidth / width;
            else ratio = mMaxHeight / height;
        } else if (mMaxWidth < width) ratio = mMaxWidth / width;
        else if (mMaxHeight < height) ratio = mMaxHeight / height;
        return ratio;
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
            outputStream.write(baos.toByteArray());
            outputStream.flush();
            baos.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    //-------扩展（bitmap到压缩后的bitmap，已经包含在，文件到压缩后的文件的步骤之中）----------------------------------
    //文件图片压缩到新的bitmap
    public Bitmap compressedToBitmap(File srcFile) {
        Bitmap bitmap = getBitmap(srcFile);
        Bitmap compressedBitmap = compressedToBitmap(bitmap);
        bitmap.recycle();
        return compressedBitmap;
    }

    //bitmap压缩到新的文件
    public File compressedToFile(Bitmap bitmap) {
        Bitmap compressedBitmap = compressedToBitmap(bitmap);
        File file = bitmap2File(compressedBitmap);
        compressedBitmap.recycle();
        return file;
    }


    //--------------------------------设置参数--------------------------------------


    public void setMaxHeight(int maxHeight) {
        mMaxHeight = maxHeight;
    }

    public void setMaxWidth(int maxWidth) {
        mMaxWidth = maxWidth;
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

        public Builder setMaxWidth(int weight) {
            mCompress.mMaxWidth = weight;
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

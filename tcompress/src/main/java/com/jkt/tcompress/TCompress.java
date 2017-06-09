package com.jkt.tcompress;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.os.Handler;
import android.os.Message;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Allen at 2017/6/5 17:31
 */
public class TCompress {
    //默认属性，通过构造者模式或者set方法设置
    private int mQuality = 80;
    private float mMaxHeight = 1280;
    private float mMaxWidth = 960;
    private Bitmap.CompressFormat mFormat = Bitmap.CompressFormat.JPEG;
    private Bitmap.Config mConfig = Bitmap.Config.ARGB_8888;

    //---------------------主线路：文件里面的图片压缩完毕存入文件---------------------------------
    public File compressedToFile(File file) {
        File ret = null;
        try {
            Bitmap bitmap = getBitmap(file);
            Bitmap compressedBitmap = compressedToBitmap(bitmap);
            ret = bitmap2File(compressedBitmap);
            bitmap.recycle();
            compressedBitmap.recycle();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return ret;
    }

    private Bitmap getBitmap(File file) {
        BitmapFactory.Options options = getOptions(file);
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        bitmap = rotateBitmap(bitmap, file);
        return bitmap;
    }

    private BitmapFactory.Options getOptions(File file) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
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

    private Bitmap rotateBitmap(Bitmap bitmap, File file) {
        int degree = getPictureDegree(file);
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
        try {
            float height = bitmap.getHeight();
            float width = bitmap.getWidth();
            float ratio = setRatio(width, height);
            ret = Bitmap.createBitmap((int) (width * ratio), (int) (height * ratio), mConfig);
            Canvas canvas = new Canvas(ret);
            canvas.drawBitmap(bitmap, null, new RectF(0, 0, ret.getWidth(), ret.getHeight()), null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
    public Bitmap compressedToBitmap(File file) {
        Bitmap ret = null;
        try {
            Bitmap bitmap = getBitmap(file);
            ret = compressedToBitmap(bitmap);
            bitmap.recycle();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return ret;
    }

    //bitmap压缩到新的文件
    public File compressedToFile(Bitmap bitmap) {
        File ret = null;
        try {
            Bitmap compressedBitmap = compressedToBitmap(bitmap);
            ret = bitmap2File(compressedBitmap);
            compressedBitmap.recycle();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return ret;
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
        private TCompress mTCompress;

        public Builder() {
            mTCompress = new TCompress();
        }

        public Builder setMaxHeight(int height) {
            mTCompress.mMaxHeight = height;
            return this;
        }

        public Builder setMaxWidth(int weight) {
            mTCompress.mMaxWidth = weight;
            return this;
        }

        public Builder setQuality(int quality) {
            mTCompress.mQuality = quality;
            return this;
        }

        public Builder setConfig(Bitmap.Config config) {
            mTCompress.mConfig = config;
            return this;
        }

        public Builder setFormat(Bitmap.CompressFormat format) {
            mTCompress.mFormat = format;
            return this;
        }

        public TCompress build() {
            return mTCompress;
        }
    }

    //-------------------------------添加异步处理----------------------------------------------

    //添加属性
    public OnCompressListener mListener;
    public Handler mHandler;


    //--------------------------------Handler--------------------------------------------------

    private Handler getHandler() {
        return new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (mListener != null) {
                    if (msg.obj == null) mListener.onCompressFinish(false, null);
                    else mListener.onCompressFinish(true, msg.obj);
                }
                return true;
            }
        });
    }

    //-------------------------------------异步方法-------------------------------------------

    //文件压缩到文件
    public void compressToFileAsync(final File file,  OnCompressListener listener) {
        mListener = listener;
        mHandler = getHandler();
        listener.onCompressStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                File target = compressedToFile(file);
                Message message = mHandler.obtainMessage();
                message.obj = target;
                message.sendToTarget();
            }
        }).start();

    }

    //Bitmap压缩到文件
    public void compressToFileAsync(final Bitmap bitmap,  OnCompressListener listener) {
        mListener = listener;
        mHandler = getHandler();
        listener.onCompressStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                File target = compressedToFile(bitmap);
                Message message = mHandler.obtainMessage();
                message.obj = target;
                message.sendToTarget();
            }
        }).start();
    }

    //文件压缩到Bitmap
    public void compressToBitmapAsync(final File file, OnCompressListener listener) {
        mListener = listener;
        mHandler = getHandler();
        listener.onCompressStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap target = compressedToBitmap(file);
                Message message = mHandler.obtainMessage();
                message.obj = target;
                message.sendToTarget();
            }
        }).start();
    }

    //Bitmap压缩到Bitmap
    public void compressToBitmapAsync(final Bitmap bitmap, OnCompressListener listener) {
        mListener = listener;
        mHandler = getHandler();
        listener.onCompressStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap target = compressedToBitmap(bitmap);
                Message message = mHandler.obtainMessage();
                message.obj = target;
                message.sendToTarget();
            }
        }).start();
    }

}

package com.jkt.compress;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jkt.tcompress.Compress;
import com.jkt.tcompress.FileUtil;
import com.jkt.tcompress.OnCompressListener;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private ImageView mOriginIV;
    private ImageView mCompressedIV;
    private TextView mOriginTV;
    private TextView mCompressedTV;
    private File mFile;
    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    private void initViews() {
        setContentView(R.layout.activity_main);
        mOriginIV = (ImageView) findViewById(R.id.main_origin_iv);
        mCompressedIV = (ImageView) findViewById(R.id.main_compressed_iv);
        mOriginTV = (TextView) findViewById(R.id.main_origin_tv);
        mCompressedTV = (TextView) findViewById(R.id.main_compressed_tv);

    }

    public void btnSelect(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
    }

    public void btnCompress(View view) {
        if (mFile == null) {
            showToast("请先选择照片");
            return;
        }
        //可以构造者方式设置,也可以创建对象设置属性值
        Compress compress = new Compress.Builder()
                .setMaxWidth(800)
                .setMaxHeight(900)
                .setQuality(80)
                .setFormat(Bitmap.CompressFormat.JPEG)
                .setConfig(Bitmap.Config.RGB_565)
                .build();
//        Compress compress = new Compress();
//        compress.setConfig(Bitmap.Config.RGB_565);
//        compress.setFormat(Bitmap.CompressFormat.WEBP);
//        compress.setQuality(80);
//        compress.setMaxWidth(800);
//        compress.setMaxHeight(800);


        //支持四种压缩转化，文件、Bitmap到压缩后的文件、Bitmap
        //同步、异步都支持4种压缩
        //----------------同步处理----------------------------
//        sync(compress);

        //----------------异步处理----------------------------
        async(compress);

    }

    private void async(Compress compress) {
        //泛型设置回调类型。如果不指定泛型，也可以根据方法名的ToFile、ToBitmap进行强转
        //文件压缩到指定文件
        compress.compressToFileAsync(mFile, new OnCompressListener<File>() {
            //onCompressStart是非抽象方法，可选监听 可以开启提示框等
            @Override
            public void onCompressStart() {
                showToast("开始压缩");
            }

            @Override
            public void compressFinish(boolean success, File file) {
                if (success) {
                    showData(file);
                } else {
                    //请查看文件权限问题（其他问题基本不存在，如果有任何机型问题，请反馈谢谢）
                    showToast("压缩失败了，请查看日志");
                }
            }
        });
        //----------------其他三种异步压缩类似-------------
//        otherThreeAsync();

    }

    private void otherThreeAsync() {
        //如果调用多个压缩方法，注意查看多个监听对象泛型是否一致。不一致，请创建多个
        //compress对象分别调用压缩方法。避免回调时候类型转化异常（调用方法时候指定回调参数类型）

        //Bitmap压缩到文件
        Compress compress1 = new Compress();
        compress1.compressToFileAsync(mBitmap, new OnCompressListener<File>() {
            @Override
            public void compressFinish(boolean success, File file) {
                if (success) {
                    showData(file);
                    Log.i("async", "1----------------");
                }
            }
        });
        //文件压缩到Bitmap
        Compress compress2 = new Compress();
        compress2.compressToBitmapAsync(mFile, new OnCompressListener<Bitmap>() {
            @Override
            public void compressFinish(boolean success, Bitmap bitmap) {
                if (success) {
                    mCompressedIV.setImageBitmap(bitmap);
                    Log.i("async", "2----------------");

                }
            }
        });
        //Bitmap压缩到Bitmap
        Compress compress3 = new Compress();
        compress3.compressToBitmapAsync(mBitmap, new OnCompressListener<Bitmap>() {
            @Override
            public void compressFinish(boolean success, Bitmap bitmap) {
                if (success) {
                    mCompressedIV.setImageBitmap(bitmap);
                    Log.i("async", "3----------------");
                }
            }
        });
    }

    private void sync(Compress compress) {
        File compressedFile = compress.compressedToFile(mFile);
        if (compressedFile == null) {
            //请查看文件权限问题（其他问题基本不存在，如果有任何机型问题，请反馈谢谢）
            showToast("压缩失败了，请查看日志");
            return;
        }
        //另外三种
//        File compressedFile1 = compress.compressedToFile(mBitmap);
//        Bitmap bitmap = compress.compressedToBitmap(mFile);
//        Bitmap bitmap1 = compress.compressedToBitmap(mBitmap);

        //数据显示
        showData(compressedFile);
    }

    private void showData(File compressedFile) {
        Bitmap bm = BitmapFactory.decodeFile(compressedFile.getAbsolutePath());
        mCompressedIV.setImageBitmap(bm);
        mCompressedTV.setText(String.format("Size : %s", FileUtil.getFileSize(compressedFile.length())));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            if (data == null) {
                showToast("Failed to open picture!");
                return;
            }
            mFile = FileUtil.createTempFile(this, data.getData());
            if (mFile == null) {
                return;
            }
            mBitmap = BitmapFactory.decodeFile(mFile.getAbsolutePath());
            mOriginIV.setImageBitmap(mBitmap);
            mOriginTV.setText(String.format("Size : %s", FileUtil.getFileSize(mFile.length())));
        }
    }


    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }


}

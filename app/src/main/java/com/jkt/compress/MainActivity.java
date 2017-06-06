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
        if (mFile==null) {
            showToast("请先选择照片");
            return;
        }
        //可以构造者方式设置,也可以创建对象设置属性值
        Compress compress = new Compress.Builder()
                .setMaxWeight(800)
                .setMaxHeight(800)
                .setQuality(80)
                .setFormat(Bitmap.CompressFormat.JPEG)
                .setConfig(Bitmap.Config.RGB_565)
                .build();
//        Compress compress = new Compress();
//        compress.setConfig(Bitmap.Config.RGB_565);
//        compress.setFormat(Bitmap.CompressFormat.WEBP);
//        compress.setQuality(80);
//        compress.setMaxWeight(800);
//        compress.setMaxHeight(800);



        //可以压缩到bitmap或者文件，参数可以是bitmap或者文件
        File compressedFile = compress.compressedToFile(mFile);
//        Bitmap bitmap = compress.compressedToBitmap(mFile);
//        File compressedFile1 = compress.compressedToFile(mBitmap);
//        Bitmap bitmap1 = compress.compressedToBitmap(mBitmap);

        mCompressedIV.setImageBitmap(BitmapFactory.decodeFile(compressedFile.getAbsolutePath()));
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
            mFile = FileUtil.createFile(this, data.getData());
            mBitmap = BitmapFactory.decodeFile(mFile.getAbsolutePath());
            mOriginIV.setImageBitmap(mBitmap);
            mOriginTV.setText(String.format("Size : %s", FileUtil.getFileSize(mFile.length())));
            Log.i("bitmapinfo",mBitmap+"   "+mBitmap.getWidth()+"     "+mBitmap.getHeight());
        }
    }


    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }


}

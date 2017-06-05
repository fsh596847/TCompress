package com.jkt.compress;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ImageView mOriginIV;
    private ImageView mCompressedIV;
    private TextView mOriginTV;
    private TextView mCompressedTV;

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
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            if (data == null) {
                showToast("Failed to open picture!");
                return;
            }
        }
    }


    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }


}

package cn.com.shijizl.customerfiling.me;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.com.shijizl.customerfiling.R;
import cn.com.shijizl.customerfiling.base.BaseActivity;

public class CropImageActivity extends BaseActivity {
    private String url;
    private ImageView imageView;

    public static void start(Context context, String url) {
        Intent intent = new Intent(context, CropImageActivity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        url = getIntent().getStringExtra("url");
        Log.e("======", "======" + url);
        initView();
        cropImage();
    }

    private void initView() {
        imageView = (ImageView) findViewById(R.id.iv_crop_image);
        Glide.with(CropImageActivity.this)
                .load(url)
                .placeholder(R.drawable.place_holder)
                .into(imageView);

        TextView tvCancel = (TextView) findViewById(R.id.tv_cancel);
        TextView tvSure = (TextView) findViewById(R.id.tv_sure);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void cropImage() {
        Intent intent = new Intent();

        intent.setAction("com.android.camera.action.CROP");
        intent.setDataAndType(Uri.parse(url), "image/*");// mUri是已经选择的图片Uri
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        intent.putExtra("return-data", true);

        CropImageActivity.this.startActivityForResult(intent, 200);
    }

    private void saveImage(Intent data) {
        // 拿到剪切数据
        Bitmap bmap = data.getParcelableExtra("data");

        // 图像保存到文件中
        FileOutputStream foutput = null;
        try {
            File tempFile = new File("", "header");
            foutput = new FileOutputStream(tempFile);
            bmap.compress(Bitmap.CompressFormat.PNG, 100, foutput);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally{
            if(null != foutput){
                try {
                    foutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

package cn.com.shijizl.customerfiling.me;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

import cn.com.shijizl.customerfiling.R;
import cn.com.shijizl.customerfiling.base.BaseActivity;
import cn.com.shijizl.customerfiling.net.NetModel;
import cn.com.shijizl.customerfiling.net.UpdateModel;
import cn.com.shijizl.customerfiling.net.model.UpdateImageResponse;
import cn.com.shijizl.customerfiling.net.model.UserInfoResponse;
import cn.com.shijizl.customerfiling.utils.GlideCircleTransform;
import cn.com.shijizl.customerfiling.utils.SettingUtils;
import cn.com.shijizl.customerfiling.utils.Utils;
import me.iwf.photopicker.PhotoPicker;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeActivity extends BaseActivity {
    private ImageView ivHeader;
    private TextView tvName;
    private TextView tvNick;

    private String imageUrl;
    private String name;

    public static void start(Context context) {
        Intent intent = new Intent(context, MeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        getUserInfo();
    }

    private void initView() {
        ImageView ivBack = (ImageView) findViewById(R.id.iv_back_me);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivHeader = (ImageView) findViewById(R.id.iv_header_me);
        ivHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkImage();
            }
        });
        tvName = (TextView) findViewById(R.id.tv_name_me);
        tvNick = (TextView) findViewById(R.id.tv_nick_me);
        RelativeLayout rlNick = (RelativeLayout) findViewById(R.id.rl_nick_me);
        rlNick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateNickActivity.start(MeActivity.this, imageUrl, name);
            }
        });
        RelativeLayout rlChange = (RelativeLayout) findViewById(R.id.rl_password_me);
        rlChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdatePasswordActivity.start(MeActivity.this);
            }
        });

        Button btExit = (Button) findViewById(R.id.bt_next_me);
        btExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    private void getUserInfo() {
        if (!Utils.isNetworkOn()) {
            Toast.makeText(this, "网络异常，请检查您的网络！", Toast.LENGTH_SHORT).show();
            return;
        }
        Call<UserInfoResponse> call = NetModel.getInstance().getUserInfo(SettingUtils.instance().getToken());
        call.enqueue(new Callback<UserInfoResponse>() {
            @Override
            public void onResponse(Call<UserInfoResponse> call, Response<UserInfoResponse> response) {
                if (response.body().getCode() == 0) {
                    UserInfoResponse.DataBean data = response.body().getData();
                    if (data != null) {
                        imageUrl = data.getProfile();
                        Glide.with(MeActivity.this)
                                .load(data.getProfile())
                                .override(200, 200)
                                .bitmapTransform(new GlideCircleTransform(MeActivity.this))
                                .crossFade(1000)
                                .into(ivHeader);
                        tvName.setText(data.getUserName());
                        tvNick.setText(data.getRealName());
                        name = data.getRealName();
                    }
                } else {
                    Toast.makeText(MeActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserInfoResponse> call, Throwable t) {
                Toast.makeText(MeActivity.this, "获取用户信息失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkImage() {
        PhotoPicker.builder()
                .setPhotoCount(1)
                .setShowCamera(true)
                .setShowGif(true)
                .setPreviewEnabled(false)
                .start(this, PhotoPicker.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                String image = photos.get(0);
                updateImage(image);
                Glide.with(MeActivity.this)
                        .load(image)
                        .override(200, 200)
                        .bitmapTransform(new GlideCircleTransform(MeActivity.this))
                        .crossFade(1000)
                        .placeholder(R.drawable.place_holder)
                        .into(ivHeader);
            }
        }
    }

    private void updateImage(String url) {
        if (!Utils.isNetworkOn()) {
            Toast.makeText(this, "网络异常，请检查您的网络！", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(url);

        MultipartBody.Part body = prepareFilePart(file.getName(), file);

        Call<UpdateImageResponse> call = UpdateModel.getInstance().updateImage(body);
        call.enqueue(new Callback<UpdateImageResponse>() {
            @Override
            public void onResponse(Call<UpdateImageResponse> call, Response<UpdateImageResponse> response) {

                if (response.body().getCode() == 0) {
                    UpdateImageResponse.DataBean data = response.body().getData();
                    if (data != null) {
                        imageUrl = data.getUrl();
                        Toast.makeText(MeActivity.this, "上传图片成功", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MeActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UpdateImageResponse> call, Throwable t) {
                Toast.makeText(MeActivity.this, "上传图片失败，请重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private MultipartBody.Part prepareFilePart(String partName, File file) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);

        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MeActivity.this);
        builder.setMessage("您确实是否退出账户");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                SettingUtils.instance().clear();
                LoginActivity.start(MeActivity.this);
                finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}

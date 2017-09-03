package cn.com.shijizl.customerfiling.me;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.util.ArrayList;

import cn.com.shijizl.customerfiling.R;
import cn.com.shijizl.customerfiling.base.BaseActivity;
import cn.com.shijizl.customerfiling.net.NetModel;
import cn.com.shijizl.customerfiling.net.UpdateModel;
import cn.com.shijizl.customerfiling.net.model.EmptyResponse;
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
    private boolean isFirst = true;

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
                if (response.body() != null) {
                    if (response.body().getCode() == 0) {
                        UserInfoResponse.DataBean data = response.body().getData();
                        if (data != null) {
                            if (isFirst) {
                                isFirst = false;
                                imageUrl = data.getProfile();
                                if (!TextUtils.isEmpty(data.getProfile())) {
                                    Glide.with(MeActivity.this)
                                            .load(data.getProfile())
                                            .override(200, 200)
                                            .bitmapTransform(new GlideCircleTransform(MeActivity.this))
                                            .crossFade(1000)
                                            .into(ivHeader);
                                }
                            }
                            tvName.setText(data.getUserName());
                            tvNick.setText(data.getRealName());
                            name = data.getRealName();
                        }
                    } else {
                        Toast.makeText(MeActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
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
        if (data == null) {
            return;
        }
        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            ArrayList<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            String image = photos.get(0);
            String[] split = image.split("/");
            Uri parse = Uri.parse("file://" + image);
            File file = new File(getCacheDir(), "cropped" + split[split.length - 1]);
            Uri destination = Uri.fromFile(file);
            Crop.of(parse, destination).asSquare().start(MeActivity.this);
        } else {
            Uri output = Crop.getOutput(data);
            String url = getAbsoluteImagePath(this, output);

            updateImage(url);
            Glide.with(MeActivity.this)
                    .load(url)
                    .override(200, 200)
                    .bitmapTransform(new GlideCircleTransform(MeActivity.this))
                    .crossFade(1000)
                    .into(ivHeader);
        }
    }

    public String getAbsoluteImagePath(Activity activity, Uri contentUri) {

        //如果是对媒体文件，在android开机的时候回去扫描，然后把路径添加到数据库中。
        //由打印的contentUri可以看到：2种结构。正常的是：content://那么这种就要去数据库读取path。
        //另外一种是Uri是 file:///那么这种是 Uri.fromFile(File file);得到的
        System.out.println(contentUri);

        String[] projection = {MediaStore.Images.Media.DATA};
        String urlpath;
        CursorLoader loader = new CursorLoader(activity, contentUri, projection, null, null, null);
        Cursor cursor = loader.loadInBackground();
        try {
            int column_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            urlpath = cursor.getString(column_index);
            //如果是正常的查询到数据库。然后返回结构
            return urlpath;
        } catch (Exception e) {

            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        //如果是文件。Uri.fromFile(File file)生成的uri。那么下面这个方法可以得到结果
        urlpath = contentUri.getPath();
        return urlpath;
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
                if (response.body() != null) {
                    if (response.body().getCode() == 0) {
                        UpdateImageResponse.DataBean data = response.body().getData();
                        if (data != null) {
                            imageUrl = data.getUrl();
                            Toast.makeText(MeActivity.this, "上传图片成功", Toast.LENGTH_SHORT).show();

                            updateUserInfo(imageUrl, name);
                        }
                    } else {
                        Toast.makeText(MeActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
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

    private void updateUserInfo(String profile, String realName) {
        if (!Utils.isNetworkOn()) {
            Toast.makeText(this, "网络异常，请检查您的网络！", Toast.LENGTH_SHORT).show();
            return;
        }
        Call<EmptyResponse> call = NetModel.getInstance().updateUserInfo(SettingUtils.instance().getToken(), profile, realName);
        call.enqueue(new Callback<EmptyResponse>() {
            @Override
            public void onResponse(Call<EmptyResponse> call, Response<EmptyResponse> response) {
                if (response.body() != null) {
                    if (response.body().getCode() == 0) {
                        Toast.makeText(MeActivity.this, "更新用户信息成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MeActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<EmptyResponse> call, Throwable t) {
                Toast.makeText(MeActivity.this, "更新用户信息失败", Toast.LENGTH_SHORT).show();
            }
        });
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

package cn.com.shijizl.customerfiling.me;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import cn.com.shijizl.customerfiling.R;
import cn.com.shijizl.customerfiling.base.BaseActivity;
import cn.com.shijizl.customerfiling.net.NetModel;
import cn.com.shijizl.customerfiling.net.model.EmptyResponse;
import cn.com.shijizl.customerfiling.utils.SettingUtils;
import cn.com.shijizl.customerfiling.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateNickActivity extends BaseActivity {
    private String imageUrl;
    private String name;

    public static void start(Context context, String imageUrl, String name) {
        Intent intent = new Intent(context, UpdateNickActivity.class);
        intent.putExtra("imageUrl", imageUrl);
        intent.putExtra("name", name);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_nick);

        imageUrl = getIntent().getStringExtra("imageUrl");
        name = getIntent().getStringExtra("name");
        initView();
    }

    private void initView() {
        ImageView ivBack = (ImageView) findViewById(R.id.iv_back_update_nick);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final EditText nick = (EditText) findViewById(R.id.et_update_nick);
        nick.setText(name);

        Button take = (Button) findViewById(R.id.bt_next_update_nick);
        take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trim = nick.getText().toString().trim();
                if (TextUtils.isEmpty(trim)) {
                    Toast.makeText(UpdateNickActivity.this, "姓名不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    updateUserInfo(imageUrl, trim);
                }
            }
        });

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
                if (response.body().getCode() == 0) {
                    Toast.makeText(UpdateNickActivity.this, "更新用户信息成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(UpdateNickActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EmptyResponse> call, Throwable t) {
                Toast.makeText(UpdateNickActivity.this, "更新用户信息失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

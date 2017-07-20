package cn.com.shijizl.customerfiling.me;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import cn.com.shijizl.customerfiling.R;
import cn.com.shijizl.customerfiling.base.BaseActivity;
import cn.com.shijizl.customerfiling.net.NetModel;
import cn.com.shijizl.customerfiling.net.model.UserInfoResponse;
import cn.com.shijizl.customerfiling.utils.GlideCircleTransform;
import cn.com.shijizl.customerfiling.utils.SettingUtils;
import cn.com.shijizl.customerfiling.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeActivity extends BaseActivity {
    private ImageView ivHeader;
    private TextView tvNick;
    private TextView tvName;

    public static void start(Context context) {
        Intent intent = new Intent(context, MeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);

        initView();
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
        tvNick = (TextView) findViewById(R.id.tv_nick_me);
        tvName = (TextView) findViewById(R.id.tv_name_me);
        ImageView ivChange = (ImageView) findViewById(R.id.iv_password_me);
        ivChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdatePasswordActivity.start(MeActivity.this);
            }
        });

        Button btExit = (Button) findViewById(R.id.bt_next_me);
        btExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingUtils.instance().clear();
                LoginActivity.start(MeActivity.this);
                finish();
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
                if (response.code() == 200) {
                    UserInfoResponse.DataBean data = response.body().getData();
                    if (data != null) {
                        Glide.with(MeActivity.this).load(data.getProfile()).bitmapTransform(new GlideCircleTransform(MeActivity.this)).crossFade(1000).placeholder(R.drawable.place_holder).into(ivHeader);
                        tvNick.setText(data.getRealName());
                        tvName.setText(data.getUserName());
                    }
                } else {
                    Toast.makeText(MeActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserInfoResponse> call, Throwable t) {
                Toast.makeText(MeActivity.this, "获取用户信息失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

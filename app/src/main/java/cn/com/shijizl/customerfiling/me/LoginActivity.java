package cn.com.shijizl.customerfiling.me;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.com.shijizl.customerfiling.R;
import cn.com.shijizl.customerfiling.base.App;
import cn.com.shijizl.customerfiling.base.BaseActivity;
import cn.com.shijizl.customerfiling.net.NetModel;
import cn.com.shijizl.customerfiling.net.model.RegisterResponse;
import cn.com.shijizl.customerfiling.utils.SettingUtils;
import cn.com.shijizl.customerfiling.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity{

    public static void start(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
    }

    private void initView() {
        ImageView ivBack = (ImageView) findViewById(R.id.iv_back_login);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.getInstance().exit();
            }
        });

        final EditText etPhone = (EditText) findViewById(R.id.et_phone_login);
        final EditText etPassword = (EditText) findViewById(R.id.et_password_login);

        Button btNext = (Button) findViewById(R.id.bt_next_login);
        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = etPhone.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (checkPhone(phone, password)) {
                    login(phone, password);
                }
            }
        });

        TextView tvRegister = (TextView) findViewById(R.id.tv_register_login);
        TextView tvForger = (TextView) findViewById(R.id.tv_forget_password);
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.start(LoginActivity.this);
            }
        });
        tvForger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgetPasswordActivity.start(LoginActivity.this);
            }
        });

    }

    private void login(String phone, String password) {
        if (!Utils.isNetworkOn()) {
            Toast.makeText(this, "网络异常，请检查您的网络！", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<RegisterResponse> login = NetModel.getInstance().login(phone, password);
        login.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.code() == 200) {
                    RegisterResponse.DataBean data = response.body().getData();
                    if (data != null) {
                        String accessToken = data.getAccessToken();
                        int userId = data.getUserId();
                        SettingUtils.instance().saveToken(accessToken);
                        SettingUtils.instance().saveUserId(userId);

                        finish();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "登录失败，请稍后重试！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkPhone(String phone, String password) {
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return false;
        } else if (phone.length() < 11) {
            Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }  else {
            return true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            App.getInstance().exit();
        }
        return false;
    }
}

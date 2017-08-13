package cn.com.shijizl.customerfiling.me;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.com.shijizl.customerfiling.R;
import cn.com.shijizl.customerfiling.base.BaseActivity;
import cn.com.shijizl.customerfiling.net.NetModel;
import cn.com.shijizl.customerfiling.net.model.CodeResponse;
import cn.com.shijizl.customerfiling.net.model.RegisterResponse;
import cn.com.shijizl.customerfiling.utils.SettingUtils;
import cn.com.shijizl.customerfiling.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhoneVerificationActivity extends BaseActivity {
    private TextView tvCode;
    private Button btGetCode;

    private String phone;
    private String password;
    private int codeNum;

    public static void start(Context context, String phone, String password) {
        Intent intent = new Intent(context, PhoneVerificationActivity.class);
        intent.putExtra("phone", phone);
        intent.putExtra("password", password);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);

        initData();
        initView();
    }

    private void initView() {
        ImageView ivBack = (ImageView) findViewById(R.id.iv_back_phone_verification);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final EditText etCode = (EditText) findViewById(R.id.et_code_phone_verification);
        tvCode = (TextView) findViewById(R.id.tv_code_phone_verification);

        btGetCode = (Button) findViewById(R.id.bt_code_phone_verification);
        btGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCode();
                btGetCode.setEnabled(false);
            }
        });

        Button button = (Button) findViewById(R.id.bt_take_phone_verification);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trim = etCode.getText().toString().trim();
                if (TextUtils.isEmpty(trim)) {
                    Toast.makeText(PhoneVerificationActivity.this, "请输入正确的验证码", Toast.LENGTH_SHORT).show();
                } else if (trim.equals(String.valueOf(codeNum))){
                    register();
                } else {
                    Toast.makeText(PhoneVerificationActivity.this, "请输入正确的验证码", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initData() {
        phone = getIntent().getStringExtra("phone");
        password = getIntent().getStringExtra("password");
    }

    private void getCode() {
        if (!Utils.isNetworkOn()) {
            Toast.makeText(this, "网络异常，请检查您的网络！", Toast.LENGTH_SHORT).show();
            return;
        }
        final Call<CodeResponse> code = NetModel.getInstance().getCode(phone);
        code.enqueue(new Callback<CodeResponse>() {
            @Override
            public void onResponse(Call<CodeResponse> call, Response<CodeResponse> response) {
                if (response.body() != null) {
                    if (response.body().getCode() == 0) {
                        CodeResponse body = response.body();
                        CodeResponse.DataBean data = body.getData();
                        if (data != null) {
                            startTime();
                            codeNum = data.getVerifyCode();
                            tvCode.setVisibility(View.VISIBLE);
                            tvCode.setText("短信验证码已发送至：" + phone);
                        }
                    } else {
                        Toast.makeText(PhoneVerificationActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<CodeResponse> call, Throwable t) {
                Toast.makeText(PhoneVerificationActivity.this, "获取验证码错误，请稍后重试！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void register() {
        if (!Utils.isNetworkOn()) {
            Toast.makeText(this, "网络异常，请检查您的网络！", Toast.LENGTH_SHORT).show();
            return;
        }
        final Call<RegisterResponse> register = NetModel.getInstance().register(phone, password, codeNum);
        register.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.body() != null) {
                    if (response.body().getCode() == 0) {
                        RegisterResponse body = response.body();
                        if (body != null) {
                            String accessToken = body.getData().getAccessToken();
                            int userId = body.getData().getUserId();
                            SettingUtils.instance().saveToken(accessToken);
                            SettingUtils.instance().saveUserId(userId);
                            Toast.makeText(PhoneVerificationActivity.this, "注册成功，请登录", Toast.LENGTH_SHORT).show();
                            LoginActivity.start(PhoneVerificationActivity.this);
                        }
                    } else {
                        Toast.makeText(PhoneVerificationActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(PhoneVerificationActivity.this, "提交失败，请稍后重试！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int seconds = 60;
    private Thread mThread;
    private void startTime() {
        if (mThread == null) {
            mThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (seconds > 0) {
                            synchronized (this) {
                                wait(1000);
                            }
                            Message msg = Message.obtain();
                            msg.what = 1010;
                            msg.arg1 = seconds;
                            handler.sendMessage(msg);
                            seconds--;
                        }
                        Message msg = Message.obtain();
                        msg.what = 1011;
                        handler.sendMessage(msg);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            mThread.start();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1010:
                    btGetCode.setText("重新获取(" + seconds + ")");
                    break;
                case 1011:
                    seconds = 60;
                    btGetCode.setEnabled(true);
                    btGetCode.setText("重新获取");
                    break;
                default:
                    break;
            }
        }
    };
}

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
import android.widget.Toast;

import cn.com.shijizl.customerfiling.R;
import cn.com.shijizl.customerfiling.base.BaseActivity;
import cn.com.shijizl.customerfiling.net.NetModel;
import cn.com.shijizl.customerfiling.net.model.CodeResponse;
import cn.com.shijizl.customerfiling.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPasswordActivity extends BaseActivity {
    private Button btGetCode;

    private int codeNum;

    public static void start(Context context) {
        Intent intent = new Intent(context, ForgetPasswordActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        initView();
    }

    private void initView() {
        ImageView ivBack = (ImageView) findViewById(R.id.iv_back_forget);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final EditText etPhone = (EditText) findViewById(R.id.et_phone_forget);
        final EditText etCode = (EditText) findViewById(R.id.et_code_forget);

        Button btNext = (Button) findViewById(R.id.bt_next_forget);
        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = etPhone.getText().toString().trim();
                String code = etCode.getText().toString().trim();

                if (checkPhone(phone)) {
                    if (TextUtils.isEmpty(code)) {
                        Toast.makeText(ForgetPasswordActivity.this, "请输入正确的验证码", Toast.LENGTH_SHORT).show();
                    } else if (code.equals(String.valueOf(codeNum))){
                        ForgetPasswordNextActivity.start(ForgetPasswordActivity.this, phone);
                    } else {
                        Toast.makeText(ForgetPasswordActivity.this, "请输入正确的验证码", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btGetCode = (Button) findViewById(R.id.bt_code_phone_forget);
        btGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = etPhone.getText().toString().trim();
                if (checkPhone(phone)) {
                    getCode(phone);
                    btGetCode.setEnabled(false);
                }
            }
        });
    }

    private boolean checkPhone(String phone) {
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return false;
        } else if (phone.length() < 11) {
            Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return false;
        }  else {
            return true;
        }
    }

    private void getCode(String phone) {
        if (!Utils.isNetworkOn()) {
            Toast.makeText(this, "网络异常，请检查您的网络！", Toast.LENGTH_SHORT).show();
            return;
        }
        final Call<CodeResponse> code = NetModel.getInstance().getCodeForget(phone);
        code.enqueue(new Callback<CodeResponse>() {
            @Override
            public void onResponse(Call<CodeResponse> call, Response<CodeResponse> response) {
                if (response.body().getCode() == 0) {
                    CodeResponse body = response.body();
                    CodeResponse.DataBean data = body.getData();
                    if (data != null) {
                        startTime();
                        codeNum = data.getVerifyCode();
                    }
                } else {
                    Toast.makeText(ForgetPasswordActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CodeResponse> call, Throwable t) {
                Toast.makeText(ForgetPasswordActivity.this, "获取验证码错误，请稍后重试！", Toast.LENGTH_SHORT).show();
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
                    btGetCode.setText("" + seconds + " s");
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

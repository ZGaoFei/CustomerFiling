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
import cn.com.shijizl.customerfiling.order.OrderDetailsActivity;
import cn.com.shijizl.customerfiling.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPasswordNextActivity extends BaseActivity {
    private String phone;

    public static void start(Context context, String phone) {
        Intent intent = new Intent(context, ForgetPasswordNextActivity.class);
        intent.putExtra("phone", phone);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password_next);

        phone = getIntent().getStringExtra("phone");
        initView();
    }

    private void initView() {
        ImageView ivBack = (ImageView) findViewById(R.id.iv_back_forget_next);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final EditText etPasswordOne = (EditText) findViewById(R.id.et_password_one_forget_next);
        final EditText etPasswordTwo = (EditText) findViewById(R.id.et_password_two_forget_next);

        Button btTake = (Button) findViewById(R.id.bt_next_forget_next);
        btTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passwordOne = etPasswordOne.getText().toString().trim();
                String passwordTwo = etPasswordTwo.getText().toString().trim();

                if (TextUtils.isEmpty(passwordOne) || TextUtils.isEmpty(passwordTwo)) {
                    Toast.makeText(ForgetPasswordNextActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else if (passwordOne.length() < 6 || passwordTwo.length() < 6) {
                    Toast.makeText(ForgetPasswordNextActivity.this, "密码的长度最小为6", Toast.LENGTH_SHORT).show();
                } else if (passwordOne.equals(passwordTwo)) {
                    take(phone, passwordOne);
                } else {
                    Toast.makeText(ForgetPasswordNextActivity.this, "两次密码不一致，请重新输入", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void take(String phone, String password) {
        if (!Utils.isNetworkOn()) {
            Toast.makeText(this, "网络异常，请检查您的网络！", Toast.LENGTH_SHORT).show();
            return;
        }
        Call<EmptyResponse> call = NetModel.getInstance().resetPassWord(phone, password);
        call.enqueue(new Callback<EmptyResponse>() {
            @Override
            public void onResponse(Call<EmptyResponse> call, Response<EmptyResponse> response) {
                if (response.code() == 200) {
                    Toast.makeText(ForgetPasswordNextActivity.this, "修改成功，请重新登录", Toast.LENGTH_SHORT).show();
                    LoginActivity.start(ForgetPasswordNextActivity.this);
                } else {
                    Toast.makeText(ForgetPasswordNextActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EmptyResponse> call, Throwable t) {
                Toast.makeText(ForgetPasswordNextActivity.this, "提交失败，请重试", Toast.LENGTH_SHORT).show();
            }
        });

    }
}

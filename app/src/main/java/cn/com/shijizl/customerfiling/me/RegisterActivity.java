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

public class RegisterActivity extends BaseActivity {

    public static void start(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
    }

    private void initView() {
        ImageView ivBack = (ImageView) findViewById(R.id.iv_back_register);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final EditText etPhone = (EditText) findViewById(R.id.et_phone_register);
        final EditText etPassword = (EditText) findViewById(R.id.et_password_register);

        Button btNext = (Button) findViewById(R.id.bt_next_register);
        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = etPhone.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (checkPhone(phone, password)) {
                    PhoneVerificationActivity.start(RegisterActivity.this, phone, password);
                }
            }
        });
    }

    private boolean checkPhone(String phone, String password) {
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return false;
        } else if (phone.length() < 11) {
            Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.length() < 6) {
            Toast.makeText(this, "密码长度不能小于6", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}

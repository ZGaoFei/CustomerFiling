package cn.com.shijizl.customerfiling.me;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import cn.com.shijizl.customerfiling.R;
import cn.com.shijizl.customerfiling.base.App;
import cn.com.shijizl.customerfiling.net.NetModel;
import cn.com.shijizl.customerfiling.net.model.EmptyResponse;
import cn.com.shijizl.customerfiling.utils.SettingUtils;
import cn.com.shijizl.customerfiling.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdatePasswordActivity extends AppCompatActivity {

    public static void start(Context context) {
        Intent intent = new Intent(context, UpdatePasswordActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        initView();
    }

    private void initView() {
        ImageView ivBack = (ImageView) findViewById(R.id.iv_back_update_pass);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.getInstance().exit();
            }
        });

        final EditText etOld = (EditText) findViewById(R.id.et_old_pass_word);
        final EditText etNew = (EditText) findViewById(R.id.et_new_pass_word);
        final EditText etNew2 = (EditText) findViewById(R.id.et_new_pass_word_too);
        Button button = (Button) findViewById(R.id.bt_next_update_password);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPass = etOld.getText().toString().trim();
                String newPass = etNew.getText().toString().trim();
                String newPass2 = etNew2.getText().toString().trim();

                if (oldPass.equals(newPass)) {
                    Toast.makeText(UpdatePasswordActivity.this, "密码重复", Toast.LENGTH_SHORT).show();
                } else if (newPass.equals(newPass2)) {
                    updatePassWord(oldPass, newPass);
                } else {
                    Toast.makeText(UpdatePasswordActivity.this, "新密码不一致，请重新输入", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updatePassWord(String oldPassWord, String newPassWord) {
        if (!Utils.isNetworkOn()) {
            Toast.makeText(this, "网络异常，请检查您的网络！", Toast.LENGTH_SHORT).show();
            return;
        }
        Call<EmptyResponse> call = NetModel.getInstance().updatePassWord(SettingUtils.instance().getToken(), oldPassWord, newPassWord);
        call.enqueue(new Callback<EmptyResponse>() {
            @Override
            public void onResponse(Call<EmptyResponse> call, Response<EmptyResponse> response) {
                if (response.code() == 200) {
                    Toast.makeText(UpdatePasswordActivity.this, "密码修改成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(UpdatePasswordActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EmptyResponse> call, Throwable t) {
                Toast.makeText(UpdatePasswordActivity.this, "密码修改失败，请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

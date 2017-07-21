package cn.com.shijizl.customerfiling.order;

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
import cn.com.shijizl.customerfiling.base.App;
import cn.com.shijizl.customerfiling.base.BaseActivity;
import cn.com.shijizl.customerfiling.net.NetModel;
import cn.com.shijizl.customerfiling.net.model.EmptyResponse;
import cn.com.shijizl.customerfiling.utils.SettingUtils;
import cn.com.shijizl.customerfiling.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCustomerActivity extends BaseActivity {
    private String projectId;

    public static void start(Context context, String projectId) {
        Intent intent = new Intent(context, AddCustomerActivity.class);
        intent.putExtra("projectId", projectId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        initView();
        initData();
    }

    private void initView() {
        ImageView ivBack = (ImageView) findViewById(R.id.iv_back_add_customer);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.getInstance().exit();
            }
        });

        final EditText etName = (EditText) findViewById(R.id.et_name_add_customer);
        final EditText etPhone = (EditText) findViewById(R.id.et_phone_add_customer);
        final EditText etAddress = (EditText) findViewById(R.id.et_address_add_customer);
        Button button = (Button) findViewById(R.id.bt_taker_add_customer);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();
                String address = etAddress.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(AddCustomerActivity.this, "姓名不能为空", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(AddCustomerActivity.this, "电话不能为空", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(address)) {
                    Toast.makeText(AddCustomerActivity.this, "地址不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    addCustomerInfo(projectId, "", name, phone, address);
                }
            }
        });
    }

    private void initData() {
        projectId = getIntent().getStringExtra("projectId");
    }

    private void addCustomerInfo(String projectId, String customerId, String custName, String phoneNum, String address) {
        if (!Utils.isNetworkOn()) {
            Toast.makeText(this, "网络异常，请检查您的网络！", Toast.LENGTH_SHORT).show();
            return;
        }
        Call<EmptyResponse> call = NetModel.getInstance().addOrUpadteCustomerInfo(SettingUtils.instance().getToken(), projectId, customerId, custName, phoneNum, address);
        call.enqueue(new Callback<EmptyResponse>() {
            @Override
            public void onResponse(Call<EmptyResponse> call, Response<EmptyResponse> response) {
                if (response.body().getCode() == 0) {
                    Toast.makeText(AddCustomerActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddCustomerActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EmptyResponse> call, Throwable t) {
                Toast.makeText(AddCustomerActivity.this, "提交失败，请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

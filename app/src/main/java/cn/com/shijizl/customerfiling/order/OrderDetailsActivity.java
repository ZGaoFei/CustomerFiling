package cn.com.shijizl.customerfiling.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.com.shijizl.customerfiling.R;
import cn.com.shijizl.customerfiling.base.BaseActivity;
import cn.com.shijizl.customerfiling.net.NetModel;
import cn.com.shijizl.customerfiling.net.model.CustomerResponse;
import cn.com.shijizl.customerfiling.net.model.EmptyResponse;
import cn.com.shijizl.customerfiling.net.model.ProjectDetailsResponse;
import cn.com.shijizl.customerfiling.utils.SettingUtils;
import cn.com.shijizl.customerfiling.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailsActivity extends BaseActivity {
    private ImageView ivUpdate;
    private TextView tvTitle;
    private ImageView ivCad;
    private ImageView ivInfo;
    private ImageView ivTable;
    private LinearLayout llBox;
    private TextView tvName, tvPhone, tvAddress, tvTime;
    private LinearLayout llTime;
    private Button btTake;

    private String projectId;
    private String projectTitle;
    private int customerId = 0;
    private int states;

    public static void start(Context context, String projectId) {
        Intent intent = new Intent(context, OrderDetailsActivity.class);
        intent.putExtra("projectId", projectId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initView() {
        ImageView ivBack = (ImageView) findViewById(R.id.iv_back_order_details);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ivUpdate = (ImageView) findViewById(R.id.iv_update_order_details);
        ivUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateOrderActivity.start(OrderDetailsActivity.this, projectId, customerId);
            }
        });

        tvTitle = (TextView) findViewById(R.id.tv_title_order_details);
        ivCad = (ImageView) findViewById(R.id.iv_image_cad_order_details);
        ivInfo = (ImageView) findViewById(R.id.iv_image_info_order_details);
        ivTable = (ImageView) findViewById(R.id.iv_image_table_order_details);

        llBox = (LinearLayout) findViewById(R.id.ll_box_order_details);
        tvName = (TextView) findViewById(R.id.tv_name_order_details);
        tvPhone = (TextView) findViewById(R.id.tv_phone_order_details);
        tvAddress = (TextView) findViewById(R.id.tv_address_order_details);
        tvTime = (TextView) findViewById(R.id.tv_time_order_details);
        llTime = (LinearLayout) findViewById(R.id.ll_time_order_details);

        btTake = (Button) findViewById(R.id.bt_taker_order_details);
        btTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customerId > 0) {// 开始施工
                    if (states == 0) {
                        startProject(projectId);
                    } else {
                        ProjectSpeedActivity.start(OrderDetailsActivity.this, projectId, projectTitle);
                    }
                } else {// 添加客户资料
                    AddCustomerActivity.start(OrderDetailsActivity.this, projectId);
                }
            }
        });
    }

    private void initData() {
        projectId = getIntent().getStringExtra("projectId");
        if (!TextUtils.isEmpty(projectId)) {
            getProjectDetail(projectId);
        }
    }

    private void getProjectDetail(String projectId) {
        if (!Utils.isNetworkOn()) {
            Toast.makeText(this, "网络异常，请检查您的网络！", Toast.LENGTH_SHORT).show();
            return;
        }
        Call<ProjectDetailsResponse> call = NetModel.getInstance().getProjectDetail(SettingUtils.instance().getToken(), projectId);
        call.enqueue(new Callback<ProjectDetailsResponse>() {
            @Override
            public void onResponse(Call<ProjectDetailsResponse> call, Response<ProjectDetailsResponse> response) {
                if (response.body().getCode() == 0) {
                    ProjectDetailsResponse.DataBean data = response.body().getData();
                    if (data != null) {
                        setTop(data);
                    }
                } else {
                    Toast.makeText(OrderDetailsActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProjectDetailsResponse> call, Throwable t) {
                Toast.makeText(OrderDetailsActivity.this, "网络异常，请检查您的网络！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setTop(ProjectDetailsResponse.DataBean data) {
        projectTitle = data.getTitle();
        tvTitle.setText(data.getTitle());
        List<ProjectDetailsResponse.DataBean.CadImgListBean> cadImgs = data.getCadImgList();
        if (cadImgs != null && !cadImgs.isEmpty()) {
            String cadUrl = cadImgs.get(0).getImgUrl();
            if (!TextUtils.isEmpty(cadUrl)) {
                Glide.with(this)
                        .load(cadUrl)
                        .override(400, 400)
                        .fitCenter()
                        .placeholder(R.drawable.place_holder)
                        .into(ivCad);
            }
        }else {
            ivCad.setImageResource(R.drawable.place_holder);
        }
        List<ProjectDetailsResponse.DataBean.StateImgListBean> stateImages = data.getStateImgList();
        if (stateImages != null && !stateImages.isEmpty()) {
            String stateUrl = stateImages.get(0).getImgUrl();
            if (!TextUtils.isEmpty(stateUrl)) {
                Glide.with(this).
                        load(stateUrl)
                        .override(400, 400)
                        .fitCenter()
                        .placeholder(R.drawable.place_holder)
                        .into(ivTable);
            }
        } else {
            ivInfo.setImageResource(R.drawable.place_holder);
        }
        List<ProjectDetailsResponse.DataBean.BudgetImgListBean> budgetImgs = data.getBudgetImgList();
        if (budgetImgs != null && !budgetImgs.isEmpty()) {
            String budgetUrl = budgetImgs.get(0).getImgUrl();
            if (!TextUtils.isEmpty(budgetUrl)) {
                Glide.with(this)
                        .load(budgetUrl)
                        .override(400, 400)
                        .fitCenter()
                        .placeholder(R.drawable.place_holder)
                        .into(ivInfo);
            }
        }else {
            ivTable.setImageResource(R.drawable.place_holder);
        }

        int state = data.getStatus();
        states = state;
        if (data.getCustomerId() > 0) {
            llBox.setVisibility(View.VISIBLE);
            customerId = data.getCustomerId();
            getCustomerInfo(String.valueOf(data.getCustomerId()));
            if (state == 0) {
                btTake.setText("开始施工");
            } else {
                btTake.setText("查看进度");
            }
        } else {
            customerId = 0;
            llBox.setVisibility(View.GONE);
            btTake.setText("添加客户资料");
        }
        if (state == 0) {
            tvTime.setVisibility(View.GONE);
        } else {
            llTime.setVisibility(View.VISIBLE);
            tvTime.setText(Utils.paseTime(data.getStartTime()));
        }
    }

    private void getCustomerInfo(String customerId) {
        if (!Utils.isNetworkOn()) {
            Toast.makeText(this, "网络异常，请检查您的网络！", Toast.LENGTH_SHORT).show();
            return;
        }
        Call<CustomerResponse> call = NetModel.getInstance().getCustomerInfo(SettingUtils.instance().getToken(), customerId);
        call.enqueue(new Callback<CustomerResponse>() {
            @Override
            public void onResponse(Call<CustomerResponse> call, Response<CustomerResponse> response) {
                if (response.body().getCode() == 0) {
                    CustomerResponse.DataBean data = response.body().getData();
                    if (data != null) {
                        setBottom(data);
                    }
                } else {
                    Toast.makeText(OrderDetailsActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CustomerResponse> call, Throwable t) {
                Toast.makeText(OrderDetailsActivity.this, "网络异常，请检查您的网络！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setBottom(CustomerResponse.DataBean data) {
        tvName.setText(data.getCustName());
        tvPhone.setText(data.getPhoneNum());
        tvAddress.setText(data.getAddress());
    }

    private void startProject(String projectId) {
        if (!Utils.isNetworkOn()) {
            Toast.makeText(this, "网络异常，请检查您的网络！", Toast.LENGTH_SHORT).show();
            return;
        }
        Call<EmptyResponse> call = NetModel.getInstance().startProject(SettingUtils.instance().getToken(), projectId);
        call.enqueue(new Callback<EmptyResponse>() {
            @Override
            public void onResponse(Call<EmptyResponse> call, Response<EmptyResponse> response) {
                if (response.body().getCode() == 0) {
                    ivUpdate.setVisibility(View.GONE);
                    llTime.setVisibility(View.VISIBLE);
                    tvTime.setVisibility(View.VISIBLE);
                    tvTime.setText(Utils.paseTime(System.currentTimeMillis()));
                    btTake.setText("查看进度");
                    states = 1;
                } else {
                    Toast.makeText(OrderDetailsActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EmptyResponse> call, Throwable t) {
                Toast.makeText(OrderDetailsActivity.this, "网络异常，请检查您的网络！", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

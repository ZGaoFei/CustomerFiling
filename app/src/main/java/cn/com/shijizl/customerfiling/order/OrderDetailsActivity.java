package cn.com.shijizl.customerfiling.order;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.com.shijizl.customerfiling.R;
import cn.com.shijizl.customerfiling.base.BaseActivity;
import cn.com.shijizl.customerfiling.net.NetModel;
import cn.com.shijizl.customerfiling.net.model.CustomerResponse;
import cn.com.shijizl.customerfiling.net.model.EmptyResponse;
import cn.com.shijizl.customerfiling.net.model.ProjectDetailsResponse;
import cn.com.shijizl.customerfiling.net.model.ProjectDetailsResponse.DataBean;
import cn.com.shijizl.customerfiling.order.adapter.ImageAdapter;
import cn.com.shijizl.customerfiling.utils.SettingUtils;
import cn.com.shijizl.customerfiling.utils.Utils;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailsActivity extends BaseActivity {
    private ImageView ivUpdate;
    private TextView tvTitle;
    private LinearLayout llBox;
    private TextView tvName, tvPhone, tvAddress, tvTime;
    private LinearLayout llTime;
    private Button btTake;
    private RecyclerView rvCad, rvInfo, rvTable;

    private String projectId;
    private String projectTitle;
    private int customerId = 0;
    private int states;
    private String name, phone, address;

    public static void start(Context context, String projectId) {
        Intent intent = new Intent(context, OrderDetailsActivity.class);
        intent.putExtra("projectId", projectId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        projectId = getIntent().getStringExtra("projectId");
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
                showSettingDialog();
            }
        });

        tvTitle = (TextView) findViewById(R.id.tv_title_order_details);
        rvCad = (RecyclerView) findViewById(R.id.rv_image_cad_order_details);
        rvInfo = (RecyclerView) findViewById(R.id.rv_image_info_order_details);
        rvTable = (RecyclerView) findViewById(R.id.rv_image_table_order_details);

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

    private void setTop(DataBean data) {
        projectTitle = data.getTitle();
        tvTitle.setText(data.getTitle());
        ArrayList<String> listCad = getList(data, 0);
        setAdapter(rvCad, listCad);
        ArrayList<String> listInfo = getList(data, 1);
        setAdapter(rvInfo, listInfo);
        ArrayList<String> listTable = getList(data, 2);
        setAdapter(rvTable, listTable);

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
        name = data.getCustName();
        phone = data.getPhoneNum();
        address = data.getAddress();
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

    private ArrayList<String> getList(DataBean data, int type) {
        ArrayList<String> strList = new ArrayList<>();
        if (data != null) {
            switch (type) {
                case 0:
                    List<DataBean.CadImgListBean> cadImgs = data.getCadImgList();
                    if (cadImgs != null && !cadImgs.isEmpty()) {
                        for (int i = 0; i < cadImgs.size(); i++) {
                            strList.add(cadImgs.get(i).getImgUrl());
                        }
                    }else {

                    }
                    break;
                case 1:
                    List<DataBean.BudgetImgListBean> budgetImgs = data.getBudgetImgList();
                    if (budgetImgs != null && !budgetImgs.isEmpty()) {
                        for (int i = 0; i < budgetImgs.size(); i++) {
                            strList.add(budgetImgs.get(i).getImgUrl());
                        }
                    }else {

                    }
                    break;
                case 2:
                    List<DataBean.StateImgListBean> stateImages = data.getStateImgList();
                    if (stateImages != null && !stateImages.isEmpty()) {
                        for (int i = 0; i < stateImages.size(); i++) {
                            strList.add(stateImages.get(i).getImgUrl());
                        }
                    } else {

                    }
                    break;
            }
        }
        return strList;
    }

    private void setAdapter(RecyclerView recyclerView, final ArrayList<String> list) {
        ImageAdapter adapter = new ImageAdapter(this, list);

        GridLayoutManager manager = new GridLayoutManager(this, 4);
        manager.setSmoothScrollbarEnabled(true);
        manager.setAutoMeasureEnabled(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(manager);

        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new ImageAdapter.ItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                PhotoPreview.builder()
                        .setPhotos(list)
                        .setCurrentItem(position)
                        .setShowDeleteButton(false)
                        .start(OrderDetailsActivity.this, PhotoPicker.REQUEST_CODE);
            }
        });
    }

    private void showSettingDialog() {
        final Dialog dialog = new AlertDialog.Builder(this).create();
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setBackgroundDrawable(new BitmapDrawable());
        dialog.show();
        window.setContentView(R.layout.dialog_me_setting);
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView btnSet = (TextView) window.findViewById(R.id.tv_me_setting_set);
        TextView btnShare = (TextView) window.findViewById(R.id.tv_me_setting_order);
        TextView btnCancel = (TextView) window.findViewById(R.id.tv_me_setting_cancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddOrderActivity.start(OrderDetailsActivity.this, projectId, projectTitle, String.valueOf(customerId), name, phone, address);
                dialog.dismiss();
            }
        });
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddCustomerActivity.start(OrderDetailsActivity.this, projectId, name, phone, address);
                dialog.dismiss();
            }
        });
    }
}

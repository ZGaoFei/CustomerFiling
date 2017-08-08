package cn.com.shijizl.customerfiling.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import cn.com.shijizl.customerfiling.R;
import cn.com.shijizl.customerfiling.base.BaseActivity;
import cn.com.shijizl.customerfiling.home.adapter.LoadMoreAdapter;
import cn.com.shijizl.customerfiling.me.LoginActivity;
import cn.com.shijizl.customerfiling.me.MeActivity;
import cn.com.shijizl.customerfiling.net.NetModel;
import cn.com.shijizl.customerfiling.net.model.EmptyResponse;
import cn.com.shijizl.customerfiling.net.model.ProjectListResponse;
import cn.com.shijizl.customerfiling.net.model.UserInfoResponse;
import cn.com.shijizl.customerfiling.order.AddOrderActivity;
import cn.com.shijizl.customerfiling.order.OrderDetailsActivity;
import cn.com.shijizl.customerfiling.utils.GlideCircleTransform;
import cn.com.shijizl.customerfiling.utils.SettingUtils;
import cn.com.shijizl.customerfiling.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {
    private RefreshLayout refreshLayout;
    private List<ProjectListResponse.DataBean> list = new ArrayList<>();
    private LoadMoreAdapter adapter;
    private int size;
    private int num;

    private LinearLayout llEmpty;
    private ImageView ivUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (TextUtils.isEmpty(SettingUtils.instance().getToken())) {
            LoginActivity.start(MainActivity.this);
        } else {
            initData();
            getUserInfo();
        }
    }

    private void initData() {
        num = 0;
        size = 10;
        getProjectList(num, size);
    }

    private void initView() {
        ivUser = (ImageView) findViewById(R.id.iv_main_me);
        TextView tvAdd = (TextView) findViewById(R.id.tv_main_add);
        llEmpty = (LinearLayout) findViewById(R.id.ll_main_empty);

        ivUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeActivity.start(MainActivity.this);
            }
        });

        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddOrderActivity.start(MainActivity.this);
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_load_main);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LoadMoreAdapter(this, list);
        recyclerView.setAdapter(adapter);
        adapter.setOnClickListener(new LoadMoreAdapter.ClickListener() {
            @Override
            public void onClick(int position, View v) {
                OrderDetailsActivity.start(MainActivity.this, String.valueOf(list.get(position).getId()));
            }

            @Override
            public void onLongClick(int position, View v) {
                showDeleteDialog(position);
            }
        });

        refreshLayout = (RefreshLayout)findViewById(R.id.smart_refresh_layout_main);
        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                num = 0;
                getProjectList(num, size);
            }
        });
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                num ++;
                getMoreProjectList(num, size);
            }
        });
    }

    private void getProjectList(int pageNum, int pageSize) {
        if (!Utils.isNetworkOn()) {
            Toast.makeText(this, "网络异常，请检查您的网络！", Toast.LENGTH_SHORT).show();
            return;
        }
        Call<ProjectListResponse> call = NetModel.getInstance().getProjectList(SettingUtils.instance().getToken(), pageNum, pageSize);
        call.enqueue(new Callback<ProjectListResponse>() {
            @Override
            public void onResponse(Call<ProjectListResponse> call, Response<ProjectListResponse> response) {
                if (response.body() != null) {
                    if (response.body().getCode() == 0) {
                        List<ProjectListResponse.DataBean> data = response.body().getData();
                        if (list != null && !list.isEmpty()) {
                            list.clear();
                        }
                        if (data != null && !data.isEmpty()) {
                            list.addAll(data);
                            adapter.updateData(list);
                            refreshLayout.finishRefresh();
                            llEmpty.setVisibility(View.GONE);
                        } else {
                            llEmpty.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(MainActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        llEmpty.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ProjectListResponse> call, Throwable t) {
                refreshLayout.finishRefresh();
                Toast.makeText(MainActivity.this, "网络异常，请检查您的网络！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getMoreProjectList(int pageNum, int pageSize) {
        if (!Utils.isNetworkOn()) {
            Toast.makeText(this, "网络异常，请检查您的网络！", Toast.LENGTH_SHORT).show();
            return;
        }
        Call<ProjectListResponse> call = NetModel.getInstance().getProjectList(SettingUtils.instance().getToken(), pageNum, pageSize);
        call.enqueue(new Callback<ProjectListResponse>() {
            @Override
            public void onResponse(Call<ProjectListResponse> call, Response<ProjectListResponse> response) {
                if (response.body().getCode() == 0) {
                    List<ProjectListResponse.DataBean> data = response.body().getData();
                    if (data != null && !data.isEmpty()) {
                        list.addAll(data);
                        adapter.updateData(list);
                        refreshLayout.finishLoadmore(true);
                    } else {
                        refreshLayout.finishLoadmore(true);
                    }
                } else {
                    Toast.makeText(MainActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProjectListResponse> call, Throwable t) {
                refreshLayout.finishLoadmore(false);
            }
        });
    }

    private void getUserInfo() {
        Call<UserInfoResponse> call = NetModel.getInstance().getUserInfo(SettingUtils.instance().getToken());
        call.enqueue(new Callback<UserInfoResponse>() {
            @Override
            public void onResponse(Call<UserInfoResponse> call, Response<UserInfoResponse> response) {
                if (response.body() != null) {
                    if (response.body().getCode() == 0) {
                        UserInfoResponse.DataBean data = response.body().getData();
                        if (data != null) {
                            Glide.with(MainActivity.this)
                                    .load(data.getProfile())
                                    .override(200, 200)
                                    .bitmapTransform(new GlideCircleTransform(MainActivity.this))
                                    .crossFade(1000)
                                    .into(ivUser);
                        }
                    } else {
                    }
                }
            }

            @Override
            public void onFailure(Call<UserInfoResponse> call, Throwable t) {
            }
        });
    }

    private void deleteProject(final int projectId) {
        if (!Utils.isNetworkOn()) {
            Toast.makeText(this, "网络异常，请检查您的网络！", Toast.LENGTH_SHORT).show();
            return;
        }
        Call<EmptyResponse> call = NetModel.getInstance().deleteProject(SettingUtils.instance().getToken(), String.valueOf(projectId));
        call.enqueue(new Callback<EmptyResponse>() {
            @Override
            public void onResponse(Call<EmptyResponse> call, Response<EmptyResponse> response) {
                if (response.body() != null) {
                    if (response.body().getCode() == 0) {
                        removeProject(projectId);
                        adapter.updateData(list);
                        Toast.makeText(MainActivity.this, "删除工单成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<EmptyResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "删除工单失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("您是否要删除此工单");
        builder.setTitle("提示");
        builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteProject(list.get(position).getId());
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void removeProject(int projectId) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == projectId) {
                list.remove(i);
                break;
            }
        }
    }
}

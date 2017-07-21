package cn.com.shijizl.customerfiling.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.com.shijizl.customerfiling.R;
import cn.com.shijizl.customerfiling.base.BaseActivity;
import cn.com.shijizl.customerfiling.net.NetModel;
import cn.com.shijizl.customerfiling.net.model.EmptyResponse;
import cn.com.shijizl.customerfiling.net.model.ProjectScheduleResponse;
import cn.com.shijizl.customerfiling.order.adapter.ProjectSpeedAdapter;
import cn.com.shijizl.customerfiling.utils.SettingUtils;
import cn.com.shijizl.customerfiling.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProjectSpeedActivity extends BaseActivity {
    private String projectId;
    private String projectTitle;

    private ProjectSpeedAdapter adapter;
    private List<ProjectScheduleResponse.DataBean> list = new ArrayList<>();
    private List<ProjectScheduleResponse.DataBean> listProject = new ArrayList<>();

    public static void start(Context context, String projectId, String projectTitle) {
        Intent intent = new Intent(context, ProjectSpeedActivity.class);
        intent.putExtra("projectId", projectId);
        intent.putExtra("projectTitle", projectTitle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_speed);

        initData();
        initView();

        getSysALLSchedule();
    }

    private void initView() {
        ImageView ivBack = (ImageView) findViewById(R.id.iv_back_project_speed);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView tvTitle = (TextView) findViewById(R.id.tv_title_project_speed);
        tvTitle.setText(projectTitle);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_project_speed);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProjectSpeedAdapter(this, list);
        recyclerView.setAdapter(adapter);
        adapter.setOnRadioButtonClick(new ProjectSpeedAdapter.RadioButtonClick() {
            @Override
            public void onClick(View view, int position) {
                boolean checked = list.get(position).isChecked();
                if (!checked) {
                    list.get(position).setChecked(true);
                    list.get(position).setEnable(false);
                    adapter.updateItem(position);
                    if (position + 1 <= list.size() - 1) {
                        list.get(position + 1).setEnable(true);
                        adapter.updateItem(position + 1);
                    }
                    updateProjectSchedule(projectId, String.valueOf(list.get(position).getScheduleCode()), list.get(position).getStepDesc());
                }
            }
        });
    }

    private void initData() {
        projectId = getIntent().getStringExtra("projectId");
        projectTitle = getIntent().getStringExtra("projectTitle");
    }

    private void updateProjectSchedule(String projectId, String code, String stepDesc) {
        if (!Utils.isNetworkOn()) {
            Toast.makeText(this, "网络异常，请检查您的网络！", Toast.LENGTH_SHORT).show();
            return;
        }
        Call<EmptyResponse> call = NetModel.getInstance().updateProjectSchedule(SettingUtils.instance().getToken(), projectId, code, stepDesc);
        call.enqueue(new Callback<EmptyResponse>() {
            @Override
            public void onResponse(Call<EmptyResponse> call, Response<EmptyResponse> response) {
                if (response.body().getCode() == 0) {
                    Toast.makeText(ProjectSpeedActivity.this, "进度更新成功", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EmptyResponse> call, Throwable t) {

            }
        });
    }

    private void getProjectSchedule(String projectId) {
        if (!Utils.isNetworkOn()) {
            Toast.makeText(this, "网络异常，请检查您的网络！", Toast.LENGTH_SHORT).show();
            return;
        }
        Call<ProjectScheduleResponse> call = NetModel.getInstance().getProjectSchedule(SettingUtils.instance().getToken(), projectId);
        call.enqueue(new Callback<ProjectScheduleResponse>() {
            @Override
            public void onResponse(Call<ProjectScheduleResponse> call, Response<ProjectScheduleResponse> response) {
                if (response.body().getCode() == 0) {
                    List<ProjectScheduleResponse.DataBean> data = response.body().getData();
                    listProject.addAll(data);

                    checkData();
                }
            }

            @Override
            public void onFailure(Call<ProjectScheduleResponse> call, Throwable t) {

            }
        });
    }

    private void getSysALLSchedule() {
        if (!Utils.isNetworkOn()) {
            Toast.makeText(this, "网络异常，请检查您的网络！", Toast.LENGTH_SHORT).show();
            return;
        }
        Call<ProjectScheduleResponse> call = NetModel.getInstance().getSysALLSchedule(SettingUtils.instance().getToken());
        call.enqueue(new Callback<ProjectScheduleResponse>() {
            @Override
            public void onResponse(Call<ProjectScheduleResponse> call, Response<ProjectScheduleResponse> response) {
                if (response.body().getCode() == 0) {
                    getProjectSchedule(projectId);

                    List<ProjectScheduleResponse.DataBean> data = response.body().getData();
                    list.addAll(data);
                }
            }

            @Override
            public void onFailure(Call<ProjectScheduleResponse> call, Throwable t) {

            }
        });
    }

    private void checkData() {
        int result = -1;
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < listProject.size(); j++) {
                if (listProject.get(j).getScheduleCode() == list.get(i).getScheduleCode()) {
                    list.get(i).setChecked(true);
                    result = i;
                    break;
                } else {
                    list.get(i).setChecked(false);
                }
            }
        }
        if (result < 0) {
            list.get(0).setEnable(true);
        } else if (result + 1 <= list.size() - 1) {
            list.get(result + 1).setEnable(true);
        }

        adapter.update(list);
    }
}

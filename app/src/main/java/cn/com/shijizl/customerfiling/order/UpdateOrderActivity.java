package cn.com.shijizl.customerfiling.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.com.shijizl.customerfiling.R;
import cn.com.shijizl.customerfiling.base.BaseActivity;
import cn.com.shijizl.customerfiling.net.NetModel;
import cn.com.shijizl.customerfiling.net.UpdateModel;
import cn.com.shijizl.customerfiling.net.model.CustomerResponse;
import cn.com.shijizl.customerfiling.net.model.EmptyResponse;
import cn.com.shijizl.customerfiling.net.model.ImageResponse;
import cn.com.shijizl.customerfiling.net.model.ProjectDetailsResponse;
import cn.com.shijizl.customerfiling.net.model.UpdateImageResponse;
import cn.com.shijizl.customerfiling.utils.SettingUtils;
import cn.com.shijizl.customerfiling.utils.Utils;
import me.iwf.photopicker.PhotoPicker;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateOrderActivity extends BaseActivity {
    private EditText etTitle;
    private ImageView ivCad;
    private ImageView ivInfo;
    private ImageView ivTable;
    private ImageView ivAddCad, ivAddInfo, ivAddTable;
    private LinearLayout llBox;
    private EditText etName, etPhone, etAddress;
    private Button btTake;

    private String projectId;
    private int customerId;

    private ImageResponse cad;
    private ImageResponse budget;
    private ImageResponse state;

    public static void start(Context context, String projectId, int customerId) {
        Intent intent = new Intent(context, UpdateOrderActivity.class);
        intent.putExtra("projectId", projectId);
        intent.putExtra("customerId", customerId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_order);

        initData();
        initView();
        checkData();
    }

    private void initView() {
        ImageView ivBack = (ImageView) findViewById(R.id.iv_back_update_order);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etTitle = (EditText) findViewById(R.id.et_title_update_order);
        ivCad = (ImageView) findViewById(R.id.iv_image_cad_update_order);
        ivInfo = (ImageView) findViewById(R.id.iv_image_info_update_order);
        ivTable = (ImageView) findViewById(R.id.iv_image_table_update_order);
        ivAddCad = (ImageView) findViewById(R.id.iv_image_update_order);
        ivAddInfo = (ImageView) findViewById(R.id.iv_info_update_order);
        ivAddTable = (ImageView) findViewById(R.id.iv_table_update_order);
        ivAddCad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkImage(0);
            }
        });
        ivAddInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkImage(1);
            }
        });
        ivAddTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkImage(2);
            }
        });

        llBox = (LinearLayout) findViewById(R.id.ll_box_update_order);
        etName = (EditText) findViewById(R.id.et_name_update_order);
        etPhone = (EditText) findViewById(R.id.et_phone_update_order);
        etAddress = (EditText) findViewById(R.id.et_address_update_order);

        btTake = (Button) findViewById(R.id.bt_taker_update_order);
        btTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trim = etTitle.getText().toString().trim();
                if (TextUtils.isEmpty(trim)) {
                    Toast.makeText(UpdateOrderActivity.this, "请添加标题", Toast.LENGTH_SHORT).show();
                } else if (cad == null || budget == null || state == null){
                    Toast.makeText(UpdateOrderActivity.this, "请完善信息", Toast.LENGTH_SHORT).show();
                } else {
                    String cadString = jsonToString(cad);
                    String infoString = jsonToString(budget);
                    String tableString = jsonToString(state);
                    addOrUpadteProject(trim, cadString, infoString, tableString);
                }

                if(customerId > 0) {
                    String name = etName.getText().toString().trim();
                    String phone = etPhone.getText().toString().trim();
                    String address = etAddress.getText().toString().trim();
                    if (TextUtils.isEmpty(name)) {
                        Toast.makeText(UpdateOrderActivity.this, "姓名不能为空", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(phone)) {
                        Toast.makeText(UpdateOrderActivity.this, "电话不能为空", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(address)) {
                        Toast.makeText(UpdateOrderActivity.this, "地址不能为空", Toast.LENGTH_SHORT).show();
                    } else {
                        addCustomerInfo(projectId, String.valueOf(customerId), name, phone, address);
                    }
                }
            }
        });
    }

    private void initData() {
        projectId = getIntent().getStringExtra("projectId");
        customerId = getIntent().getIntExtra("customerId", 0);
    }

    private void checkData() {
        getProjectDetail(projectId);
        if (customerId > 0) {
            llBox.setVisibility(View.VISIBLE);
            getCustomerInfo(String.valueOf(customerId));
        } else {
            llBox.setVisibility(View.GONE);
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
                    Toast.makeText(UpdateOrderActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProjectDetailsResponse> call, Throwable t) {
                Toast.makeText(UpdateOrderActivity.this, "网络异常，请检查您的网络！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setTop(ProjectDetailsResponse.DataBean data) {
        etTitle.setText(data.getTitle());
        List<ProjectDetailsResponse.DataBean.CadImgListBean> cadImgs = data.getCadImgList();
        if (cadImgs != null && !cadImgs.isEmpty()) {
            cad = getImageResponse(cadImgs.get(0).getImgUrl(), cadImgs.get(0).getWidth(), cadImgs.get(0).getHeight());
            String cadUrl = cadImgs.get(0).getImgUrl();
            if (!TextUtils.isEmpty(cadUrl)) {
                Glide.with(this)
                        .load(cadUrl)
                        .override(400, 400)
                        .fitCenter()
                        .into(ivCad);
            }
        }
        List<ProjectDetailsResponse.DataBean.BudgetImgListBean> budgetImgs = data.getBudgetImgList();
        if (budgetImgs != null && !budgetImgs.isEmpty()) {
            budget = getImageResponse(budgetImgs.get(0).getImgUrl(), budgetImgs.get(0).getWidth(), budgetImgs.get(0).getHeight());
            String budgetUrl = budgetImgs.get(0).getImgUrl();
            if (!TextUtils.isEmpty(budgetUrl)) {
                Glide.with(this)
                        .load(budgetUrl)
                        .override(400, 400)
                        .fitCenter()
                        .into(ivInfo);
            }
        }
        List<ProjectDetailsResponse.DataBean.StateImgListBean> stateImages = data.getStateImgList();
        if (stateImages != null && !stateImages.isEmpty()) {
            state = getImageResponse(stateImages.get(0).getImgUrl(), stateImages.get(0).getWidth(), stateImages.get(0).getHeight());
            String stateUrl = stateImages.get(0).getImgUrl();
            if (!TextUtils.isEmpty(stateUrl)) {
                Glide.with(this)
                        .load(stateUrl)
                        .override(400, 400)
                        .fitCenter()
                        .into(ivTable);
            }
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
                    Toast.makeText(UpdateOrderActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CustomerResponse> call, Throwable t) {
                Toast.makeText(UpdateOrderActivity.this, "网络异常，请检查您的网络！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setBottom(CustomerResponse.DataBean data) {
        etName.setText(data.getCustName());
        etPhone.setText(data.getPhoneNum());
        etAddress.setText(data.getAddress());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (data != null) {
                ArrayList<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                String image = photos.get(0);
                switch (requestCode) {
                    case 0:
                        updateImage(image, 0);
                        break;
                    case 1:
                        updateImage(image, 1);
                        break;
                    case 2:
                        updateImage(image, 2);
                        break;
                }
            }
        }
    }

    private void checkImage(int requestCode) {
        PhotoPicker.builder()
                .setPhotoCount(1)
                .setShowCamera(true)
                .setShowGif(true)
                .setPreviewEnabled(false)
                .start(this, requestCode);
    }

    private void updateImage(String url, final int type) {
        if (!Utils.isNetworkOn()) {
            Toast.makeText(this, "网络异常，请检查您的网络！", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(url);

        MultipartBody.Part body = prepareFilePart(file.getName(), file);

        Call<UpdateImageResponse> call = UpdateModel.getInstance().updateImage(body);
        call.enqueue(new Callback<UpdateImageResponse>() {
            @Override
            public void onResponse(Call<UpdateImageResponse> call, Response<UpdateImageResponse> response) {

                if (response.body().getCode() == 0) {
                    UpdateImageResponse.DataBean data = response.body().getData();
                    if (data != null) {
                        Toast.makeText(UpdateOrderActivity.this, "上传图片成功", Toast.LENGTH_SHORT).show();
                        setImage(type, data);
                    }
                } else {
                    Toast.makeText(UpdateOrderActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UpdateImageResponse> call, Throwable t) {
                Toast.makeText(UpdateOrderActivity.this, "上传图片失败，请重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private MultipartBody.Part prepareFilePart(String partName, File file) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);

        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    private void addOrUpadteProject(String title, String budgetImgs, String cadImgs, String stateImgs) {
        Call<EmptyResponse> call = NetModel.getInstance().addOrUpadteProject(SettingUtils.instance().getToken(), projectId, title, cadImgs, budgetImgs, stateImgs);
        call.enqueue(new Callback<EmptyResponse>() {
            @Override
            public void onResponse(Call<EmptyResponse> call, Response<EmptyResponse> response) {
                if (response.body().getCode() == 0) {
                    Toast.makeText(UpdateOrderActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(UpdateOrderActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EmptyResponse> call, Throwable t) {
                Toast.makeText(UpdateOrderActivity.this, "提交失败，请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setImage(int type, UpdateImageResponse.DataBean data) {
        switch (type) {
            case 0:
                cad = getImageResponse(data);
                Glide.with(UpdateOrderActivity.this)
                        .load(data.getUrl())
                        .override(400, 400)
                        .fitCenter()
                        .into(ivCad);
                break;
            case 1:
                budget = getImageResponse(data);
                Glide.with(UpdateOrderActivity.this)
                        .load(data.getUrl())
                        .override(400, 400)
                        .fitCenter()
                        .into(ivInfo);
                break;
            case 2:
                state = getImageResponse(data);
                Glide.with(UpdateOrderActivity.this)
                        .load(data.getUrl())
                        .override(400, 400)
                        .fitCenter()
                        .into(ivTable);
                break;
        }
    }

    private ImageResponse getImageResponse(UpdateImageResponse.DataBean data) {
        String url = data.getUrl();
        int width = data.getWidth();
        int height = data.getHeight();

        return new ImageResponse(url, width, height);
    }

    private ImageResponse getImageResponse(String url, int width, int height) {
        return new ImageResponse(url, width, height);
    }

    private String jsonToString(ImageResponse response) {
        List<ImageResponse> list = new ArrayList<>();
        list.add(response);

        Gson gson = new Gson();
        String toJson = gson.toJson(list);
        return toJson;
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
                    Toast.makeText(UpdateOrderActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(UpdateOrderActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EmptyResponse> call, Throwable t) {
                Toast.makeText(UpdateOrderActivity.this, "提交失败，请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

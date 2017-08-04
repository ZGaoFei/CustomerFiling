package cn.com.shijizl.customerfiling.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.com.shijizl.customerfiling.R;
import cn.com.shijizl.customerfiling.base.BaseActivity;
import cn.com.shijizl.customerfiling.net.NetModel;
import cn.com.shijizl.customerfiling.net.UpdateModel;
import cn.com.shijizl.customerfiling.net.model.EmptyResponse;
import cn.com.shijizl.customerfiling.net.model.ImageResponse;
import cn.com.shijizl.customerfiling.net.model.ProjectDetailsResponse;
import cn.com.shijizl.customerfiling.net.model.UpdateImageResponse;
import cn.com.shijizl.customerfiling.order.adapter.AddImageAdapter;
import cn.com.shijizl.customerfiling.utils.SettingUtils;
import cn.com.shijizl.customerfiling.utils.Utils;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddOrderActivity extends BaseActivity {
    private ArrayList<ImageResponse> cadList = new ArrayList<>();
    private ArrayList<ImageResponse> infoList = new ArrayList<>();
    private ArrayList<ImageResponse> tableList = new ArrayList<>();
    private AddImageAdapter cadAdapter;
    private AddImageAdapter infoAdapter;
    private AddImageAdapter tableAdapter;

    private String projectId;
    private String title;
    private String customerId, custName, phoneNum, address;

    public static void start(Context context) {
        Intent intent = new Intent(context, AddOrderActivity.class);
        context.startActivity(intent);
    }

    public static void start(Context context, String projectId, String title, String customerId, String custName, String phoneNum, String address) {
        Intent intent = new Intent(context, AddOrderActivity.class);
        intent.putExtra("projectId", projectId);
        intent.putExtra("title", title);
        intent.putExtra("customerId", customerId);
        intent.putExtra("custName", custName);
        intent.putExtra("phoneNum", phoneNum);
        intent.putExtra("address", address);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);

        projectId = getIntent().getStringExtra("projectId");
        title = getIntent().getStringExtra("title");
        customerId = getIntent().getStringExtra("customerId");
        custName = getIntent().getStringExtra("custName");
        phoneNum = getIntent().getStringExtra("phoneNum");
        address = getIntent().getStringExtra("address");

        initView();

        if (!TextUtils.isEmpty(projectId)) {
            getProjectDetail(projectId);
        }
    }

    private void initView() {
        ImageView ivBack = (ImageView) findViewById(R.id.iv_back_add_order);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final EditText etTitle = (EditText) findViewById(R.id.et_phone_add_order);
        if (!TextUtils.isEmpty(title)) {
            etTitle.setText(title);
        }
        RecyclerView rvCad = (RecyclerView) findViewById(R.id.rv_image_cad_add_order);
        RecyclerView rvInfo = (RecyclerView) findViewById(R.id.rv_image_info_add_order);
        RecyclerView rvTable = (RecyclerView) findViewById(R.id.rv_image_table_add_order);

        cadList.add(addEmptyImage());
        infoList.add(addEmptyImage());
        tableList.add(addEmptyImage());

        cadAdapter = new AddImageAdapter(this, cadList);
        infoAdapter = new AddImageAdapter(this, infoList);
        tableAdapter = new AddImageAdapter(this, tableList);
        setAdapter(rvCad, cadList, cadAdapter, 0, 10);
        setAdapter(rvInfo, infoList, infoAdapter, 1, 11);
        setAdapter(rvTable, tableList, tableAdapter, 2, 12);

        Button btNext = (Button) findViewById(R.id.bt_taker_order);
        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trim = etTitle.getText().toString().trim();
                if (TextUtils.isEmpty(trim)) {
                    Toast.makeText(AddOrderActivity.this, "请添加标题", Toast.LENGTH_SHORT).show();
                } else {
                    String cadString = jsonToString(cadList);
                    String infoString = jsonToString(infoList);
                    String tableString = jsonToString(tableList);
                    addOrUpadteProject(trim, cadString, infoString, tableString);
                }

                if (!TextUtils.isEmpty(projectId)) {
                    addCustomerInfo(projectId, customerId, custName, phoneNum, address);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (data != null) {
                ArrayList<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                switch (requestCode) {
                    case 0:
                        takeImage(photos, 0);
                        break;
                    case 1:
                        takeImage(photos, 1);
                        break;
                    case 2:
                        takeImage(photos, 2);
                        break;
                    case 10:
                        deleteImageNext(photos, 10);
                        break;
                    case 11:
                        deleteImageNext(photos, 11);
                        break;
                    case 12:
                        deleteImageNext(photos, 12);
                        break;
                }
            }
        }
    }

    private void takeImage(ArrayList<String> photos, int type) {
        for (int i = 0; i < photos.size(); i++) {
            updateImage(photos.get(i), type);
        }
    }

    private void checkImage(int requestCode) {
        PhotoPicker.builder()
                .setPhotoCount(9)
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
                        setImage(type, data);
                    }
                } else {
                    Toast.makeText(AddOrderActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UpdateImageResponse> call, Throwable t) {
                Toast.makeText(AddOrderActivity.this, "上传图片失败，请重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private MultipartBody.Part prepareFilePart(String partName, File file) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);

        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    private void addOrUpadteProject(String title, String cadImgs, String budgetImgs, String stateImgs) {
        Call<EmptyResponse> call = NetModel.getInstance().addOrUpadteProject(SettingUtils.instance().getToken(), projectId, title, cadImgs, budgetImgs, stateImgs);
        call.enqueue(new Callback<EmptyResponse>() {
            @Override
            public void onResponse(Call<EmptyResponse> call, Response<EmptyResponse> response) {
                if (response.body() != null) {
                    if (response.body().getCode() == 0) {
                        Toast.makeText(AddOrderActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddOrderActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<EmptyResponse> call, Throwable t) {
                Toast.makeText(AddOrderActivity.this, "提交失败，请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setImage(int type, UpdateImageResponse.DataBean data) {
        switch (type) {
            case 0:
                ImageResponse cad = getImageResponse(data);
                if (cadList.size() < 13) {
                    cadList.add(0, cad);
                    cadAdapter.update(cadList);
                } else {
                    Toast.makeText(this, "最多只能选择12张图片", Toast.LENGTH_SHORT).show();
                }
                break;
            case 1:
                ImageResponse info = getImageResponse(data);
                if (infoList.size() < 13) {
                    infoList.add(0, info);
                    infoAdapter.update(infoList);
                } else {
                    Toast.makeText(this, "最多只能选择12张图片", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                ImageResponse table = getImageResponse(data);
                if (tableList.size() < 13) {
                    tableList.add(0, table);
                    tableAdapter.update(tableList);
                } else {
                    Toast.makeText(this, "最多只能选择12张图片", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private ImageResponse getImageResponse(UpdateImageResponse.DataBean data) {
        String url = data.getUrl();
        int width = data.getWidth();
        int height = data.getHeight();

        return new ImageResponse(url, width, height);
    }

    private String jsonToString(List<ImageResponse> list) {
        list.remove(list.size() - 1);
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    private void setAdapter(RecyclerView recyclerView, final ArrayList<ImageResponse> list, AddImageAdapter adapter, final int type, final int deleteType) {
        GridLayoutManager manager = new GridLayoutManager(this, 4);
        manager.setSmoothScrollbarEnabled(true);
        manager.setAutoMeasureEnabled(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(manager);

        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new AddImageAdapter.ItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                if (list != null && !list.isEmpty()) {
                    PhotoPreview.builder()
                            .setPhotos(toStringList(list))
                            .setCurrentItem(position)
                            .setShowDeleteButton(true)
                            .start(AddOrderActivity.this, deleteType);
                }
            }

            @Override
            public void onAddItemClickListener(View view, int position) {
                checkImage(type);
            }
        });
    }

    private ImageResponse addEmptyImage() {
        return new ImageResponse(null, 0, 0);
    }

    private ArrayList<String> toStringList(List<ImageResponse> list) {
        ArrayList<String> stringList = new ArrayList<>();
        for (int i = 0; i < list.size() - 1; i++) {
            stringList.add(list.get(i).getImgUrl());
        }

        return stringList;
    }

    private void deleteImageNext(ArrayList<String> photos, int type) {
        switch (type) {
            case 10:
                deleteImage(cadList, photos);
                cadAdapter.update(cadList);
                break;
            case 11:
                deleteImage(infoList, photos);
                infoAdapter.update(infoList);
                break;
            case 12:
                deleteImage(tableList, photos);
                tableAdapter.update(tableList);
                break;
        }
    }

    private void deleteImage(ArrayList<ImageResponse> list, ArrayList<String> photos) {
        if (photos.isEmpty()) {
            list.clear();
            list.add(addEmptyImage());
        } else {
            for (int i = 0; i < list.size(); i++) {
                boolean isHave = false;
                for (int j = 0; j < photos.size(); j++) {
                    if (!TextUtils.isEmpty(list.get(i).getImgUrl())) {
                        if (list.get(i).getImgUrl().equals(photos.get(j))) {
                            isHave = true;
                            break;
                        } else {
                            isHave = false;
                        }
                    } else {
                        isHave = true;
                    }
                }
                if (!isHave) {
                    list.remove(i);
                }
            }
        }
    }

    //====修改工单信息=====
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
                    Toast.makeText(AddOrderActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProjectDetailsResponse> call, Throwable t) {
                Toast.makeText(AddOrderActivity.this, "网络异常，请检查您的网络！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setTop(ProjectDetailsResponse.DataBean data) {
        getList(cadList, data, 0);
        cadAdapter.update(cadList);
        getList(infoList, data, 1);
        infoAdapter.update(infoList);
        getList(tableList, data, 2);
        tableAdapter.update(tableList);
    }

    private void getList(ArrayList<ImageResponse> list, ProjectDetailsResponse.DataBean data, int type) {
        if (data != null) {
            switch (type) {
                case 0:
                    List<ProjectDetailsResponse.DataBean.CadImgListBean> cadImgs = data.getCadImgList();
                    if (cadImgs != null && !cadImgs.isEmpty()) {
                        for (int i = 0; i < cadImgs.size(); i++) {
                            ImageResponse response = new ImageResponse(cadImgs.get(i).getImgUrl(), cadImgs.get(i).getWidth(), cadImgs.get(i).getHeight());
                            list.add(0, response);
                        }
                    }else {

                    }
                    break;
                case 1:
                    List<ProjectDetailsResponse.DataBean.BudgetImgListBean> budgetImgs = data.getBudgetImgList();
                    if (budgetImgs != null && !budgetImgs.isEmpty()) {
                        for (int i = 0; i < budgetImgs.size(); i++) {
                            ImageResponse response = new ImageResponse(budgetImgs.get(i).getImgUrl(), budgetImgs.get(i).getWidth(), budgetImgs.get(i).getHeight());
                            list.add(0, response);
                        }
                    }else {

                    }
                    break;
                case 2:
                    List<ProjectDetailsResponse.DataBean.StateImgListBean> stateImages = data.getStateImgList();
                    if (stateImages != null && !stateImages.isEmpty()) {
                        for (int i = 0; i < stateImages.size(); i++) {
                            ImageResponse response = new ImageResponse(stateImages.get(i).getImgUrl(), stateImages.get(i).getWidth(), stateImages.get(i).getHeight());
                            list.add(0, response);
                        }
                    } else {

                    }
                    break;
            }
        }
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
            }

            @Override
            public void onFailure(Call<EmptyResponse> call, Throwable t) {
            }
        });
    }

}

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

import com.bumptech.glide.Glide;
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

public class AddOrderActivity extends BaseActivity {
    private ImageView ivImageCad;
    private ImageView ivImageInfo;
    private ImageView ivImageTable;
    private ImageView ivAddImage;
    private ImageView ivAddInfo;
    private ImageView ivAddTable;

    private ImageResponse cad;
    private ImageResponse info;
    private ImageResponse table;

    public static void start(Context context) {
        Intent intent = new Intent(context, AddOrderActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);

        initView();
    }

    private void initView() {
        ImageView ivBack = (ImageView) findViewById(R.id.iv_back_add_order);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivImageCad = (ImageView) findViewById(R.id.iv_image_cad_order);
        ivImageInfo = (ImageView) findViewById(R.id.iv_image_info_order);
        ivImageTable = (ImageView) findViewById(R.id.iv_image_table_order);
        ivAddImage = (ImageView) findViewById(R.id.iv_add_image_order);
        ivAddInfo = (ImageView) findViewById(R.id.iv_add_info_order);
        ivAddTable = (ImageView) findViewById(R.id.iv_add_table_order);
        ivImageCad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkImage(0);
            }
        });
        ivImageInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkImage(1);
            }
        });
        ivImageTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkImage(2);
            }
        });
        ivAddImage.setOnClickListener(new View.OnClickListener() {
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

        final EditText etTitle = (EditText) findViewById(R.id.et_phone_add_order);

        Button btNext = (Button) findViewById(R.id.bt_taker_order);
        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trim = etTitle.getText().toString().trim();
                if (TextUtils.isEmpty(trim)) {
                    Toast.makeText(AddOrderActivity.this, "请添加标题", Toast.LENGTH_SHORT).show();
                } else if (cad == null || info == null || table == null){
                    Toast.makeText(AddOrderActivity.this, "请完善信息", Toast.LENGTH_SHORT).show();
                } else {
                    String cadString = jsonToString(cad);
                    String infoString = jsonToString(info);
                    String tableString = jsonToString(table);
                    addOrUpadteProject(trim, cadString, infoString, tableString);
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

    private void addOrUpadteProject(String title, String budgetImgs, String cadImgs, String stateImgs) {
        Call<EmptyResponse> call = NetModel.getInstance().addOrUpadteProject(SettingUtils.instance().getToken(), "", title, budgetImgs, cadImgs, stateImgs);
        call.enqueue(new Callback<EmptyResponse>() {
            @Override
            public void onResponse(Call<EmptyResponse> call, Response<EmptyResponse> response) {
                if (response.body().getCode() == 0) {
                    Toast.makeText(AddOrderActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddOrderActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
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
                cad = getImageResponse(data);
                ivAddImage.setVisibility(View.GONE);
                ivImageCad.setVisibility(View.VISIBLE);
                Glide.with(AddOrderActivity.this)
                        .load(data.getUrl())
                        .override(400, 400)
                        .fitCenter()
                        .placeholder(R.drawable.place_holder)
                        .into(ivImageCad);
                break;
            case 1:
                info = getImageResponse(data);
                ivAddInfo.setVisibility(View.GONE);
                ivImageInfo.setVisibility(View.VISIBLE);
                Glide.with(AddOrderActivity.this)
                        .load(data.getUrl())
                        .override(400, 400)
                        .fitCenter()
                        .placeholder(R.drawable.place_holder)
                        .into(ivImageInfo);
                break;
            case 2:
                table = getImageResponse(data);
                ivAddTable.setVisibility(View.GONE);
                ivImageTable.setVisibility(View.VISIBLE);
                Glide.with(AddOrderActivity.this)
                        .load(data.getUrl())
                        .override(400, 400)
                        .fitCenter()
                        .placeholder(R.drawable.place_holder)
                        .into(ivImageTable);
                break;
        }
    }

    private ImageResponse getImageResponse(UpdateImageResponse.DataBean data) {
        String url = data.getUrl();
        int width = data.getWidth();
        int height = data.getHeight();

        return new ImageResponse(url, width, height);
    }

    private String jsonToString(ImageResponse response) {
        List<ImageResponse> list = new ArrayList<>();
        list.add(response);

        Gson gson = new Gson();
        String toJson = gson.toJson(list);
        return toJson;
    }
}

package cn.com.shijizl.customerfiling.net;


import cn.com.shijizl.customerfiling.net.model.UpdateImageResponse;
import okhttp3.MultipartBody;
import retrofit2.Call;

public class UpdateModel {

    private static ApiService apiService;
    private static UpdateModel updateModel;

    public static UpdateModel getInstance() {
        if (updateModel == null) {
            synchronized (UpdateModel.class) {
                if (updateModel == null) {
                    updateModel = new UpdateModel();
                }
            }
        }
        return updateModel;
    }

    private UpdateModel() {
        apiService = RetrofitHelper.createUploadsApi(ApiService.class);
    }

    public Call<UpdateImageResponse> updateImage(MultipartBody.Part file) {
        return apiService.upload(file);
    }
}

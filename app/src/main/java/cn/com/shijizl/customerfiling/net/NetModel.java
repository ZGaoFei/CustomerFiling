package cn.com.shijizl.customerfiling.net;


import cn.com.shijizl.customerfiling.net.model.CodeResponse;
import cn.com.shijizl.customerfiling.net.model.CustomerResponse;
import cn.com.shijizl.customerfiling.net.model.EmptyResponse;
import cn.com.shijizl.customerfiling.net.model.ProjectDetailsResponse;
import cn.com.shijizl.customerfiling.net.model.ProjectListResponse;
import cn.com.shijizl.customerfiling.net.model.ProjectScheduleResponse;
import cn.com.shijizl.customerfiling.net.model.RegisterResponse;
import cn.com.shijizl.customerfiling.net.model.UserInfoResponse;
import retrofit2.Call;

public class NetModel {
    private static ApiService apiService;
    private static NetModel netModel;

    public static NetModel getInstance() {
        if (netModel == null) {
            synchronized (NetModel.class) {
                if (netModel == null) {
                    netModel = new NetModel();
                }
            }
        }
        return netModel;
    }

    private NetModel() {
        apiService = RetrofitHelper.createApi(ApiService.class);
    }

    public Call<CodeResponse> getCode(String phone) {
        return apiService.getOrderNum(phone);
    }

    public Call<RegisterResponse> register(String userName, String passWord, int verifyCode) {
        return apiService.getNewComerTask(userName, passWord, verifyCode);
    }

    public Call<RegisterResponse> login(String userName, String passWord) {
        return apiService.login(userName, passWord);
    }

    public Call<CodeResponse> getCodeForget(String phone) {
        return apiService.getOrderNumForget(phone);
    }

    public Call<EmptyResponse> resetPassWord(String phoneNum, String passWord) {
        return apiService.resetPassWord(phoneNum, passWord);
    }

    public Call<EmptyResponse> addOrUpadteProject(String accessToken, String projectId, String title, String cadImgs, String budgetImgs, String stateImgs) {
        return apiService.addOrUpadteProject(accessToken, projectId, title, cadImgs, budgetImgs, stateImgs);
    }

    public Call<UserInfoResponse> getUserInfo(String accessToken) {
        return apiService.getUserInfo(accessToken);
    }

    public Call<EmptyResponse> updatePassWord(String accessToken, String oldPassWord, String newPassWord) {
        return apiService.updatePassWord(accessToken, oldPassWord, newPassWord);
    }

    public Call<ProjectListResponse> getProjectList(String accessToken, int pageNum, int pageSize) {
        return apiService.getProjectList(accessToken, pageSize, pageNum);
    }

    public Call<ProjectDetailsResponse> getProjectDetail(String accessToken, String projectId) {
        return apiService.getProjectDetail(accessToken, projectId);
    }

    public Call<CustomerResponse> getCustomerInfo(String accessToken, String customerId) {
        return apiService.getCustomerInfo(accessToken, customerId);
    }

    public Call<EmptyResponse> startProject(String accessToken, String projectId) {
        return apiService.startProject(accessToken, projectId);
    }

    public Call<EmptyResponse> addOrUpadteCustomerInfo(String accessToken, String projectId, String customerId, String custName, String phoneNum, String address) {
        return apiService.addOrUpadteCustomerInfo(accessToken, projectId, customerId, custName, phoneNum, address);
    }

    public Call<EmptyResponse> updateProjectSchedule(String accessToken, String projectId, String code, String stepDesc) {
        return apiService.updateProjectSchedule(accessToken, projectId, code, stepDesc);
    }

    public Call<ProjectScheduleResponse> getProjectSchedule(String accessToken, String projectId) {
        return apiService.getProjectSchedule(accessToken, projectId);
    }

    public Call<ProjectScheduleResponse> getSysALLSchedule(String accessToken) {
        return apiService.getSysALLSchedule(accessToken);
    }

    public Call<EmptyResponse> updateUserInfo(String accessToken, String profile, String realName) {
        return apiService.updateUserInfo(accessToken, profile, realName);
    }

    public Call<EmptyResponse> deleteProject(String accessToken, String projectId) {
        return apiService.deleteProject(accessToken, projectId);
    }

    public Call<EmptyResponse> checkVerifyCode(String phoneNum, String code) {
        return apiService.checkVerifyCode(phoneNum, code);
    }
}

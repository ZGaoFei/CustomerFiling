package cn.com.shijizl.customerfiling.net;


import cn.com.shijizl.customerfiling.net.model.CodeResponse;
import cn.com.shijizl.customerfiling.net.model.CustomerResponse;
import cn.com.shijizl.customerfiling.net.model.EmptyResponse;
import cn.com.shijizl.customerfiling.net.model.ProjectDetailsResponse;
import cn.com.shijizl.customerfiling.net.model.ProjectListResponse;
import cn.com.shijizl.customerfiling.net.model.ProjectScheduleResponse;
import cn.com.shijizl.customerfiling.net.model.RegisterResponse;
import cn.com.shijizl.customerfiling.net.model.UpdateImageResponse;
import cn.com.shijizl.customerfiling.net.model.UserInfoResponse;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {

    @GET("yorkba/app/user/getVerifyCode")
    Call<CodeResponse> getOrderNum(
            @Query("phoneNum") String phoneNum);


    @FormUrlEncoded
    @POST("yorkba/app/user/regist")
    Call<RegisterResponse> getNewComerTask(
            @Field("userName") String userName,
            @Field("passWord") String passWord,
            @Field("verifyCode") int verifyCode
    );

    @FormUrlEncoded
    @POST("yorkba/app/user/login")
    Call<RegisterResponse> login(
            @Field("userName") String userName,
            @Field("passWord") String passWord
    );

    @GET("yorkba/app/user/getCodeForgot")
    Call<CodeResponse> getOrderNumForget(
            @Query("phoneNum") String phoneNum);

    @FormUrlEncoded
    @POST("yorkba/app/user/resetPassWord")
    Call<EmptyResponse> resetPassWord(
            @Field("phoneNum") String phoneNum,
            @Field("passWord") String passWord
    );

    @Multipart
    @POST("yorkba/app/picture/upload")
    Call<UpdateImageResponse> upload(
            @Part MultipartBody.Part file
    );

    @FormUrlEncoded
    @POST("yorkba/app/project/addOrUpadteProject")
    Call<EmptyResponse> addOrUpadteProject(
            @Field("accessToken") String accessToken,
            @Field("projectId") String projectId,
            @Field("title") String title,
            @Field("budgetImgs") String budgetImgs,
            @Field("cadImgs") String cadImgs,
            @Field("stateImgs") String stateImgs
    );

    @GET("yorkba/app/user/getUserInfo")
    Call<UserInfoResponse> getUserInfo(
            @Query("accessToken") String accessToken
    );

    @FormUrlEncoded
    @POST("yorkba/app/user/updatePassWord")
    Call<EmptyResponse> updatePassWord(
            @Field("accessToken") String accessToken,
            @Field("oldPassWord") String oldPassWord,
            @Field("newPassWord") String newPassWord
    );

    @GET("yorkba/app/project/getProjectList")
    Call<ProjectListResponse> getProjectList(
            @Query("accessToken") String accessToken,
            @Query("pageSize") int pageSize,
            @Query("pageNum") int pageNum
    );

    @GET("yorkba/app/project/getProjectDetail")
    Call<ProjectDetailsResponse> getProjectDetail(
            @Query("accessToken") String accessToken,
            @Query("projectId") String projectId
    );

    @GET("yorkba/app/project/getCustomerInfo")
    Call<CustomerResponse> getCustomerInfo(
            @Query("accessToken") String accessToken,
            @Query("customerId") String customerId
    );

    @FormUrlEncoded
    @POST("yorkba/app/project/startProject")
    Call<EmptyResponse> startProject(
            @Field("accessToken") String accessToken,
            @Field("projectId") String projectId
    );

    @FormUrlEncoded
    @POST("yorkba/app/project/addOrUpadteCustomerInfo")
    Call<EmptyResponse> addOrUpadteCustomerInfo(
            @Field("accessToken") String accessToken,
            @Field("projectId") String projectId,
            @Field("customerId") String customerId,
            @Field("custName") String custName,
            @Field("phoneNum") String phoneNum,
            @Field("address") String address
    );

    @FormUrlEncoded
    @POST("yorkba/app/project/updateProjectSchedule")
    Call<EmptyResponse> updateProjectSchedule(
            @Field("accessToken") String accessToken,
            @Field("projectId") String projectId,
            @Field("code") String code,
            @Field("stepDesc") String stepDesc
    );

    @GET("yorkba/app/project/getProjectSchedule")
    Call<ProjectScheduleResponse> getProjectSchedule(
            @Query("accessToken") String accessToken,
            @Query("projectId") String projectId
    );

    @GET("yorkba/app/project/getSysALLSchedule")
    Call<ProjectScheduleResponse> getSysALLSchedule(
            @Query("accessToken") String accessToken
    );

    @FormUrlEncoded
    @POST("yorkba/app/user/updateUserInfo")
    Call<EmptyResponse> updateUserInfo(
            @Field("accessToken") String accessToken,
            @Field("profile") String profile,
            @Field("realName") String realName
    );
}

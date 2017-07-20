package cn.com.shijizl.customerfiling.net.core;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * get请求添加公共参数以及加sign的逻辑
 * 1. 通过 {@link CoreUtil#getNewHttpUrl(HttpUrl)}，将公共参数加入
 * 2. 通过 {@link CoreUtil#calSign(Map)} 对参数进行sign
 * 3. 讲sign得到的字符串加入到query参数中
 * 4. 构建request对象，请求
 * post请求添加公共参数以及加sign的逻辑
 * 1. 通过 {@link CoreUtil#getParamsFromPostMethod(RequestBody)} 获取params
 * 2. 通过 {@link CoreUtil#calSign(Map)} 对参数进行sign
 * 3. 将sign的参数加到params中，
 * 4. 构建request对象，请求
 */
public class SecurityInterceptor implements Interceptor {
    private final static String PARAM_SIGN = "sign";
    private final static String METHOD_GET = "GET";
    private final static String METHOD_POST = "POST";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        HashMap<String, String> params;
        MediaType mediaType;
        if (request.method().equals(METHOD_GET)) {
            HttpUrl url = CoreUtil.getNewHttpUrl(request.url());
            params = CoreUtil.getParamsFromGetMethod(url);
            url = url.newBuilder().addQueryParameter(PARAM_SIGN, CoreUtil.calSign(params)).build();
            request = request.newBuilder()
                    .url(url)
                    .method(request.method(), request.body())
                    .build();
        } else if (request.method().equals(METHOD_POST)) {
            params = CoreUtil.getParamsFromPostMethod(request.body());
            params.put(PARAM_SIGN, CoreUtil.calSign(params));
            if (null == request.body().contentType()) {
                mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
            } else {
                mediaType = request.body().contentType();
            }
            RequestBody body = RequestBody.create(mediaType, CoreUtil.mapToString(params));
            request = request.newBuilder()
                    .method(request.method(), body)
                    .build();
        }
        return chain.proceed(request);
    }

}

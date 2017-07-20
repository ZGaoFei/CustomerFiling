package cn.com.shijizl.customerfiling.net.core;

import android.text.TextUtils;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.shijizl.customerfiling.utils.Utils;
import okhttp3.HttpUrl;
import okhttp3.RequestBody;
import okio.Buffer;

public class CoreUtil {
    private final static String PARAM_APP_ID = "app_id";
    private final static String PARAM_NONCE = "nonce";
    private final static String PARAM_TIMESTAMP = "timestamp";
    private static final String PARAM_VERSION = "version";
    private static final String PARAM_BUILD = "build";
    private final static String PARAM_SIGN = "sign";
    private static final String PARAM_UDID = "udid";
    private static final String PARAM_DEVICE_MODEL = "devicemodel";
    private static final String PARAM_SYS_NAME = "sysname";
    private static final String PARAM_SYS_VERSION = "sysversion";


    private static HashMap<String, String> getCommonParamsNoSign(HashMap<String, String> params) {
        if (params == null)
            params = new HashMap<>();
        params.put(PARAM_APP_ID, "android");
        params.put(PARAM_NONCE, Utils.getRandomString());
        params.put(PARAM_UDID, Utils.getDeviceId());
        params.put(PARAM_DEVICE_MODEL, Utils.getDeviceModel());
        params.put(PARAM_SYS_NAME, Utils.getSystemName());
        params.put(PARAM_SYS_VERSION, Utils.getSystemVersion());
        if (!TextUtils.isEmpty(getVersion())) {
            params.put(PARAM_VERSION, getVersion());
            params.put(PARAM_BUILD, getBuildVersion());
        }

        String timeStamp = Utils.getTimeStamp();
        params.put(PARAM_TIMESTAMP, timeStamp);
        return params;
    }

    static String calSign(Map<String, String> params) {
        if (params == null) {
            return "";
        }

        String[] keyArray = params.keySet().toArray(new String[0]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (String key : keyArray) {

            if (!PARAM_SIGN.equalsIgnoreCase(key)) {
                sb.append(key);
                sb.append("=");
                sb.append(URLDecoder.decode(params.get(key)));
                sb.append("&");
            }
        }
        String paramString = sb.toString();

        if (paramString.length() != 0) {
            return Utils.encryption(paramString);
        } else {
            return "";
        }

    }

    private static String getBuildVersion() {
        String version = Utils.getVersionName();
        if (!TextUtils.isEmpty(version)) {
            return version.substring(3);
        }

        return null;
    }

    private static String getVersion() {
        String version = Utils.getVersionName();
        if (!TextUtils.isEmpty(version)) {
            return version.substring(0, 3);
        }

        return null;
    }

    /**
     * 从get请求的url中获取参数列表
     *
     * @param httpUrl httpurl
     * @return 返回从url中获取的query参数列表
     */
    static HashMap<String, String> getParamsFromGetMethod(HttpUrl httpUrl) {
        HashMap<String, String> params = new HashMap<>();
        int paramsNum = httpUrl.queryParameterNames().size();
        for (int i = 0; i < paramsNum; i++) {
            params.put(httpUrl.queryParameterName(i), httpUrl.queryParameterValue(i));
        }
        return params;
    }

    /**
     * 从post请求提中获取参数列表
     *
     * @param body post请求请求体
     * @return 返回body中的参数以及公共的请求参数
     * @throws IOException
     */
    static HashMap<String, String> getParamsFromPostMethod(RequestBody body) throws IOException {
        HashMap<String, String> params = new HashMap<>();
        if (body == null) {
            return params;
        }
        Buffer buffer = new Buffer();
        body.writeTo(buffer);
        StringBuilder stringBuilder = new StringBuilder();
        String s;
        while ((s = buffer.readUtf8Line()) != null) {
            stringBuilder.append(s);
        }
        //先分割原先的参数
        String paramsString = stringBuilder.toString().trim();
        if (paramsString.length() > 0) {
            String[] params_string = paramsString.split("&");
            for (int i = 0; i < params_string.length; i++) {
                String[] param = params_string[i].split("=");
                if (param.length == 1) {
                    params.put(param[0], "");
                } else {
                    params.put(param[0], param[1]);
                }
            }
        }
        //拼接新的参数并返回
        return getCommonParamsNoSign(params);
    }


    /**
     * 获取拼接了公共参数的HttpUrl
     *
     * @param url get请求的httpurl
     * @return 返回重新build之后的httpurl，加入了公共参数
     */
    static HttpUrl getNewHttpUrl(HttpUrl url) {
        HttpUrl.Builder builder = url.newBuilder()
                .addQueryParameter(PARAM_APP_ID, "android")
                .addQueryParameter(PARAM_NONCE, Utils.getRandomString())
                .addQueryParameter(PARAM_UDID, Utils.getDeviceId())
                .addQueryParameter(PARAM_DEVICE_MODEL, Utils.getDeviceModel())
                .addQueryParameter(PARAM_SYS_NAME, Utils.getSystemName())
                .addQueryParameter(PARAM_SYS_VERSION, Utils.getSystemVersion());
        if (!TextUtils.isEmpty(getVersion())) {
            builder.addQueryParameter(PARAM_VERSION, getVersion())
                    .addQueryParameter(PARAM_BUILD, getBuildVersion());
        }
        String timeStamp = Utils.getTimeStamp();
        builder.addQueryParameter(PARAM_TIMESTAMP, timeStamp);
        return builder.build();
    }

    /**
     * 将map转化为string
     *
     * @param params 参数列表
     * @return 转化后的字符串
     */
    public static String mapToString(HashMap<String, String> params) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (params == null) {
            return stringBuilder.toString();
        }

        List<Map.Entry<String, String>> infoIds = new ArrayList<>(params.entrySet());
        for (int i = 0; i < infoIds.size(); i++) {
            String id = infoIds.get(i).toString();
            if (i == infoIds.size() - 1) {
                stringBuilder.append(id);
            } else {
                stringBuilder.append(id).append("&");
            }
        }
        return stringBuilder.toString();
    }

    public static int booleanToInt(boolean b) {
        return b ? 1 : 0;
    }

    public static boolean intToBoolean(int i) {
        return i >= 1;
    }

}

package cn.com.shijizl.customerfiling.utils;


import android.content.Context;
import android.content.SharedPreferences;

import cn.com.shijizl.customerfiling.base.App;

public class SettingUtils {
    private static final String ACCESS_TOKEN = "access_token";
    private static final String USER_ID = "user_id";

    private static SettingUtils settingUtils;
    private Context context;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private SettingUtils() {
        init();
    }

    public static SettingUtils instance() {
        if (settingUtils == null) {
            synchronized (SettingUtils.class) {
                if (settingUtils == null) {
                    settingUtils = new SettingUtils();
                }
            }
        }
        return settingUtils;
    }

    private void init() {
        sp = App.getInstance().getSharedPreferences("customer_filing", Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public void saveToken(String token) {
        editor.putString(ACCESS_TOKEN, token).apply();
    }

    public String getToken() {
        return sp.getString(ACCESS_TOKEN, "");
    }

    public void saveUserId(int userId) {
        editor.putInt(USER_ID, userId).apply();
    }

    public int getUserId() {
        return sp.getInt(USER_ID, 0);
    }

    public void clear() {
        editor.putString(ACCESS_TOKEN, "").apply();
        editor.putInt(USER_ID, -1).apply();
    }
}

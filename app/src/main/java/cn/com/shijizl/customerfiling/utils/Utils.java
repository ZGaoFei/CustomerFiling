package cn.com.shijizl.customerfiling.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.com.shijizl.customerfiling.base.App;


public class Utils {
    public static final String TAG = Utils.class.getSimpleName();

    private static long lastClickTime;

    private Utils() {
    }

    /**
     * 判断手机号码格式是否正确
     *
     * @param phoneNumber
     * @return
     */
    public static boolean validateMobileNumber(String phoneNumber) {
        Pattern pattern = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(17[0-9])|(18[0,0-9]))\\d{8}$");
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    /**
     * md5加密
     */
    public static String encryption(String plainText) {
        String re_md5 = new String();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            re_md5 = buf.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return re_md5;
    }

    /**
     * 随机生成字符串
     *
     * @return
     */
    public static String getRandomString() {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < base.length(); i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 获取设备唯一标识码
     *
     * @return
     */
    public static String getDeviceId() {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) App.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
            return telephonyManager.getDeviceId();
        } catch (SecurityException e) {
            e.printStackTrace();
            return "device_no_permission";
        }

    }

    /**
     * 获取设备类别
     *
     * @return
     */
    public static String getDeviceModel() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        model = model.replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]", "");
        if (model.startsWith(manufacturer)) {
            final String capitalized = capitalize(model);
            return capitalized.replaceAll(" ", "_");
        } else {
            final String assembled = capitalize(manufacturer) + "_" + model;
            return assembled.replaceAll(" ", "_");
        }

    }

    /**
     * 系统版本号
     *
     * @return
     */
    public static String getSystemVersion() {
        return String.valueOf(Build.VERSION.SDK_INT);
    }

    /**
     * 系统版本名称
     *
     * @return
     */
    public static String getSystemName() {
        return String.valueOf(Build.VERSION.RELEASE);
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    /**
     * 获取版本名称
     *
     * @return
     */
    public static String getVersionName() {
        try {
            return App.getInstance().getPackageManager()
                    .getPackageInfo(App.getInstance().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("read version name failed");
        }
    }

    /**
     * 获取版本号
     *
     * @return
     */
    public static int getVersionCode() {
        try {
            return App.getInstance().getPackageManager()
                    .getPackageInfo(App.getInstance().getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("read version code failed");
        }
    }

    /**
     * 判断网络是否可用
     *
     * @return
     */
    public static boolean isNetworkOn() {
        ConnectivityManager connMgr = (ConnectivityManager)
                App.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifi != null && wifi.isAvailable() && wifi.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
            return true;
        }

        NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobile != null && mobile.isAvailable() && mobile.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否是WiFi网络
     */
    public static boolean isWifi() {
        ConnectivityManager connMgr = (ConnectivityManager)
                App.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifi != null
                && wifi.isAvailable()
                && wifi.getDetailedState() == NetworkInfo.DetailedState.CONNECTED;
    }

    public static String fileNameFromPath(String path) {
        if (path == null) {
            // the caller should not give null
            // Log.w(TAG, "null in getParentPath");
            return null;
        }
        return path.substring(path.lastIndexOf("/") + 1);
    }

    /**
     * 获取当前系统时间，以毫秒形式显示
     *
     * @return
     */
    public static long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }

    /**
     * 获取时间戳
     *
     * @return
     */
    public static String getTimeStamp() {
        return Long.toString(getCurrentTimeMillis() / 1000);
    }

    /**
     * 格式化时间
     * @param time
     * @return
     */
    public static String paseTime(long time) {
        SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd");
        return formater.format(time);
    }

    public static String paseTime(Date time) {
        SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd");
        return formater.format(time);
    }

}

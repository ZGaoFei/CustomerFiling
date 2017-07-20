package cn.com.shijizl.customerfiling.base;

import android.app.Activity;
import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import cn.com.shijizl.customerfiling.net.RetrofitHelper;

public class App extends Application {
    private static App instance;

    private List<Activity> list = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        RetrofitHelper.init();
    }

    public static App getInstance() {
        return instance;
    }

    public void addActivity(Activity activity) {
        list.add(activity);
    }

    public void deleteActivity(Activity activity) {
        if (activity != null) {
            list.remove(activity);
        }
    }

    public void exit() {
        for (Activity activity : list) {
            if (activity != null) {
                activity.finish();
            }
        }

        System.exit(0);
    }

}

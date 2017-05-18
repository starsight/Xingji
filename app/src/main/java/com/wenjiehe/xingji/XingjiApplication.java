package com.wenjiehe.xingji;

import android.app.Application;

import com.wenjiehe.xingji.Activity.MainActivity;

/**
 * Created by Administrator on 2017/5/18.
 */

public class XingjiApplication extends Application {
    private static MainActivity mainActivity = null;

    private static XingjiApplication mInstance;

    public static XingjiApplication instance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        exit();
    }

    public void setMainActivity(MainActivity activity) {
        mainActivity = activity;
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void exit() {
        if (mainActivity != null) {
            mainActivity.finish();
        }
        System.exit(0);
    }

}

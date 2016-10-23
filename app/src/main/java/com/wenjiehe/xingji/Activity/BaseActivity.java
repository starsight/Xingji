package com.wenjiehe.xingji.Activity;

/**
 * Created by wenjie on 16/07/22.
 */

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;

import com.avos.avoscloud.RefreshCallback;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.wenjiehe.xingji.R;
import com.wenjiehe.xingji.im.MessageHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;

public class BaseActivity extends Activity {

    public BaseActivity activity;
    private String userId, userName;

    private int signNum = 0;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AVOSCloud.initialize(this,
                "Hncn0N117OUU0Cxc46FvOrUb-9Nh9j0Va",
                "HE1bjVngkRCnP03lK3lB6LSK");
        //注册默认的消息处理逻辑
        AVIMMessageManager.registerMessageHandler(AVIMTypedMessage.class, new MessageHandler(this));
        //AVIMMessageManager.registerDefaultMessageHandler(new CustomMessageHandler());
        //AVAnalytics.trackAppOpened(getIntent());
        activity = this;
        userId = null;
        AVUser currentUser = AVUser.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getObjectId();
            userName = currentUser.getUsername();
            if (currentUser.get("signnum") != null)
                signNum = (Integer) currentUser.get("signnum");

        }

    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public int getSignNum() {
        return signNum;
    }

    protected void showError(String errorMessage) {
        showError(errorMessage, activity);
    }

    public void showError(String errorMessage, Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle(
                        activity.getResources().getString(
                                R.string.dialog_message_title))
                .setMessage(errorMessage)
                .setNegativeButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).show();
    }

    protected void onPause() {
        super.onPause();
        //AVAnalytics.onPause(this);
    }

    protected void onResume() {
        super.onResume();
        //AVAnalytics.onResume(this);
    }


}


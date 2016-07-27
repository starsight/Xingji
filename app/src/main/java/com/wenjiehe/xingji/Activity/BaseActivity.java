package com.wenjiehe.xingji.Activity;

/**
 * Created by wenjie on 16/07/22.
 */

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;
import com.wenjiehe.xingji.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

public class BaseActivity extends Activity {

    public BaseActivity activity;
    private String userId,userName;

    private int signNum =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AVOSCloud.initialize(this,
                "Hncn0N117OUU0Cxc46FvOrUb-9Nh9j0Va",
                "HE1bjVngkRCnP03lK3lB6LSK");

        AVAnalytics.trackAppOpened(getIntent());
        activity = this;
        userId = null;
        AVUser currentUser = AVUser.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getObjectId();
            userName = currentUser.getUsername();
            //signNum = (Integer)currentUser.get("signnum");
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
        AVAnalytics.onPause(this);
    }

    protected void onResume() {
        super.onResume();
        AVAnalytics.onResume(this);
    }
}

